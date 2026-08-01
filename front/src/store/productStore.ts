import { create } from 'zustand';
import * as api from '../services/api';
import type { Producto } from '../types/Producto';
import type { Categoria } from '../types/Categoria';

export type OrdenCriterio = 'relevancia' | 'precio-asc' | 'precio-desc' | 'nombre' | 'rating';

interface Filtros {
  categoriaId: number | 'all';
  buscar: string;
  soloEnStock: boolean;
  soloConDescuento: boolean;
  ordenar: OrdenCriterio;
}

interface ProductState {
  productos: Producto[];
  categorias: Categoria[];
  filtros: Filtros;
  loading: boolean;
  error: string | null;

  fetchProductos: () => Promise<void>;
  fetchCategorias: () => Promise<void>;
  setFiltro: <K extends keyof Filtros>(key: K, value: Filtros[K]) => void;
  resetFiltros: () => void;
  getProductosFiltrados: () => Producto[];
}

const filtrosIniciales: Filtros = {
  categoriaId: 'all',
  buscar: '',
  soloEnStock: false,
  soloConDescuento: false,
  ordenar: 'relevancia',
};

function ordenarProductos(productos: Producto[], criterio: OrdenCriterio): Producto[] {
  const sorted = [...productos];
  switch (criterio) {
    case 'precio-asc':
      return sorted.sort(
        (a, b) => a.precio * (1 - a.descuento / 100) - b.precio * (1 - b.descuento / 100),
      );
    case 'precio-desc':
      return sorted.sort(
        (a, b) => b.precio * (1 - b.descuento / 100) - a.precio * (1 - a.descuento / 100),
      );
    case 'nombre':
      return sorted.sort((a, b) => a.nombre.localeCompare(b.nombre));
    case 'rating':
      return sorted.sort((a, b) => b.rating - a.rating);
    default:
      return sorted;
  }
}

export const useProductStore = create<ProductState>()((set, get) => ({
  productos: [],
  categorias: [],
  filtros: filtrosIniciales,
  loading: false,
  error: null,

  fetchProductos: async () => {
    set({ loading: true, error: null });
    try {
      const productos = await api.getProductos();
      set({ productos, loading: false });
    } catch (err) {
      console.error('Error al cargar productos:', err);
      set({
        loading: false,
        error: 'No se pudieron cargar los productos. Asegúrate de que el servidor está ejecutándose en http://localhost:8080',
      });
    }
  },

  fetchCategorias: async () => {
    try {
      const categorias = await api.getCategorias();
      set({ categorias });
    } catch (err) {
      console.error('Error al cargar categorías:', err);
    }
  },

  setFiltro: (key, value) => {
    set((state) => ({ filtros: { ...state.filtros, [key]: value } }));
  },

  resetFiltros: () => set({ filtros: filtrosIniciales }),

  getProductosFiltrados: () => {
    const { productos, filtros } = get();
    let resultado = [...productos];

    if (filtros.categoriaId !== 'all') {
      resultado = resultado.filter((p) => p.categoria?.id === filtros.categoriaId);
    }
    if (filtros.buscar.trim()) {
      const termino = filtros.buscar.trim().toLowerCase();
      resultado = resultado.filter(
        (p) =>
          p.nombre.toLowerCase().includes(termino) ||
          p.descripcion?.toLowerCase().includes(termino),
      );
    }
    if (filtros.soloEnStock) {
      resultado = resultado.filter((p) => p.stock > 0);
    }
    if (filtros.soloConDescuento) {
      resultado = resultado.filter((p) => p.descuento > 0);
    }

    return ordenarProductos(resultado, filtros.ordenar);
  },
}));
