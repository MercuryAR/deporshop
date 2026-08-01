export interface CarritoItem {
  productoId: number;
  nombre: string;
  precio: number;
  cantidad: number;
  subtotal: number;
  imagenUrl?: string;
}

export interface Carrito {
  items: CarritoItem[];
  total: number;
  cantidad: number;
}
