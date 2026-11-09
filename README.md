# Sistema de Automatización Avícola para Variables Productivas

> **Trabajo de Grado — Ingeniería de Software (2026)**  
> **Autor:** David Mateo Guerrero  
> **Institución:** Universidad Cooperativa de Colombia — Sede Pasto, Nariño  
> **Patrón Arquitectónico:** Monolito Modular (Modular Monolith)  
> **Ecosistema Tecnológico:** Spring Boot 3 (Java 21) + Next.js 14 (TypeScript / Tailwind CSS) + PostgreSQL 15 + Docker Compose  

---

## 📋 Descripción del Sistema

**AvícolaTrack** es una plataforma de software empresarial diseñada para estandarizar, automatizar y monitorear en tiempo real las variables zootécnicas y productivas en granjas avícolas comerciales de pequeña y mediana escala. 

El sistema sustituye las tradicionales planillas en papel y cuadernos rústicos de campo por una interfaz web/móvil táctil de captura rápida y un motor analítico centralizado que evalúa automáticamente la mortalidad diaria, el consumo de alimento balanceado, la ingesta hídrica, la ganancia de peso y el Índice de Conversión Alimenticia ($ICA$), generando alertas tempranas para salvaguardar la rentabilidad y bioseguridad del lote.

---

## 🏗️ Arquitectura de Monolito Modular

A diferencia de arquitecturas distribuidas complejas (microservicios), el sistema se estructura bajo el patrón de **Monolito Modular**, garantizando:
- **Cero latencia de red interna:** Comunicación directa en memoria entre módulos de dominio.
- **Transaccionalidad ACID pura:** Persistencia relacional en PostgreSQL que asegura descuentos atómicos de población viva ante registros de bajas.
- **Mantenibilidad para desarrollador único:** Menor carga cognitiva y despliegue unificado mediante un único contenedor backend.

```text
com.avicola.monolith/
├── iam/                  # Seguridad, RBAC y emisión de tokens JWT
├── infraestructura/      # Gestión de Granjas y Galpones
├── lotes/                # Control de ocupación (Regla: Máximo 4 lotes por galpón)
├── operacion/            # Capturas diarias de campo y pesajes muestrales
├── zootecnia/            # Motor de cálculo matemático (ICA, GDP, CDA)
└── alertas/              # Motor de inferencia y disparo de alertas operativas
```

---

## ⚙️ Reglas de Negocio Implementadas

1. **Esquema Multi-Lote por Galpón:** Cada galpón físico aloja hasta **cuatro (4) lotes independientes** (posiciones o cuadrantes 1 al 4), permitiendo trazabilidad individual por lote.
2. **Control de Acceso Basado en Roles (RBAC):**
   - `ROLE_GALPONERO`: Acceso a la interfaz móvil táctil de campo para el registro rápido de bajas diarias, alimento y agua por lote.
   - `ROLE_ADMIN`: Acceso al dashboard ejecutivo, apertura y cierre de lotes, auditoría de alertas y reportes consolidados.
3. **Cálculos Zootécnicos Estandarizados:**
   - **Consumo Diario por Ave ($CDA$):** $(\text{Alimento kg} \times 1000) / \text{Aves Vivas}$.
   - **Tasa de Mortalidad Diaria:** $(\text{Bajas} / \text{Aves Vivas al inicio}) \times 100$.
   - **Ganancia Diaria de Peso ($GDP$):** $(\text{Peso Actual} - \text{Peso Anterior}) / \text{Días}$.
   - **Índice de Conversión Alimenticia ($ICA$):** $\text{Total Alimento Acumulado (kg)} / \text{Biomasa Producida (kg)}$.
4. **Motor de Alertas Tempranas:**
   - 🚨 **Alerta Crítica de Mortalidad:** Se dispara si la tasa diaria en un lote es $\ge 0.50\%$.
   - 🚨 **Alerta Crítica de Agua:** Se dispara ante una caída $> 20\%$ en el consumo hídrico respecto al día anterior.
   - ⚠️ **Alerta de Relación Hídrica:** Se dispara si la relación agua/alimento supera 2.6 L/kg o desciende de 1.4 L/kg.
   - 🚨 **Alerta de Conversión Deficiente:** Se dispara si en semana 5 el $ICA > 1.80$.

