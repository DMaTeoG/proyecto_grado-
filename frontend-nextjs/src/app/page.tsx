'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';

export default function HomePage() {
  const router = useRouter();

  useEffect(() => {
    const user = api.getCurrentUser();
    if (!user) {
      router.push('/login');
    } else if (user.rol === 'ROLE_ADMIN') {
      router.push('/admin/dashboard');
    } else {
      router.push('/galponero/registro');
    }
  }, [router]);

  return (
    <div className="flex h-screen items-center justify-center bg-slate-100">
      <div className="text-center">
        <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-emerald-600 border-r-transparent"></div>
        <p className="mt-4 text-sm font-medium text-slate-600">Cargando AvícolaTrack...</p>
      </div>
    </div>
  );
}
