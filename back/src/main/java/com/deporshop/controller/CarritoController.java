package com.deporshop.controller;

import com.deporshop.model.CarritoItem;
import com.deporshop.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerCarrito() {
        Map<String, Object> response = new HashMap<>();
        response.put("items", carritoService.obtenerCarrito());
        response.put("total", carritoService.obtenerTotal());
        response.put("cantidad", carritoService.obtenerCantidadTotal());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregarProducto(
            @RequestParam Long productoId,
            @RequestParam(defaultValue = "1") int cantidad) {
        carritoService.agregarProducto(productoId, cantidad);
        return ResponseEntity.ok(obtenerCarrito().getBody());
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable Long productoId) {
        carritoService.eliminarProducto(productoId);
        return ResponseEntity.ok(obtenerCarrito().getBody());
    }

    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<Map<String, Object>> actualizarCantidad(
            @PathVariable Long productoId,
            @RequestParam int cantidad) {
        carritoService.actualizarCantidad(productoId, cantidad);
        return ResponseEntity.ok(obtenerCarrito().getBody());
    }

    @DeleteMapping("/limpiar")
    public ResponseEntity<Void> limpiarCarrito() {
        carritoService.limpiarCarrito();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> obtenerTotal() {
        Map<String, Object> response = new HashMap<>();
        response.put("total", carritoService.obtenerTotal());
        response.put("cantidad", carritoService.obtenerCantidadTotal());
        return ResponseEntity.ok(response);
    }
}
