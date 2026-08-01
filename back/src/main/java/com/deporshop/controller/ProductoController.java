package com.deporshop.controller;

import com.deporshop.model.Producto;
import com.deporshop.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos(@RequestParam(required = false) Long categoriaId,
                                                        @RequestParam(required = false) String buscar) {
        List<Producto> productos;

        if (buscar != null && !buscar.isEmpty()) {
            productos = productoService.buscar(buscar);
        } else if (categoriaId != null) {
            productos = productoService.obtenerPorCategoria(categoriaId);
        } else {
            productos = productoService.obtenerTodos();
        }

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoriaId));
    }

    @GetMapping("/buscar/{termino}")
    public ResponseEntity<List<Producto>> buscar(@PathVariable String termino) {
        return ResponseEntity.ok(productoService.buscar(termino));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.crear(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizar(id, producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
