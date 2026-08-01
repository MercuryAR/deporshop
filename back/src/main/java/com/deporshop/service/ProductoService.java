package com.deporshop.service;

import com.deporshop.model.Producto;
import com.deporshop.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> obtenerPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoria_Id(categoriaId);
    }

    public List<Producto> buscar(String termino) {
        return productoRepository.findByNombreContainingIgnoreCase(termino);
    }

    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto productoData) {
        return productoRepository.findById(id)
            .map(producto -> {
                if (productoData.nombre != null) {
                    producto.nombre = productoData.nombre;
                }
                if (productoData.descripcion != null) {
                    producto.descripcion = productoData.descripcion;
                }
                if (productoData.precio > 0) {
                    producto.precio = productoData.precio;
                }
                if (productoData.imagenUrl != null) {
                    producto.imagenUrl = productoData.imagenUrl;
                }
                if (productoData.stock >= 0) {
                    producto.stock = productoData.stock;
                }
                if (productoData.descuento >= 0) {
                    producto.descuento = productoData.descuento;
                }
                if (productoData.categoria != null) {
                    producto.categoria = productoData.categoria;
                }
                return productoRepository.save(producto);
            })
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
