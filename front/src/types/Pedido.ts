export interface PedidoItem {
  productoId: number;
  nombre: string;
  precio: number;
  cantidad: number;
  subtotal: number;
}

export interface PedidoUsuario {
  id: number;
  nombre: string;
  email: string;
}

export interface Pedido {
  id: number;
  usuario: PedidoUsuario;
  items: PedidoItem[];
  total: number;
  estado: string;
  fecha: string;
}
