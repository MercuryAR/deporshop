package com.deporshop.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PedidoItem {
    public Long productoId;
    public String nombre;
    public double precio;
    public int cantidad;
    public double subtotal;

    public PedidoItem() {
    }

    public PedidoItem(Long productoId, String nombre, double precio, int cantidad, double subtotal) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }
}
