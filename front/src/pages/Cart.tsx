import { Link, useNavigate } from 'react-router-dom';
import CartItem from '../components/CartItem';
import { useCartStore, ENVIO_GRATIS_DESDE } from '../store/cartStore';
import { formatearPrecio } from '../services/api';

export default function Cart() {
  const { items, loading, clear, getSubtotal, getEnvio, getTotalConEnvio } = useCartStore();
  const navigate = useNavigate();

  const subtotal = getSubtotal();
  const envio = getEnvio();
  const total = getTotalConEnvio();
  const faltaParaEnvioGratis = ENVIO_GRATIS_DESDE - subtotal;

  async function handleClear() {
    if (confirm('¿Estás seguro de que deseas vaciar el carrito?')) {
      await clear();
    }
  }

  if (loading && items.length === 0) {
    return <p className="text-center py-24 text-gray-500">Cargando carrito…</p>;
  }

  if (items.length === 0) {
    return (
      <div className="container-app py-24 text-center">
        <div className="text-7xl mb-6">🛒</div>
        <h1 className="text-2xl font-bold mb-3">Tu carrito está vacío</h1>
        <p className="text-gray-500 mb-8">Agrega productos para comenzar tu compra</p>
        <Link to="/productos" className="btn-primary">
          Ver productos
        </Link>
      </div>
    );
  }

  return (
    <div className="container-app py-10">
      <h1 className="text-3xl font-bold mb-8">Tu carrito</h1>

      <div className="grid lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 flex flex-col gap-4">
          {items.map((item) => (
            <CartItem key={item.productoId} item={item} />
          ))}

          <div className="flex flex-wrap gap-3 mt-4">
            <Link to="/productos" className="btn-secondary">
              Continuar comprando
            </Link>
            <button className="btn-error" onClick={handleClear}>
              Vaciar carrito
            </button>
          </div>
        </div>

        <div className="bg-gray-50 p-6 rounded-lg h-fit flex flex-col gap-4">
          <h2 className="text-xl font-bold">Resumen del pedido</h2>

          <div className="flex justify-between text-gray-700">
            <span>Subtotal</span>
            <span>{formatearPrecio(subtotal)}</span>
          </div>
          <div className="flex justify-between text-gray-700">
            <span>Envío</span>
            <span>{envio === 0 ? 'Gratis' : formatearPrecio(envio)}</span>
          </div>

          {faltaParaEnvioGratis > 0 && (
            <p className="bg-success text-white text-sm text-center px-3 py-2 rounded-md">
              Agrega {formatearPrecio(faltaParaEnvioGratis)} más para envío gratis
            </p>
          )}

          <div className="flex justify-between text-xl font-bold pt-4 border-t-2 border-gray-200">
            <span>Total</span>
            <span>{formatearPrecio(total)}</span>
          </div>

          <button className="btn-accent w-full mt-2" onClick={() => navigate('/checkout')}>
            Proceder al pago
          </button>
        </div>
      </div>
    </div>
  );
}
