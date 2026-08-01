import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import * as api from '../services/api';
import type { CarritoItem } from '../types/Carrito';

const ENVIO_GRATIS_DESDE = 150000;
const COSTO_ENVIO = 6990;

interface CartState {
  items: CarritoItem[];
  total: number;
  cantidad: number;
  loading: boolean;
  error: string | null;

  fetchCart: () => Promise<void>;
  addItem: (productoId: number, cantidad?: number) => Promise<void>;
  removeItem: (productoId: number) => Promise<void>;
  updateQuantity: (productoId: number, cantidad: number) => Promise<void>;
  clear: () => Promise<void>;

  getSubtotal: () => number;
  getEnvio: () => number;
  getTotalConEnvio: () => number;
}

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],
      total: 0,
      cantidad: 0,
      loading: false,
      error: null,

      fetchCart: async () => {
        set({ loading: true, error: null });
        try {
          const carrito = await api.getCarrito();
          set({
            items: carrito.items ?? [],
            total: carrito.total ?? 0,
            cantidad: carrito.cantidad ?? 0,
            loading: false,
          });
        } catch (err) {
          console.error('Error al obtener el carrito:', err);
          set({ loading: false, error: 'No se pudo cargar el carrito.' });
        }
      },

      addItem: async (productoId, cantidad = 1) => {
        set({ loading: true, error: null });
        try {
          const carrito = await api.addToCart(productoId, cantidad);
          set({
            items: carrito.items ?? [],
            total: carrito.total ?? 0,
            cantidad: carrito.cantidad ?? 0,
            loading: false,
          });
        } catch (err) {
          console.error('Error al agregar al carrito:', err);
          set({ loading: false, error: 'No se pudo agregar el producto al carrito.' });
          throw err;
        }
      },

      removeItem: async (productoId) => {
        set({ loading: true, error: null });
        try {
          await api.removeFromCart(productoId);
          await get().fetchCart();
        } catch (err) {
          console.error('Error al eliminar del carrito:', err);
          set({ loading: false, error: 'No se pudo eliminar el producto.' });
        }
      },

      updateQuantity: async (productoId, cantidad) => {
        if (cantidad < 1) {
          await get().removeItem(productoId);
          return;
        }
        set({ loading: true, error: null });
        try {
          const carrito = await api.updateCartQty(productoId, cantidad);
          set({
            items: carrito.items ?? [],
            total: carrito.total ?? 0,
            cantidad: carrito.cantidad ?? 0,
            loading: false,
          });
        } catch (err) {
          console.error('Error al actualizar cantidad:', err);
          set({ loading: false, error: 'No se pudo actualizar la cantidad.' });
        }
      },

      clear: async () => {
        set({ loading: true, error: null });
        try {
          await api.clearCart();
          set({ items: [], total: 0, cantidad: 0, loading: false });
        } catch (err) {
          console.error('Error al vaciar el carrito:', err);
          set({ loading: false, error: 'No se pudo vaciar el carrito.' });
        }
      },

      getSubtotal: () => get().total,
      getEnvio: () => (get().total >= ENVIO_GRATIS_DESDE || get().total === 0 ? 0 : COSTO_ENVIO),
      getTotalConEnvio: () => get().getSubtotal() + get().getEnvio(),
    }),
    {
      name: 'deporshop-cart',
      partialize: (state) => ({ items: state.items, total: state.total, cantidad: state.cantidad }),
    },
  ),
);

export { ENVIO_GRATIS_DESDE, COSTO_ENVIO };
