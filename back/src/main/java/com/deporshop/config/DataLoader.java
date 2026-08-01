package com.deporshop.config;

import com.deporshop.model.Categoria;
import com.deporshop.model.Producto;
import com.deporshop.repository.CategoriaRepository;
import com.deporshop.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() == 0) {
            cargarDatos();
        }
    }

    private void cargarDatos() {
        // Crear categorías
        Categoria zapatillas = new Categoria("Zapatillas");
        Categoria accesorios = new Categoria("Accesorios");
        Categoria ropa = new Categoria("Ropa Deportiva");

        categoriaRepository.save(zapatillas);
        categoriaRepository.save(accesorios);
        categoriaRepository.save(ropa);

        // Crear productos de ejemplo (precios en pesos argentinos, escala realista)
        Producto p1 = new Producto("Running Max Pro", "Zapatillas para correr con tecnología de amortiguación", 134999, zapatillas);
        p1.imagenUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop";
        p1.descuento = 10;
        p1.stock = 15;
        p1.rating = 4.5;
        p1.resenas = 42;

        Producto p2 = new Producto("Trail Blazer", "Zapatillas para trail running", 149999, zapatillas);
        p2.imagenUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop&q=70";
        p2.stock = 20;
        p2.rating = 4.8;
        p2.resenas = 67;

        Producto p3 = new Producto("Court Flex", "Zapatillas para tenis y canchas duras", 94999, zapatillas);
        p3.imagenUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop&q=60";
        p3.descuento = 15;
        p3.stock = 25;
        p3.rating = 4.3;
        p3.resenas = 31;

        Producto p4 = new Producto("Mochila deportiva", "Mochila impermeable con compartimientos", 42999, accesorios);
        p4.imagenUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop";
        p4.stock = 50;
        p4.rating = 4.6;
        p4.resenas = 28;

        Producto p5 = new Producto("Remera deportiva", "Remera de rendimiento técnico con humedad", 24999, ropa);
        p5.imagenUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=400&fit=crop";
        p5.stock = 100;
        p5.rating = 4.4;
        p5.resenas = 19;

        Producto p6 = new Producto("Short deportivo", "Short ligero y cómodo para deportes", 27999, ropa);
        p6.imagenUrl = "https://images.unsplash.com/photo-1506401543311-27ad1cb34e09?w=400&h=400&fit=crop";
        p6.stock = 80;
        p6.rating = 4.5;
        p6.resenas = 22;

        productoRepository.save(p1);
        productoRepository.save(p2);
        productoRepository.save(p3);
        productoRepository.save(p4);
        productoRepository.save(p5);
        productoRepository.save(p6);
    }
}
