import { UserSession, Lote, Galpon, RegistroDiarioRequest, BiometriaRequest, Alerta, ReporteZootecnico } from './types';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

function getHeaders(): HeadersInit {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('avicola_token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  }
  return headers;
}

export const api = {
  // Auth
  async login(email: string, password: string): Promise<UserSession> {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Error al iniciar sesión');
    }
    const data: UserSession = await res.json();
    if (typeof window !== 'undefined') {
      localStorage.setItem('avicola_token', data.token);
      localStorage.setItem('avicola_user', JSON.stringify(data));
    }
    return data;
  },

  logout() {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('avicola_token');
      localStorage.removeItem('avicola_user');
    }
  },

  getCurrentUser(): UserSession | null {
    if (typeof window !== 'undefined') {
      const raw = localStorage.getItem('avicola_user');
      return raw ? JSON.parse(raw) : null;
    }
    return null;
  },

  // Galpones y Lotes
  async getGalpones(granjaId: string): Promise<Galpon[]> {
    const res = await fetch(`${API_BASE}/granjas/${granjaId}/galpones`, { headers: getHeaders() });
    if (!res.ok) throw new Error('Error al consultar galpones');
    return res.json();
  },

  async getLotesPorGalpon(galponId: string): Promise<Lote[]> {
    const res = await fetch(`${API_BASE}/galpones/${galponId}/lotes?soloActivos=true`, { headers: getHeaders() });
    if (!res.ok) throw new Error('Error al consultar lotes del galpón');
    return res.json();
  },

  async crearLote(galponId: string, data: any): Promise<Lote> {
    const res = await fetch(`${API_BASE}/galpones/${galponId}/lotes`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Error al crear lote');
    }
    return res.json();
  },

  // Registro Operativo
  async registrarDiario(data: RegistroDiarioRequest): Promise<any> {
    const res = await fetch(`${API_BASE}/operacion/diario`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Error al guardar el registro diario');
    }
    return res.json();
  },

  async registrarBiometria(data: BiometriaRequest): Promise<any> {
    const res = await fetch(`${API_BASE}/operacion/biometria`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Error al registrar pesaje');
    }
    return res.json();
  },

  // Alertas
  async getAlertasActivas(): Promise<Alerta[]> {
    const res = await fetch(`${API_BASE}/alertas/activas`, { headers: getHeaders() });
    if (!res.ok) throw new Error('Error al consultar alertas');
    return res.json();
  },

  async marcarAlertaRevisada(alertaId: string): Promise<Alerta> {
    const res = await fetch(`${API_BASE}/alertas/${alertaId}/revisar`, {
      method: 'PATCH',
      headers: getHeaders(),
    });
    if (!res.ok) throw new Error('Error al auditar alerta');
    return res.json();
  },

  // Zootecnia
  async getReporteLote(loteId: string): Promise<ReporteZootecnico> {
    const res = await fetch(`${API_BASE}/zootecnia/lote/${loteId}/reporte`, { headers: getHeaders() });
    if (!res.ok) throw new Error('Error al obtener reporte zootécnico');
    return res.json();
  },
};
