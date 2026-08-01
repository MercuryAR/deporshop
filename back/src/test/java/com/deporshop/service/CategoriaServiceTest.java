package com.deporshop.service;

import com.deporshop.model.Categoria;
import com.deporshop.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void obtenerTodas_devuelveTodasLasCategorias() {
        Categoria categoria = new Categoria("Zapatillas");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> resultado = categoriaService.obtenerTodas();

        assertThat(resultado).containsExactly(categoria);
    }

    @Test
    void actualizar_pisaSoloNombreNoNulo() {
        Categoria existente = new Categoria("Zapatillas");
        existente.id = 1L;
        Categoria cambios = new Categoria("Calzado Deportivo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(existente)).thenReturn(existente);

        Categoria resultado = categoriaService.actualizar(1L, cambios);

        assertThat(resultado.nombre).isEqualTo("Calzado Deportivo");
    }

    @Test
    void actualizar_categoriaInexistente_lanzaExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.actualizar(99L, new Categoria("X")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("no encontrada");
    }

    @Test
    void eliminar_delegaEnRepository() {
        categoriaService.eliminar(1L);

        verify(categoriaRepository).deleteById(1L);
    }
}
