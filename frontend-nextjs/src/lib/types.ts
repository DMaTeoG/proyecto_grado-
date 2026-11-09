export type Role = 'ROLE_ADMIN' | 'ROLE_GALPONERO';

export interface UserSession {
  token: string;
  id: string;
  nombreCompleto: string;
  email: string;
  rol: Role;
}

export interface Granja {
  id: string;
  nombre: string;
  ubicacion: string;
}

export interface Galpon {
  id: string;
  numeroGalpon: number;
  capacidadTotalAves: number;
  granja: Granja;
}

export interface Lote {
  id: string;
  galponId: string;
  numeroGalpon: number;
  codigoLote: string;
  posicionEnGalpon: number; // 1, 2, 3 o 4
  lineaGenetica: string;
  fechaEncasetamiento: string;
  diasDeVida: number;
  cantidadInicialAves: number;
  cantidadActualVivas: number;
  pesoInicialPromedioG: number;
  estado: 'ACTIVO' | 'CERRADO';
}

export interface RegistroDiarioRequest {
  loteId: string;
  fechaRegistro: string;
  diaVida: number;
  avesMuertas: number;
  alimentoSuministradoKg: number;
  aguaConsumidaLitros: number;
  observaciones?: string;
}

export interface BiometriaRequest {
  loteId: string;
  fechaPesaje: string;
  diaVida: number;
  pesoPromedioG: number;
  tamanoMuestraAves: number;
}

export interface Alerta {
  id: string;
  loteId: string;
  codigoLote: string;
  numeroGalpon: number;
  posicionEnGalpon: number;
  tipoAlerta: 'MORTALIDAD_ALTA' | 'CONSUMO_ALIMENTO_ANORMAL' | 'CONSUMO_AGUA_CRITICO' | 'ICA_DEFICIENTE' | 'PESO_BAJO';
  nivelSeveridad: 'INFORMATIVO' | 'ADVERTENCIA' | 'CRITICO';
  mensaje: string;
  valorRegistrado: number;
  valorUmbralEsperado: number;
  revisada: boolean;
  fechaHoraAlerta: string;
}

export interface ReporteZootecnico {
  loteId: string;
  codigoLote: string;
  galponNumero: number;
  posicionEnGalpon: number;
  avesIniciales: number;
  avesVivas: number;
  totalBajas: number;
  tasaMortalidadAcumulada: number;
  totalAlimentoKg: number;
  totalAguaLitros: number;
  relacionHidrica: number;
  pesoPromedioActualG: number;
  gananciaDiariaPesoG: number;
  indiceConversionAlimenticia: number;
}
