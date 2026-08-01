import axios from 'axios';
import type { AxiosInstance } from 'axios';
import type { Producto } from '../types/Producto';
import type { Categoria } from '../types/Categoria';
import type { Carrito } from '../types/Carrito';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/Usuario';
import type { Pedido } from '../types/Pedido';
import { useAuthStore } from '../store/authStore';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api';

export const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  // El carrito vive en la sesión HTTP del backend (@SessionScope). Sin esto, el
  // navegador no envía ni guarda la cookie de sesión en requests cross-origin
  // (front en :5173, back en :8080), y cada request cae en una sesión nueva y vacía.
  withCredentials: true,
});

// Attach the JWT (once auth is wired up) to every outgoing request.
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ============================================
// PRODUCTOS
// ============================================

export interface ProductoFiltros {
  categoriaId?: number | string;
  buscar?: string;
}

export async function getProductos(params?: ProductoFiltros): Promise<Producto[]> {
  const { data } = await api.get<Producto[]>('/productos', { params });
  return data;
}

export async function getProducto(id: number | string): Promise<Producto> {
  const { data } = await api.get<Producto>(`/productos/${id}`);
  return data;
}

// ============================================
// CATEGORIAS
// ============================================

export async function getCategorias(): Promise<Categoria[]> {
  const { data } = await api.get<Categoria[]>('/categorias');
  return data;
}

// ============================================
// CARRITO
// ============================================

export async function getCarrito(): Promise<Carrito> {
  const { data } = await api.get<Carrito>('/carrito');
  return data;
}

export async function addToCart(productoId: number, cantidad = 1): Promise<Carrito> {
  const { data } = await api.post<Carrito>('/carrito/agregar', null, {
    params: { productoId, cantidad },
  });
  return data;
}

export async function removeFromCart(productoId: number): Promise<void> {
  await api.delete(`/carrito/eliminar/${productoId}`);
}

export async function updateCartQty(productoId: number, cantidad: number): Promise<Carrito> {
  const { data } = await api.put<Carrito>(`/carrito/actualizar/${productoId}`, null, {
    params: { cantidad },
  });
  return data;
}

export async function clearCart(): Promise<void> {
  await api.delete('/carrito/limpiar');
}

export async function getCartTotal(): Promise<number> {
  const { data } = await api.get<number>('/carrito/total');
  return data;
}

// ============================================
// AUTH
// ============================================

export async function login(payload: LoginRequest): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/auth/login', payload);
  return data;
}

export async function register(payload: RegisterRequest): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/auth/register', payload);
  return data;
}

// ============================================
// PEDIDOS
// ============================================

export interface ItemPedidoRequest {
  productoId: number;
  cantidad: number;
}

export async function crearPedido(items: ItemPedidoRequest[]): Promise<Pedido> {
  const { data } = await api.post<Pedido>('/pedidos', { items });
  return data;
}

export async function getMisPedidos(): Promise<Pedido[]> {
  const { data } = await api.get<Pedido[]>('/pedidos/mis-pedidos');
  return data;
}

// ============================================
// UTILIDADES
// ============================================

export function formatearPrecio(precio: number): string {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  }).format(precio);
}
