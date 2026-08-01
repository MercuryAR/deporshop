package com.deporshop.service;

import com.deporshop.model.Categoria;
import com.deporshop.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria crear(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria categoriaData) {
        return categoriaRepository.findById(id)
            .map(categoria -> {
                if (categoriaData.nombre != null) {
                    categoria.nombre = categoriaData.nombre;
                }
                return categoriaRepository.save(categoria);
            })
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
}
