'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { api } from '@/lib/api';
import { Lote, ReporteZootecnico } from '@/lib/types';

export default function AdminReportesPage() {
  const [lotes, setLotes] = useState<Lote[]>([]);
  const [loteId, setLoteId] = useState<string>('');
  const [reporte, setReporte] = useState<ReporteZootecnico | null>(null);
  const [registros, setRegistros] = useState<any[]>([]);
  const [biometrias, setBiometrias] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarLotes();
  }, []);

  const cargarLotes = async () => {
    try {
      const resGranjas = await fetch('http://localhost:8080/api/v1/granjas', {
        headers: { Authorization: `Bearer ${localStorage.getItem('avicola_token')}` },
      });
      if (resGranjas.ok) {
        const granjas = await resGranjas.json();
        if (granjas.length > 0) {
          const gals = await api.getGalpones(granjas[0].id);
          if (gals.length > 0) {
            const lts = await api.getLotesPorGalpon(gals[0].id);
            setLotes(lts);
            if (lts.length > 0) {
              setLoteId(lts[0].id);
              cargarReporte(lts[0].id);
            }
          }
        }
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const cargarReporte = async (id: string) => {
    try {
      const rep = await api.getReporteLote(id);
      setReporte(rep);

      const resReg = await fetch(`http://localhost:8080/api/v1/operacion/lote/${id}/registros`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('avicola_token')}` },
      });
      if (resReg.ok) setRegistros(await resReg.json());

      const resBio = await fetch(`http://localhost:8080/api/v1/operacion/lote/${id}/biometrias`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('avicola_token')}` },
      });
      if (resBio.ok) setBiometrias(await resBio.json());
    } catch (e) {
      console.error(e);
    }
  };

  const handleCambioLote = (id: string) => {
    setLoteId(id);
    cargarReporte(id);
  };

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
            <Link href="/admin/alertas" className="text-slate-300 hover:text-white transition">Alertas</Link>
            <Link href="/admin/reportes" className="text-emerald-400 border-b-2 border-emerald-400 pb-1">Reportes</Link>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
          <div>
            <h1 className="text-2xl font-black text-slate-900">Analítica Zootécnica y Rendimiento</h1>
            <p className="text-sm text-slate-500 mt-1">
              Informe consolidado del Índice de Conversión ($ICA$), Ganancia Diaria ($GDP$) y curva de consumo.
            </p>
          </div>

          <div className="flex items-center space-x-3">
            <label className="text-xs font-bold uppercase text-slate-500">Seleccionar Lote:</label>
            <select
              value={loteId}
              onChange={(e) => handleCambioLote(e.target.value)}
              className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm font-bold text-slate-800 shadow-sm focus:border-emerald-500"
            >
              {lotes.map((l) => (
                <option key={l.id} value={l.id}>
                  Cuadrante {l.posicionEnGalpon} ({l.codigoLote})
                </option>
              ))}
            </select>
          </div>
        </div>

        {reporte && (
          <>
            {/* Grid de Métricas Principales */}
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <p className="text-[10px] font-bold uppercase text-slate-400">Conversión (ICA)</p>
                <p className="text-2xl font-black text-emerald-600 mt-1">
                  {reporte.indiceConversionAlimenticia > 0 ? reporte.indiceConversionAlimenticia : 'N/A'}
                </p>
                <p className="text-[10px] text-slate-500">Meta: 1.45 - 1.65</p>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <p className="text-[10px] font-bold uppercase text-slate-400">Peso Promedio</p>
                <p className="text-2xl font-black text-blue-600 mt-1">
                  {reporte.pesoPromedioActualG} g
                </p>
                <p className="text-[10px] text-slate-500">Última biometría</p>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <p className="text-[10px] font-bold uppercase text-slate-400">Ganancia Diaria (GDP)</p>
                <p className="text-2xl font-black text-slate-900 mt-1">
                  {reporte.gananciaDiariaPesoG} g/d
                </p>
                <p className="text-[10px] text-slate-500">Ritmo de engorde</p>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <p className="text-[10px] font-bold uppercase text-slate-400">Mortalidad Acum.</p>
                <p className="text-2xl font-black text-red-600 mt-1">
                  {reporte.tasaMortalidadAcumulada}%
                </p>
                <p className="text-[10px] text-slate-500">{reporte.totalBajas} aves muertas</p>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <p className="text-[10px] font-bold uppercase text-slate-400">Alimento Total</p>
                <p className="text-2xl font-black text-slate-900 mt-1">
                  {reporte.totalAlimentoKg} kg
                </p>
                <p className="text-[10px] text-slate-500">Concentrado</p>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <p className="text-[10px] font-bold uppercase text-slate-400">Relación Agua:Alimento</p>
                <p className="text-2xl font-black text-cyan-600 mt-1">
                  {reporte.relacionHidrica}
                </p>
                <p className="text-[10px] text-slate-500">Normal: 1.8 - 2.0 L/kg</p>
              </div>
            </div>

            {/* Tablas de Historial */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Registros Diarios */}
              <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
                <h3 className="text-base font-bold text-slate-900 mb-3">Historial de Registros Diarios</h3>
                <div className="max-h-80 overflow-y-auto">
                  <table className="w-full text-xs text-left">
                    <thead className="bg-slate-50 font-bold uppercase text-slate-500 sticky top-0 border-b">
                      <tr>
                        <th className="py-2 px-3">Fecha</th>
                        <th className="py-2 px-3">Día</th>
                        <th className="py-2 px-3">Muertas</th>
                        <th className="py-2 px-3">Alimento</th>
                        <th className="py-2 px-3">Agua</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {registros.map((r, i) => (
                        <tr key={i} className="hover:bg-slate-50">
                          <td className="py-2 px-3 font-semibold">{r.fechaRegistro}</td>
                          <td className="py-2 px-3">{r.diaVida}</td>
                          <td className="py-2 px-3 font-bold text-red-600">{r.avesMuertas}</td>
                          <td className="py-2 px-3">{r.alimentoSuministradoKg} kg</td>
                          <td className="py-2 px-3">{r.aguaConsumidaLitros} L</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Biometrías */}
              <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
                <h3 className="text-base font-bold text-slate-900 mb-3">Historial de Pesajes Muestrales</h3>
                <div className="max-h-80 overflow-y-auto">
                  <table className="w-full text-xs text-left">
                    <thead className="bg-slate-50 font-bold uppercase text-slate-500 sticky top-0 border-b">
                      <tr>
                        <th className="py-2 px-3">Fecha</th>
                        <th className="py-2 px-3">Día</th>
                        <th className="py-2 px-3">Peso Promedio</th>
                        <th className="py-2 px-3">GDP</th>
                        <th className="py-2 px-3">Muestra</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {biometrias.map((b, i) => (
                        <tr key={i} className="hover:bg-slate-50">
                          <td className="py-2 px-3 font-semibold">{b.fechaPesaje}</td>
                          <td className="py-2 px-3">{b.diaVida}</td>
                          <td className="py-2 px-3 font-bold text-blue-700">{b.pesoPromedioG} g</td>
                          <td className="py-2 px-3">{b.gananciaDiariaPesoG ? `${b.gananciaDiariaPesoG} g/d` : '-'}</td>
                          <td className="py-2 px-3">{b.tamanoMuestraAves} aves</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
