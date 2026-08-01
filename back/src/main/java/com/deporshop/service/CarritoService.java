package com.deporshop.service;

import com.deporshop.model.CarritoItem;
import com.deporshop.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;

@Service
@SessionScope
public class CarritoService {

    @Autowired
    private ProductoService productoService;

    private Map<Long, CarritoItem> carrito = new HashMap<>();

    public void agregarProducto(Long productoId, int cantidad) {
        Producto producto = productoService.obtenerPorId(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (carrito.containsKey(productoId)) {
            CarritoItem item = carrito.get(productoId);
            item.actualizar(item.cantidad + cantidad);
        } else {
            carrito.put(productoId, new CarritoItem(producto, cantidad));
        }
    }

    public void eliminarProducto(Long productoId) {
        carrito.remove(productoId);
    }

    public void actualizarCantidad(Long productoId, int cantidad) {
        if (carrito.containsKey(productoId)) {
            if (cantidad <= 0) {
                carrito.remove(productoId);
            } else {
                carrito.get(productoId).actualizar(cantidad);
            }
        }
    }

    public List<CarritoItem> obtenerCarrito() {
        return new ArrayList<>(carrito.values());
    }

    public double obtenerTotal() {
        return carrito.values().stream()
            .mapToDouble(item -> item.subtotal)
            .sum();
    }

    public int obtenerCantidadTotal() {
        return carrito.values().stream()
            .mapToInt(item -> item.cantidad)
            .sum();
    }

    public void limpiarCarrito() {
        carrito.clear();
    }

    public boolean estaVacio() {
        return carrito.isEmpty();
    }
}
