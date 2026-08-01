import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { getMisPedidos, formatearPrecio } from '../services/api';
import type { Pedido } from '../types/Pedido';

export default function User() {
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();
  const location = useLocation();

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const pedidoConfirmado = (location.state as { pedidoConfirmado?: number } | null)?.pedidoConfirmado;

  useEffect(() => {
    let activo = true;
    setLoading(true);
    getMisPedidos()
      .then((data) => {
        if (activo) setPedidos(data);
      })
      .catch((err) => {
        console.error('Error al obtener pedidos:', err);
        if (activo) setError('No se pudo cargar tu historial de pedidos.');
      })
      .finally(() => {
        if (activo) setLoading(false);
      });
    return () => {
      activo = false;
    };
  }, []);

  function handleLogout() {
    logout();
    navigate('/');
  }

  function formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  return (
    <div className="container-app py-16 max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold mb-8">Mi perfil</h1>

      <div className="flex flex-col sm:flex-row gap-6 items-center sm:items-start bg-gray-50 rounded-xl p-6 mb-8">
        <div className="w-20 h-20 rounded-full bg-primary flex items-center justify-center flex-shrink-0 text-white text-3xl font-bold">
          {user?.nombre?.charAt(0).toUpperCase() ?? '?'}
        </div>
        <div className="text-center sm:text-left">
          <p className="mb-2">
            <strong className="inline-block min-w-[110px] text-gray-500">Nombre:</strong>{' '}
            {user?.nombre ?? '—'}
          </p>
          <p className="mb-2">
            <strong className="inline-block min-w-[110px] text-gray-500">Email:</strong>{' '}
            {user?.email ?? '—'}
          </p>
        </div>
      </div>

      {pedidoConfirmado && (
        <div className="bg-green-50 border border-green-200 text-green-800 rounded-xl p-4 mb-8">
          ¡Pedido #{pedidoConfirmado} confirmado! Gracias por tu compra.
        </div>
      )}

      <h2 className="text-xl font-bold mb-4">Mis pedidos</h2>

      {loading && <p className="text-gray-500 mb-8">Cargando pedidos...</p>}

      {!loading && error && <p className="text-red-600 mb-8">{error}</p>}

      {!loading && !error && pedidos.length === 0 && (
        <div className="flex items-center justify-center min-h-[200px] border-2 border-dashed border-gray-200 rounded-xl text-center p-8 mb-8">
          <div>
            <p className="text-gray-500 mb-2">Aún no tienes pedidos.</p>
            <p className="text-sm text-gray-400">Tu historial de compras aparecerá aquí.</p>
          </div>
        </div>
      )}

      {!loading && !error && pedidos.length > 0 && (
        <div className="flex flex-col gap-4 mb-8">
          {pedidos.map((pedido) => (
            <div key={pedido.id} className="border border-gray-200 rounded-xl p-5">
              <div className="flex justify-between items-start mb-3">
                <div>
                  <p className="font-semibold">Pedido #{pedido.id}</p>
                  <p className="text-sm text-gray-500">{formatearFecha(pedido.fecha)}</p>
                </div>
                <span className="text-xs font-medium bg-primary/10 text-primary px-3 py-1 rounded-full">
                  {pedido.estado}
                </span>
              </div>
              <div className="flex flex-col gap-1 mb-3">
                {pedido.items.map((item) => (
                  <div key={item.productoId} className="flex justify-between text-sm text-gray-700">
                    <span>
                      {item.nombre} x{item.cantidad}
                    </span>
                    <span>{formatearPrecio(item.subtotal)}</span>
                  </div>
                ))}
              </div>
              <div className="flex justify-between font-bold pt-3 border-t border-gray-100">
                <span>Total</span>
                <span>{formatearPrecio(pedido.total)}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      <button className="btn-outline" onClick={handleLogout}>
        Cerrar sesión
      </button>
    </div>
  );
}
