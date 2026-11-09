'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { api } from '@/lib/api';
import { Alerta } from '@/lib/types';

export default function AdminAlertasPage() {
  const [alertas, setAlertas] = useState<Alerta[]>([]);
  const [filtro, setFiltro] = useState<'TODAS' | 'CRITICO' | 'ADVERTENCIA'>('TODAS');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarAlertas();
  }, []);

  const cargarAlertas = async () => {
    setLoading(true);
    try {
      const data = await api.getAlertasActivas();
      setAlertas(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleMarcarRevisada = async (id: string) => {
    try {
      await api.marcarAlertaRevisada(id);
      setAlertas((prev) => prev.filter((a) => a.id !== id));
    } catch (e) {
      alert('Error al auditar la alerta');
    }
  };

  const alertasFiltradas = alertas.filter((a) => {
    if (filtro === 'TODAS') return true;
    return a.nivelSeveridad === filtro;
  });

  return (
    <div className="min-h-screen bg-slate-100">
      <nav className="bg-slate-900 text-white shadow-lg sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">
          <div className="flex items-center space-x-3">
            <span className="text-3xl">🐔</span>
            <span className="text-lg font-black tracking-tight text-white">AvícolaTrack</span>
          </div>
          <div className="flex items-center space-x-6 text-sm font-semibold">
            <Link href="/admin/dashboard" className="text-slate-300 hover:text-white transition">Dashboard</Link>
            <Link href="/admin/galpones" className="text-slate-300 hover:text-white transition">Galpones y Lotes</Link>
            <Link href="/admin/alertas" className="text-emerald-400 border-b-2 border-emerald-400 pb-1">Alertas</Link>
            <Link href="/admin/reportes" className="text-slate-300 hover:text-white transition">Reportes</Link>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
          <div>
            <h1 className="text-2xl font-black text-slate-900">Bandeja de Alertas Zootécnicas</h1>
            <p className="text-sm text-slate-500 mt-1">
              Detección automática de anomalías en mortalidad, agua, alimento y conversión alimenticia ($ICA$).
            </p>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setFiltro('TODAS')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition ${
                filtro === 'TODAS' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'
              }`}
            >
              Todas ({alertas.length})
            </button>
            <button
              onClick={() => setFiltro('CRITICO')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition ${
                filtro === 'CRITICO' ? 'bg-red-600 text-white' : 'bg-red-50 text-red-700'
              }`}
            >
              Críticas ({alertas.filter((a) => a.nivelSeveridad === 'CRITICO').length})
            </button>
            <button
              onClick={() => setFiltro('ADVERTENCIA')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition ${
                filtro === 'ADVERTENCIA' ? 'bg-amber-500 text-white' : 'bg-amber-50 text-amber-700'
              }`}
            >
              Advertencias ({alertas.filter((a) => a.nivelSeveridad === 'ADVERTENCIA').length})
            </button>
          </div>
        </div>

        {alertasFiltradas.length === 0 ? (
          <div className="bg-white rounded-2xl p-12 text-center border border-slate-200">
            <span className="text-4xl">🎉</span>
            <h3 className="text-lg font-bold text-slate-900 mt-3">Sin Alertas Pendientes</h3>
            <p className="text-sm text-slate-500 mt-1">
              Todos los lotes del galpón operan dentro de los umbrales zootécnicos esperados.
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {alertasFiltradas.map((alerta) => (
              <div
                key={alerta.id}
                className={`p-5 rounded-2xl border shadow-sm flex flex-col md:flex-row md:items-center md:justify-between gap-4 ${
                  alerta.nivelSeveridad === 'CRITICO' ? 'bg-red-50/70 border-red-200' : 'bg-amber-50/70 border-amber-200'
                }`}
              >
                <div className="space-y-1.5">
                  <div className="flex items-center space-x-2">
                    <span
                      className={`text-[10px] font-black uppercase px-2 py-0.5 rounded ${
                        alerta.nivelSeveridad === 'CRITICO' ? 'bg-red-600 text-white' : 'bg-amber-500 text-white'
                      }`}
                    >
                      {alerta.nivelSeveridad}
                    </span>
                    <span className="text-xs font-bold text-slate-800">
                      Tipo: {alerta.tipoAlerta}
                    </span>
                    <span className="text-xs text-slate-500">
                      • Galpón {alerta.numeroGalpon} — Lote {alerta.posicionEnGalpon} ({alerta.codigoLote})
                    </span>
                  </div>
                  <p className="text-base font-bold text-slate-900">{alerta.mensaje}</p>
                  <p className="text-xs text-slate-500">
                    Valor detectado: <strong>{alerta.valorRegistrado}</strong> | Umbral de referencia:{' '}
                    <strong>{alerta.valorUmbralEsperado}</strong>
                  </p>
                </div>
                <button
                  onClick={() => handleMarcarRevisada(alerta.id)}
                  className="rounded-xl bg-white border border-slate-300 px-4 py-2 text-xs font-bold text-slate-800 hover:bg-slate-50 transition shadow-sm whitespace-nowrap self-start md:self-auto"
                >
                  ✓ Marcar como Atendida
                </button>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
