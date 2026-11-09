'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { api } from '@/lib/api';
import { Alerta, Lote, UserSession, ReporteZootecnico } from '@/lib/types';

export default function AdminDashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserSession | null>(null);
  const [lotes, setLotes] = useState<Lote[]>([]);
  const [alertas, setAlertas] = useState<Alerta[]>([]);
  const [reportes, setReportes] = useState<{ [loteId: string]: ReporteZootecnico }>({});
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const currentUser = api.getCurrentUser();
    if (!currentUser || currentUser.rol !== 'ROLE_ADMIN') {
      router.push('/login');
      return;
    }
    setUser(currentUser);
    cargarDatos();
  }, [router]);

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const resGranjas = await fetch('http://localhost:8080/api/v1/granjas', {
        headers: { Authorization: `Bearer ${localStorage.getItem('avicola_token')}` },
      });
      if (resGranjas.ok) {
        const granjas = await resGranjas.json();
        if (granjas.length > 0) {
          const resGalpones = await api.getGalpones(granjas[0].id);
          if (resGalpones.length > 0) {
            const lotesCargados = await api.getLotesPorGalpon(resGalpones[0].id);
            setLotes(lotesCargados);

            // Cargar reportes para cada lote activo
            const reportesMap: { [loteId: string]: ReporteZootecnico } = {};
            for (const lote of lotesCargados) {
              try {
                const rep = await api.getReporteLote(lote.id);
                reportesMap[lote.id] = rep;
              } catch (e) {
                // Si aún no tiene registros
              }
            }
            setReportes(reportesMap);
          }
        }
      }

      const alertasActivas = await api.getAlertasActivas();
      setAlertas(alertasActivas);
    } catch (e) {
      console.error('Error cargando datos del dashboard:', e);
    } finally {
      setLoading(false);
    }
  };

  const handleMarcarRevisada = async (alertaId: string) => {
    try {
      await api.marcarAlertaRevisada(alertaId);
      setAlertas((prev) => prev.filter((a) => a.id !== alertaId));
    } catch (e) {
      alert('Error al auditar la alerta');
    }
  };

  const handleLogout = () => {
    api.logout();
    router.push('/login');
  };

  const totalAvesVivas = lotes.reduce((acc, l) => acc + l.cantidadActualVivas, 0);
  const totalAvesIniciales = lotes.reduce((acc, l) => acc + l.cantidadInicialAves, 0);
  const alertasCriticas = alertas.filter((a) => a.nivelSeveridad === 'CRITICO').length;

  return (
    <div className="min-h-screen bg-slate-100">
      {/* Navegación Superior */}
      <nav className="bg-slate-900 text-white shadow-lg sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">
          <div className="flex items-center space-x-3">
            <span className="text-3xl">🐔</span>
            <div>
              <span className="text-lg font-black tracking-tight text-white">AvícolaTrack</span>
              <span className="ml-2 text-xs font-semibold px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                Panel Administrador
              </span>
            </div>
          </div>

          <div className="flex items-center space-x-6 text-sm font-semibold">
            <Link href="/admin/dashboard" className="text-emerald-400 border-b-2 border-emerald-400 pb-1">
              Dashboard
            </Link>
            <Link href="/admin/galpones" className="text-slate-300 hover:text-white transition">
              Galpones y Lotes
            </Link>
            <Link href="/admin/alertas" className="text-slate-300 hover:text-white transition flex items-center space-x-1">
              <span>Alertas</span>
              {alertas.length > 0 && (
                <span className="bg-red-600 text-white text-[10px] px-1.5 py-0.2 rounded-full">
                  {alertas.length}
                </span>
              )}
            </Link>
            <Link href="/admin/reportes" className="text-slate-300 hover:text-white transition">
              Reportes
            </Link>
            <button
              onClick={handleLogout}
              className="rounded-lg bg-red-600/80 px-3 py-1.5 text-xs font-bold text-white hover:bg-red-600 transition"
            >
              Salir
            </button>
          </div>
        </div>
      </nav>

      {/* Contenido Principal */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Cabecera Informativa */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
          <div>
            <h1 className="text-2xl font-black text-slate-900">
              Granja Avícola El Porvenir
            </h1>
            <p className="text-sm text-slate-500 mt-1">
              Monitoreo Zootécnico en Tiempo Real — Vereda La Alianza, Pasto, Nariño (2026)
            </p>
          </div>
          <div className="flex items-center space-x-3">
            <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
              🟢 Sistema Normal y Sincronizado
            </span>
            <button
              onClick={cargarDatos}
              className="rounded-lg bg-slate-100 px-3 py-1.5 text-xs font-bold text-slate-700 hover:bg-slate-200 transition"
            >
              🔄 Actualizar
            </button>
          </div>
        </div>

        {/* Tarjetas KPI */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-200">
            <p className="text-xs font-bold uppercase text-slate-400">Población Viva Actual</p>
            <p className="text-3xl font-black text-slate-900 mt-2">
              {totalAvesVivas.toLocaleString()}
            </p>
            <p className="text-xs text-slate-500 mt-1">
              De {totalAvesIniciales.toLocaleString()} encasetadas ({((totalAvesVivas/totalAvesIniciales)*100 || 100).toFixed(1)}%)
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-200">
            <p className="text-xs font-bold uppercase text-slate-400">Alertas Críticas Activas</p>
            <p className={`text-3xl font-black mt-2 ${alertasCriticas > 0 ? 'text-red-600' : 'text-emerald-600'}`}>
              {alertasCriticas}
            </p>
            <p className="text-xs text-slate-500 mt-1">
              {alertas.length} alertas operativas totales
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-200">
            <p className="text-xs font-bold uppercase text-slate-400">Galpón 1 — Lotes Activos</p>
            <p className="text-3xl font-black text-blue-600 mt-2">
              {lotes.length} / 4
            </p>
            <p className="text-xs text-slate-500 mt-1">
              Esquema cuadrantes (Lotes 1 a 4)
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-200">
            <p className="text-xs font-bold uppercase text-slate-400">Días de Vida Promedio</p>
            <p className="text-3xl font-black text-slate-900 mt-2">
              {lotes[0]?.diasDeVida || 21} días
            </p>
            <p className="text-xs text-slate-500 mt-1">
              Estirpe genética: Cobb 500
            </p>
          </div>
        </div>

        {/* Bandeja de Alertas Operativas */}
        {alertas.length > 0 && (
          <section className="bg-white rounded-2xl p-6 shadow-sm border border-red-200">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-red-900 flex items-center space-x-2">
                <span>🚨</span>
                <span>Bandeja de Alertas Tempranas Pendientes</span>
              </h2>
              <span className="text-xs font-bold bg-red-100 text-red-800 px-2.5 py-1 rounded-full">
                {alertas.length} por atender
              </span>
            </div>

            <div className="space-y-3">
              {alertas.map((alerta) => (
                <div
                  key={alerta.id}
                  className={`p-4 rounded-xl border flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 ${
                    alerta.nivelSeveridad === 'CRITICO'
                      ? 'bg-red-50/60 border-red-200'
                      : 'bg-amber-50/60 border-amber-200'
                  }`}
                >
                  <div>
                    <div className="flex items-center space-x-2">
                      <span
                        className={`text-[10px] font-black uppercase px-2 py-0.5 rounded ${
                          alerta.nivelSeveridad === 'CRITICO'
                            ? 'bg-red-600 text-white'
                            : 'bg-amber-500 text-white'
                        }`}
                      >
                        {alerta.nivelSeveridad}
                      </span>
                      <span className="text-xs font-bold text-slate-700">
                        Galpón {alerta.numeroGalpon} — Lote {alerta.posicionEnGalpon} ({alerta.codigoLote})
                      </span>
                      <span className="text-xs text-slate-400">
                        {new Date(alerta.fechaHoraAlerta).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      </span>
                    </div>
                    <p className="text-sm font-semibold text-slate-900 mt-1">{alerta.mensaje}</p>
                  </div>
                  <button
                    onClick={() => handleMarcarRevisada(alerta.id)}
                    className="self-start sm:self-auto rounded-lg bg-white border border-slate-300 px-3 py-1.5 text-xs font-bold text-slate-700 hover:bg-slate-50 transition shadow-sm"
                  >
                    ✓ Marcar Atendida
                  </button>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Monitoreo por Cuadrantes (Los 4 lotes del Galpón) */}
        <section className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-lg font-bold text-slate-900">
                Galpón 1: Estado de los 4 Lotes Independientes
              </h2>
              <p className="text-xs text-slate-500">
                Supervisión individualizada por cada cuadrante operativo
              </p>
            </div>
            <Link
              href="/galponero/registro"
              className="rounded-lg bg-emerald-600 px-3 py-1.5 text-xs font-bold text-white hover:bg-emerald-700 transition"
            >
              + Ingresar Registro de Campo
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {lotes.map((lote) => {
              const rep = reportes[lote.id];
              return (
                <div
                  key={lote.id}
                  className="rounded-xl border border-slate-200 bg-slate-50/50 p-4 space-y-3"
                >
                  <div className="flex items-center justify-between border-b border-slate-200 pb-2">
                    <span className="text-xs font-black uppercase text-emerald-800">
                      Cuadrante {lote.posicionEnGalpon}
                    </span>
                    <span className="text-[10px] font-bold text-slate-500">
                      {lote.codigoLote}
                    </span>
                  </div>

                  <div className="space-y-1.5 text-xs">
                    <div className="flex justify-between">
                      <span className="text-slate-500">Población Viva:</span>
                      <strong className="text-slate-900 font-bold">{lote.cantidadActualVivas}</strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-500">Mortalidad Acum.:</span>
                      <strong className="text-slate-900">
                        {rep ? `${rep.tasaMortalidadAcumulada}% (${rep.totalBajas})` : '0.0%'}
                      </strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-500">Alimento Acumulado:</span>
                      <strong className="text-slate-900">
                        {rep ? `${rep.totalAlimentoKg} kg` : '0.0 kg'}
                      </strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-500">Peso Promedio:</span>
                      <strong className="text-blue-700 font-bold">
                        {rep ? `${rep.pesoPromedioActualG} g` : `${lote.pesoInicialPromedioG} g`}
                      </strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-500">Conversión (ICA):</span>
                      <strong className="text-emerald-700 font-bold">
                        {rep && rep.indiceConversionAlimenticia > 0 ? rep.indiceConversionAlimenticia : 'N/A'}
                      </strong>
                    </div>
                  </div>

                  <div className="pt-2">
                    <Link
                      href={`/admin/reportes?loteId=${lote.id}`}
                      className="block text-center text-xs font-bold text-emerald-600 hover:text-emerald-700 bg-white border border-emerald-200 py-1.5 rounded-lg transition"
                    >
                      Ver Detalle Completo →
                    </Link>
                  </div>
                </div>
              );
            })}
          </div>
        </section>
      </main>
    </div>
  );
}
