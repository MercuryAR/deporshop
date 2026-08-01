package com.deporshop.repository;

import com.deporshop.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria_Id(Long categoriaId);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
