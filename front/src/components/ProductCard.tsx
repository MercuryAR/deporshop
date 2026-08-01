import { useState } from 'react';
import { Link } from 'react-router-dom';
import type { Producto } from '../types/Producto';
import { formatearPrecio } from '../services/api';
import { useCartStore } from '../store/cartStore';

interface ProductCardProps {
  producto: Producto;
}

export default function ProductCard({ producto }: ProductCardProps) {
  const addItem = useCartStore((s) => s.addItem);
  const [adding, setAdding] = useState(false);
  const [added, setAdded] = useState(false);

  const tieneDescuento = producto.descuento > 0;
  const precioFinal = producto.precio * (1 - producto.descuento / 100);
  const rating = Math.round(producto.rating ?? 0);

  async function handleAddToCart(e: React.MouseEvent) {
    e.preventDefault();
    e.stopPropagation();
    setAdding(true);
    try {
      await addItem(producto.id, 1);
      setAdded(true);
      setTimeout(() => setAdded(false), 1500);
    } finally {
      setAdding(false);
    }
  }

  return (
    <article className="flex flex-col h-full bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm transition-all hover:shadow-lg hover:-translate-y-1 hover:border-accent">
      <Link to={`/producto/${producto.id}`} className="relative aspect-square block overflow-hidden bg-gray-100 group">
        <img
          src={producto.imagenUrl || '/img/placeholder.png'}
          alt={producto.nombre}
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
          onError={(e) => {
            (e.target as HTMLImageElement).src = '/img/placeholder.png';
          }}
        />
        {tieneDescuento && (
          <span className="absolute top-3 right-3 bg-error text-white text-sm font-bold px-3 py-1 rounded-full shadow-md">
            -{Math.round(producto.descuento)}%
          </span>
        )}
      </Link>

      <div className="flex flex-col flex-grow p-5">
        <p className="text-xs uppercase tracking-wide text-gray-500 font-semibold mb-1">
          {producto.categoria?.nombre || 'Producto'}
        </p>
        <Link to={`/producto/${producto.id}`}>
          <h3 className="text-lg font-semibold text-gray-900 mb-3 line-clamp-2 hover:text-accent transition-colors">
            {producto.nombre}
          </h3>
        </Link>

        {producto.rating > 0 && (
          <div className="flex items-center gap-1 mb-3 text-sm">
            <span className="text-amber-400">
              {'★'.repeat(rating)}
              {'☆'.repeat(5 - rating)}
            </span>
            <span className="text-gray-500">({producto.resenas})</span>
          </div>
        )}

        <div className="flex items-center justify-between gap-3 mt-auto pt-3 border-t border-gray-200">
          <div>
            {tieneDescuento ? (
              <>
                <span className="block text-sm line-through text-gray-500">
                  {formatearPrecio(producto.precio)}
                </span>
                <span className="block text-xl font-bold text-error">
                  {formatearPrecio(precioFinal)}
                </span>
              </>
            ) : (
              <span className="text-xl font-bold text-accent">{formatearPrecio(producto.precio)}</span>
            )}
          </div>
          <button
            className="btn-accent btn text-sm py-2 px-4 disabled:opacity-60"
            disabled={producto.stock === 0 || adding}
            onClick={handleAddToCart}
          >
            {added ? 'Agregado ✓' : adding ? 'Agregando…' : 'Agregar'}
          </button>
        </div>

        {producto.stock === 0 ? (
          <p className="text-center text-error font-bold mt-3 text-sm">Agotado</p>
        ) : producto.stock < 5 ? (
          <p className="text-center text-warning font-semibold text-xs mt-3">
            Quedan {producto.stock} unidades
          </p>
        ) : null}
      </div>
    </article>
  );
}
