-- SCRIPT DDL: SISTEMA DE AUTOMATIZACIÓN AVÍCOLA PARA VARIABLES PRODUCTIVAS
-- POSTGRESQL 15+

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. SEGURIDAD Y USUARIOS (RBAC)
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_completo VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL CHECK (rol IN ('ROLE_ADMIN', 'ROLE_GALPONERO')),
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. INFRAESTRUCTURA: GRANJAS Y GALPONES
CREATE TABLE IF NOT EXISTS granjas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(200) NOT NULL,
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS galpones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    granja_id UUID NOT NULL REFERENCES granjas(id) ON DELETE CASCADE,
    numero_galpon INT NOT NULL,
    capacidad_total_aves INT NOT NULL CHECK (capacidad_total_aves > 0),
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_granja_galpon UNIQUE (granja_id, numero_galpon)
);

-- 3. GESTION DE LOTES (REGLA DE CAPACIDAD: HASTA 4 LOTES POR GALPON)
CREATE TABLE IF NOT EXISTS lotes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    galpon_id UUID NOT NULL REFERENCES galpones(id) ON DELETE RESTRICT,
    codigo_lote VARCHAR(50) NOT NULL UNIQUE,
    posicion_en_galpon INT NOT NULL CHECK (posicion_en_galpon BETWEEN 1 AND 4),
    linea_genetica VARCHAR(50) NOT NULL DEFAULT 'Cobb 500',
    fecha_encasetamiento DATE NOT NULL,
    cantidad_inicial_aves INT NOT NULL CHECK (cantidad_inicial_aves > 0),
    cantidad_actual_vivas INT NOT NULL CHECK (cantidad_actual_vivas >= 0),
    peso_inicial_promedio_g NUMERIC(6,2) NOT NULL CHECK (peso_inicial_promedio_g > 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'CERRADO')),
    creado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_galpon_posicion_activo UNIQUE (galpon_id, posicion_en_galpon, estado)
);

-- 4. REGISTROS OPERATIVOS DIARIOS (AGUA, ALIMENTO, MORTALIDAD)
CREATE TABLE IF NOT EXISTS registros_diarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lote_id UUID NOT NULL REFERENCES lotes(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    fecha_registro DATE NOT NULL,
    dia_vida INT NOT NULL CHECK (dia_vida >= 1),
    aves_muertas INT NOT NULL DEFAULT 0 CHECK (aves_muertas >= 0),
    alimento_suministrado_kg NUMERIC(8,2) NOT NULL DEFAULT 0.0 CHECK (alimento_suministrado_kg >= 0.0),
    agua_consumida_litros NUMERIC(8,2) NOT NULL DEFAULT 0.0 CHECK (agua_consumida_litros >= 0.0),
    observaciones TEXT,
    registrado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_lote_fecha_registro UNIQUE (lote_id, fecha_registro)
);

-- 5. BIOMETRIAS DE PESO (PESAJES PERIODICOS)
CREATE TABLE IF NOT EXISTS biometrias_peso (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lote_id UUID NOT NULL REFERENCES lotes(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    fecha_pesaje DATE NOT NULL,
    dia_vida INT NOT NULL CHECK (dia_vida >= 1),
    peso_promedio_g NUMERIC(7,2) NOT NULL CHECK (peso_promedio_g > 0),
    ganancia_diaria_peso_g NUMERIC(6,2),
    tamano_muestra_aves INT NOT NULL CHECK (tamano_muestra_aves > 0),
    registrado_en TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. MOTOR DE ALERTAS TEMPRANAS
CREATE TABLE IF NOT EXISTS alertas_lote (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lote_id UUID NOT NULL REFERENCES lotes(id) ON DELETE CASCADE,
    tipo_alerta VARCHAR(50) NOT NULL CHECK (tipo_alerta IN ('MORTALIDAD_ALTA', 'CONSUMO_ALIMENTO_ANORMAL', 'CONSUMO_AGUA_CRITICO', 'ICA_DEFICIENTE', 'PESO_BAJO')),
    nivel_severidad VARCHAR(20) NOT NULL CHECK (nivel_severidad IN ('INFORMATIVO', 'ADVERTENCIA', 'CRITICO')),
    mensaje TEXT NOT NULL,
    valor_registrado NUMERIC(10,2) NOT NULL,
    valor_umbral_esperado NUMERIC(10,2) NOT NULL,
    revisada BOOLEAN DEFAULT FALSE,
    fecha_hora_alerta TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- INDICES PARA CONSULTAS DE RENDIMIENTO
CREATE INDEX IF NOT EXISTS idx_registros_lote_fecha ON registros_diarios(lote_id, fecha_registro);
CREATE INDEX IF NOT EXISTS idx_biometrias_lote_fecha ON biometrias_peso(lote_id, fecha_pesaje);
CREATE INDEX IF NOT EXISTS idx_alertas_lote_no_revisadas ON alertas_lote(lote_id, revisada) WHERE revisada = FALSE;