---

## 📁 Estructura del Monorepo

```text
.
├── backend-monolith/                   # Backend Spring Boot 3 (Java 21)
│   ├── src/main/java/com/avicola/monolith/
│   ├── src/main/resources/application.properties
│   ├── src/main/resources/db/migration/V1__init_schema.sql
│   ├── src/test/java/com/avicola/monolith/
│   ├── Dockerfile
│   └── pom.xml
├── frontend-nextjs/                    # Frontend Next.js 14 (TypeScript + Tailwind)
│   ├── src/app/
│   │   ├── admin/dashboard/page.tsx    # Dashboard ejecutivo
│   │   ├── admin/galpones/page.tsx     # Gestión de galpones y lotes
│   │   ├── admin/alertas/page.tsx      # Bandeja de alertas zootécnicas
│   │   ├── admin/reportes/page.tsx     # Analítica e historial
│   │   ├── galponero/registro/page.tsx # Captura táctil móvil para operario
│   │   └── login/page.tsx              # Autenticación con roles
│   ├── src/lib/api.ts                  # Cliente API HTTP
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml                  # Orquestación multicontenedor
├── .env.example                        # Variables de entorno modelo
├── .env                                # Configuración local activa
└── README.md                           # Documentación técnica
```

---

## 🚀 Puesta en Marcha Rápida (Docker Compose)

### Requisitos Previos
- Docker Engine 24.0+ y Docker Compose v2+.

### Instrucciones de Despliegue
```bash
# 1. Clonar el repositorio
git clone https://github.com/mateoguerrero/sistema-automatizacion-avicola.git
cd sistema-automatizacion-avicola

# 2. Levantar los tres contenedores (PostgreSQL, Spring Boot y Next.js)
docker compose up -d --build

# 3. Comprobar estado de los contenedores
docker compose ps
```

### URLs de Acceso
- **Frontend Web / Móvil:** `http://localhost:3000`
- **Backend API REST:** `http://localhost:8080/api/v1`
- **Documentación Swagger / OpenAPI:** `http://localhost:8080/swagger-ui.html`
- **Base de Datos PostgreSQL:** `localhost:5432` (`avicola_db`)

---

## 👤 Credenciales Iniciales de Prueba (Semilla Automática)

El sistema inicializa automáticamente la base de datos con una granja piloto (*Granja Avícola El Porvenir*), un galpón con sus 4 lotes activos y los siguientes usuarios:

| Rol | Correo Electrónico | Contraseña | Vistas y Permisos |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@avicola.com` | `admin123` | Dashboard global, apertura de lotes, auditoría de alertas y reportes completos. |
| **Galponero** | `galponero@avicola.com` | `galponero123` | Vista móvil simplificada con selector de cuadrantes (Lotes 1 a 4) para registro de bajas y raciones. |

---

## 🧪 Ejecución de Pruebas Automatizadas

El backend incluye una suite exhaustiva de pruebas unitarias sobre los algoritmos zootécnicos, el motor de alertas y la regla de capacidad máxima de 4 lotes:

```bash
cd backend-monolith
mvn clean test
```

Resultados esperados:
- `CalculoZootecnicoServiceTest`: 4 pruebas (100% éxito en fórmulas de ICA, GDP, CDA y mortalidad).
- `AlertEngineServiceTest`: 2 pruebas (validación de disparo de alertas críticas y advertencias).
- `LoteServiceTest`: 1 prueba (verificación del rechazo ante un 5to lote activo).
- **Total: 7 pruebas ejecutadas, 0 fallos, 0 errores.**

---

## 📄 Licencia y Derechos Académicos
Desarrollado por **David Mateo Guerrero** como requisito de grado para optar al título de **Ingeniero de Software** en la **Universidad Cooperativa de Colombia (Sede Pasto, Nariño, 2026)**.
