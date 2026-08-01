import type { Categoria } from './Categoria';

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  descuento: number;
  imagenUrl: string;
  stock: number;
  categoria: Categoria;
  rating: number;
  resenas: number;
}
