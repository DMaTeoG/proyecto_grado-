'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { Lote, UserSession } from '@/lib/types';

export default function RegistroGalponeroPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserSession | null>(null);
  const [lotes, setLotes] = useState<Lote[]>([]);
  const [loteSeleccionado, setLoteSeleccionado] = useState<Lote | null>(null);
  const [modo, setModo] = useState<'DIARIO' | 'BIOMETRIA'>('DIARIO');

  // Formulario Diario
  const [muertas, setMuertas] = useState<string>('0');
  const [alimentoKg, setAlimentoKg] = useState<string>('');
  const [aguaLitros, setAguaLitros] = useState<string>('');
  const [observaciones, setObservaciones] = useState<string>('');

  // Formulario Biometría
  const [pesoPromedioG, setPesoPromedioG] = useState<string>('');
  const [tamanoMuestra, setTamanoMuestra] = useState<string>('50');

  const [loading, setLoading] = useState<boolean>(false);
  const [mensaje, setMensaje] = useState<{ tipo: 'ok' | 'error'; texto: string } | null>(null);

  useEffect(() => {
    const currentUser = api.getCurrentUser();
    if (!currentUser) {
      router.push('/login');
      return;
    }
    setUser(currentUser);
    cargarLotes();
  }, [router]);

  const cargarLotes = async () => {
    try {
      // Cargar granjas y galpones iniciales
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
            if (lotesCargados.length > 0) {
              setLoteSeleccionado(lotesCargados[0]);
            }
          }
        }
      }
    } catch (e) {
      console.error('Error cargando lotes:', e);
    }
  };

  const handleLogout = () => {
    api.logout();
    router.push('/login');
  };

  const handleSubmitDiario = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loteSeleccionado) return;
    setLoading(true);
    setMensaje(null);

    try {
      await api.registrarDiario({
        loteId: loteSeleccionado.id,
        fechaRegistro: new Date().toISOString().split('T')[0],
        diaVida: Number(loteSeleccionado.diasDeVida) || 21,
        avesMuertas: Number(muertas),
        alimentoSuministradoKg: Number(alimentoKg),
        aguaConsumidaLitros: Number(aguaLitros),
        observaciones,
      });

      setMensaje({
        tipo: 'ok',
        texto: `✅ Registro guardado. Aves vivas actualizadas: ${
          loteSeleccionado.cantidadActualVivas - Number(muertas)
        }. Alertas zootécnicas evaluadas.`,
      });

      // Actualizar localmente el lote seleccionado
      setLoteSeleccionado((prev) =>
        prev ? { ...prev, cantidadActualVivas: prev.cantidadActualVivas - Number(muertas) } : null
      );
      setMuertas('0');
      setAlimentoKg('');
      setAguaLitros('');
      setObservaciones('');
    } catch (err: any) {
      setMensaje({ tipo: 'error', texto: `❌ ${err.message || 'Error al guardar registro'}` });
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitBiometria = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loteSeleccionado) return;
    setLoading(true);
    setMensaje(null);

    try {
      await api.registrarBiometria({
        loteId: loteSeleccionado.id,
        fechaPesaje: new Date().toISOString().split('T')[0],
        diaVida: Number(loteSeleccionado.diasDeVida) || 21,
        pesoPromedioG: Number(pesoPromedioG),
        tamanoMuestraAves: Number(tamanoMuestra),
      });

      setMensaje({
        tipo: 'ok',
        texto: `✅ Pesaje muestral guardado (${pesoPromedioG} g). Ganancia diaria (GDP) y conversión (ICA) recalculadas.`,
      });

      setPesoPromedioG('');
    } catch (err: any) {
      setMensaje({ tipo: 'error', texto: `❌ ${err.message || 'Error al guardar pesaje'}` });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 pb-12">
      {/* Barra Superior */}
      <header className="bg-slate-900 text-white px-4 py-3 shadow-md flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <span className="text-2xl">🐔</span>
          <div>
            <h1 className="text-base font-bold leading-tight">AvícolaTrack — Campo</h1>
            <p className="text-xs text-emerald-400 font-medium">
              {user ? `${user.nombreCompleto} (${user.rol})` : 'Galponero'}
            </p>
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="rounded-lg bg-slate-800 px-3 py-1.5 text-xs font-semibold text-slate-300 hover:bg-slate-700 transition"
        >
          Cerrar Sesión
        </button>
      </header>

      <main className="max-w-lg mx-auto p-4 space-y-4">
        {/* Selector de Lotes (Regla: 4 lotes por galpón) */}
        <section className="bg-white rounded-xl p-4 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">
              Galpón 1 — 4 Cuadrantes de Aves
            </span>
            <span className="text-xs font-semibold bg-emerald-100 text-emerald-800 px-2 py-0.5 rounded-full">
              Activo
            </span>
          </div>

          <div className="grid grid-cols-4 gap-2">
            {lotes.map((lote) => {
              const isSelected = loteSeleccionado?.id === lote.id;
              return (
                <button
                  key={lote.id}
                  type="button"
                  onClick={() => setLoteSeleccionado(lote)}
                  className={`p-2.5 rounded-xl border text-center transition ${
                    isSelected
                      ? 'border-emerald-600 bg-emerald-50 ring-2 ring-emerald-500'
                      : 'border-slate-200 bg-slate-50 hover:bg-slate-100 text-slate-700'
                  }`}
                >
                  <p className="text-xs font-bold text-slate-900">Lote {lote.posicionEnGalpon}</p>
                  <p className="text-[10px] font-semibold text-emerald-700 mt-0.5">
                    {lote.cantidadActualVivas} vivas
                  </p>
                </button>
              );
            })}
          </div>

          {loteSeleccionado && (
            <div className="mt-3 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-600">
              <span>Código: <strong className="text-slate-800">{loteSeleccionado.codigoLote}</strong></span>
              <span>Día de Vida: <strong className="text-slate-800">{loteSeleccionado.diasDeVida}</strong></span>
              <span>Línea: <strong className="text-slate-800">{loteSeleccionado.lineaGenetica}</strong></span>
            </div>
          )}
        </section>

        {/* Selector de Modo */}
        <div className="grid grid-cols-2 gap-2 bg-slate-200 p-1 rounded-xl">
          <button
            type="button"
            onClick={() => setModo('DIARIO')}
            className={`py-2 rounded-lg text-xs font-bold transition ${
              modo === 'DIARIO'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            📋 Registro Diario
          </button>
          <button
            type="button"
            onClick={() => setModo('BIOMETRIA')}
            className={`py-2 rounded-lg text-xs font-bold transition ${
              modo === 'BIOMETRIA'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            ⚖️ Pesaje Muestral
          </button>
        </div>

        {/* Mensaje de Feedback */}
        {mensaje && (
          <div
            className={`p-3.5 rounded-xl text-xs font-semibold border ${
              mensaje.tipo === 'ok'
                ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
                : 'bg-red-50 border-red-200 text-red-800'
            }`}
          >
            {mensaje.texto}
          </div>
        )}

        {/* Formulario 1: Registro Diario */}
        {modo === 'DIARIO' && (
          <form onSubmit={handleSubmitDiario} className="bg-white rounded-xl p-5 shadow-sm border border-slate-200 space-y-4">
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Aves Muertas del Día (Bajas)
              </label>
              <input
                type="number"
                min="0"
                required
                value={muertas}
                onChange={(e) => setMuertas(e.target.value)}
                className="mt-1 block w-full rounded-xl border border-slate-300 p-3 text-xl font-bold text-slate-900 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500"
                placeholder="0"
              />
              <p className="mt-1 text-[11px] text-slate-500">
                Se descontarán automáticamente de la población viva ({loteSeleccionado?.cantidadActualVivas || 0}).
              </p>
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Alimento Suministrado (kg)
              </label>
              <input
                type="number"
                step="0.1"
                min="0"
                required
                value={alimentoKg}
                onChange={(e) => setAlimentoKg(e.target.value)}
                className="mt-1 block w-full rounded-xl border border-slate-300 p-3 text-xl font-bold text-slate-900 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500"
                placeholder="Ej. 180.5"
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Agua Consumida (Litros)
              </label>
              <input
                type="number"
                step="0.5"
                min="0"
                required
                value={aguaLitros}
                onChange={(e) => setAguaLitros(e.target.value)}
                className="mt-1 block w-full rounded-xl border border-slate-300 p-3 text-xl font-bold text-slate-900 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500"
                placeholder="Ej. 360.0"
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Observaciones (Opcional)
              </label>
              <textarea
                rows={2}
                value={observaciones}
                onChange={(e) => setObservaciones(e.target.value)}
                className="mt-1 block w-full rounded-xl border border-slate-300 p-2.5 text-sm text-slate-900 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500"
                placeholder="Ej. Ventiladores ajustados, cama seca..."
              />
            </div>

            <button
              type="submit"
              disabled={loading || !loteSeleccionado}
              className="w-full rounded-xl bg-emerald-600 py-3.5 text-base font-bold text-white shadow-lg shadow-emerald-600/30 hover:bg-emerald-700 active:scale-[0.99] disabled:opacity-50 transition"
            >
              {loading ? 'Guardando Registro...' : 'Guardar Registro Diario'}
            </button>
          </form>
        )}

        {/* Formulario 2: Biometría de Pesaje */}
        {modo === 'BIOMETRIA' && (
          <form onSubmit={handleSubmitBiometria} className="bg-white rounded-xl p-5 shadow-sm border border-slate-200 space-y-4">
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Peso Promedio Muestral (Gramos)
              </label>
              <input
                type="number"
                step="1"
                min="10"
                required
                value={pesoPromedioG}
                onChange={(e) => setPesoPromedioG(e.target.value)}
                className="mt-1 block w-full rounded-xl border border-slate-300 p-3 text-xl font-bold text-slate-900 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500"
                placeholder="Ej. 850"
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Tamaño de Muestra (Aves pesadas)
              </label>
              <input
                type="number"
                min="5"
                required
                value={tamanoMuestra}
                onChange={(e) => setTamanoMuestra(e.target.value)}
                className="mt-1 block w-full rounded-xl border border-slate-300 p-3 text-base font-semibold text-slate-900 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500"
                placeholder="50"
              />
            </div>

            <button
              type="submit"
              disabled={loading || !loteSeleccionado}
              className="w-full rounded-xl bg-blue-600 py-3.5 text-base font-bold text-white shadow-lg shadow-blue-600/30 hover:bg-blue-700 active:scale-[0.99] disabled:opacity-50 transition"
            >
              {loading ? 'Guardando Pesaje...' : 'Guardar Pesaje Muestral'}
            </button>
          </form>
        )}
      </main>
    </div>
  );
}
