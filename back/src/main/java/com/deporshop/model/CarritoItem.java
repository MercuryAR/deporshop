package com.deporshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {
    public Long productoId;
    public String nombre;
    public double precio;
    public int cantidad;
    public double subtotal;

    public CarritoItem(Producto producto, int cantidad) {
        this.productoId = producto.id;
        this.nombre = producto.nombre;
        this.precio = producto.getPrecioConDescuento();
        this.cantidad = cantidad;
        this.subtotal = this.precio * cantidad;
    }

    public void actualizar(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.precio * cantidad;
    }
}
