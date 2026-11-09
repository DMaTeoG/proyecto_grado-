'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { api } from '@/lib/api';
import { Galpon, Lote } from '@/lib/types';

export default function AdminGalponesPage() {
  const [galpones, setGalpones] = useState<Galpon[]>([]);
  const [lotes, setLotes] = useState<Lote[]>([]);
  const [selectedGalpon, setSelectedGalpon] = useState<string>('');
  const [loading, setLoading] = useState(true);

  // Formulario nuevo lote
  const [showModal, setShowModal] = useState(false);
  const [posicion, setPosicion] = useState(1);
  const [codigoLote, setCodigoLote] = useState('');
  const [cantidadAves, setCantidadAves] = useState('3000');
  const [pesoInicial, setPesoInicial] = useState('42.0');
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const resGranjas = await fetch('http://localhost:8080/api/v1/granjas', {
        headers: { Authorization: `Bearer ${localStorage.getItem('avicola_token')}` },
      });
      if (resGranjas.ok) {
        const granjas = await resGranjas.json();
        if (granjas.length > 0) {
          const gals = await api.getGalpones(granjas[0].id);
          setGalpones(gals);
          if (gals.length > 0) {
            setSelectedGalpon(gals[0].id);
            const lts = await api.getLotesPorGalpon(gals[0].id);
            setLotes(lts);
          }
        }
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleCrearLote = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);
    try {
      await api.crearLote(selectedGalpon, {
        codigoLote,
        posicionEnGalpon: Number(posicion),
        lineaGenetica: 'Cobb 500',
        fechaEncasetamiento: new Date().toISOString().split('T')[0],
        cantidadInicialAves: Number(cantidadAves),
        pesoInicialPromedioG: Number(pesoInicial),
      });
      setShowModal(false);
      setCodigoLote('');
      // Recargar
      const lts = await api.getLotesPorGalpon(selectedGalpon);
      setLotes(lts);
    } catch (err: any) {
      setErrorMsg(err.message || 'Error al crear lote');
    }
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
            <Link href="/admin/galpones" className="text-emerald-400 border-b-2 border-emerald-400 pb-1">Galpones y Lotes</Link>
            <Link href="/admin/alertas" className="text-slate-300 hover:text-white transition">Alertas</Link>
            <Link href="/admin/reportes" className="text-slate-300 hover:text-white transition">Reportes</Link>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
          <div>
            <h1 className="text-2xl font-black text-slate-900">Gestión de Galpones y Lotes</h1>
            <p className="text-sm text-slate-500 mt-1">
              Esquema de control multi-lote: hasta 4 lotes independientes por galpón físico.
            </p>
          </div>
          <button
            onClick={() => setShowModal(true)}
            className="rounded-xl bg-emerald-600 px-4 py-2.5 text-sm font-bold text-white shadow hover:bg-emerald-700 transition"
          >
            + Abrir Nuevo Lote
          </button>
        </div>

        {/* Listado de Lotes del Galpón */}
        <section className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
          <h2 className="text-lg font-bold text-slate-900 mb-4">
            Galpón 1 — Lotes Activos ({lotes.length} / 4)
          </h2>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-700">
              <thead className="bg-slate-50 text-xs font-bold uppercase text-slate-500 border-b">
                <tr>
                  <th className="py-3 px-4">Cuadrante</th>
                  <th className="py-3 px-4">Código Lote</th>
                  <th className="py-3 px-4">Línea</th>
                  <th className="py-3 px-4">Aves Iniciales</th>
                  <th className="py-3 px-4">Aves Vivas</th>
                  <th className="py-3 px-4">Días Vida</th>
                  <th className="py-3 px-4">Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {lotes.map((lote) => (
                  <tr key={lote.id} className="hover:bg-slate-50">
                    <td className="py-3 px-4 font-bold text-slate-900">Posición {lote.posicionEnGalpon}</td>
                    <td className="py-3 px-4 font-mono font-semibold text-emerald-700">{lote.codigoLote}</td>
                    <td className="py-3 px-4">{lote.lineaGenetica}</td>
                    <td className="py-3 px-4">{lote.cantidadInicialAves.toLocaleString()}</td>
                    <td className="py-3 px-4 font-bold text-slate-900">{lote.cantidadActualVivas.toLocaleString()}</td>
                    <td className="py-3 px-4">{lote.diasDeVida} días</td>
                    <td className="py-3 px-4">
                      <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                        {lote.estado}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        {/* Modal Nuevo Lote */}
        {showModal && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
              <h3 className="text-lg font-bold text-slate-900">Apertura de Nuevo Lote</h3>
              {errorMsg && (
                <div className="p-3 bg-red-50 text-red-800 text-xs font-semibold rounded-lg border border-red-200">
                  {errorMsg}
                </div>
              )}
              <form onSubmit={handleCrearLote} className="space-y-3">
                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600">Código de Lote</label>
                  <input
                    type="text"
                    required
                    value={codigoLote}
                    onChange={(e) => setCodigoLote(e.target.value)}
                    placeholder="Ej. LOTE-G1-Q2-2026"
                    className="w-full mt-1 p-2.5 border rounded-lg text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600">Posición / Cuadrante (1 a 4)</label>
                  <select
                    value={posicion}
                    onChange={(e) => setPosicion(Number(e.target.value))}
                    className="w-full mt-1 p-2.5 border rounded-lg text-sm"
                  >
                    <option value={1}>Cuadrante 1</option>
                    <option value={2}>Cuadrante 2</option>
                    <option value={3}>Cuadrante 3</option>
                    <option value={4}>Cuadrante 4</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600">Cantidad de Aves</label>
                  <input
                    type="number"
                    required
                    value={cantidadAves}
                    onChange={(e) => setCantidadAves(e.target.value)}
                    className="w-full mt-1 p-2.5 border rounded-lg text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600">Peso Inicial Promedio (g)</label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    value={pesoInicial}
                    onChange={(e) => setPesoInicial(e.target.value)}
                    className="w-full mt-1 p-2.5 border rounded-lg text-sm"
                  />
                </div>
                <div className="flex justify-end space-x-2 pt-3">
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="px-4 py-2 border rounded-lg text-xs font-bold text-slate-600 hover:bg-slate-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-emerald-600 text-white rounded-lg text-xs font-bold hover:bg-emerald-700"
                  >
                    Crear Lote
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
