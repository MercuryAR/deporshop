import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCartStore } from '../store/cartStore';
import { crearPedido, formatearPrecio } from '../services/api';

export default function Checkout() {
  const { items, clear, getSubtotal, getEnvio, getTotalConEnvio } = useCartStore();
  const [confirmando, setConfirmando] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  async function handleConfirmar() {
    setConfirmando(true);
    setError(null);
    try {
      const pedido = await crearPedido(
        items.map((item) => ({ productoId: item.productoId, cantidad: item.cantidad })),
      );
      await clear();
      navigate('/perfil', { state: { pedidoConfirmado: pedido.id } });
    } catch (err) {
      console.error('Error al confirmar el pedido:', err);
      setError('No se pudo confirmar el pedido. Intenta nuevamente.');
      setConfirmando(false);
    }
  }

  if (items.length === 0) {
    return (
      <div className="container-app py-24 text-center">
        <p className="text-lg text-gray-500 mb-6">No hay productos en tu carrito para pagar.</p>
        <Link to="/productos" className="btn-primary">
          Ver productos
        </Link>
      </div>
    );
  }

  return (
    <div className="container-app py-10 max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold mb-8">Resumen de compra</h1>

      <div className="bg-white border border-gray-200 rounded-xl shadow-md p-6 mb-6">
        <div className="flex flex-col gap-3 mb-6">
          {items.map((item) => (
            <div key={item.productoId} className="flex justify-between text-gray-700">
              <span>
                {item.nombre} x{item.cantidad}
              </span>
              <span className="font-medium">{formatearPrecio(item.subtotal)}</span>
            </div>
          ))}
        </div>

        <div className="flex flex-col gap-2 pt-4 border-t border-gray-200">
          <div className="flex justify-between text-gray-700">
            <span>Subtotal</span>
            <span>{formatearPrecio(getSubtotal())}</span>
          </div>
          <div className="flex justify-between text-gray-700">
            <span>Envío</span>
            <span>{getEnvio() === 0 ? 'Gratis' : formatearPrecio(getEnvio())}</span>
          </div>
          <div className="flex justify-between text-xl font-bold pt-2 border-t-2 border-gray-200">
            <span>Total</span>
            <span>{formatearPrecio(getTotalConEnvio())}</span>
          </div>
        </div>
      </div>

      {error && <p className="text-sm text-red-600 mb-4">{error}</p>}

      <button
        className="btn-accent w-full text-lg py-3 disabled:opacity-60 disabled:cursor-not-allowed"
        onClick={handleConfirmar}
        disabled={confirmando}
      >
        {confirmando ? 'Confirmando...' : 'Confirmar pedido'}
      </button>
    </div>
  );
}
