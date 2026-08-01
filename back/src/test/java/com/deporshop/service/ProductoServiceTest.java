package com.deporshop.service;

import com.deporshop.model.Categoria;
import com.deporshop.model.Producto;
import com.deporshop.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria("Zapatillas");
        categoria.id = 1L;

        producto = new Producto("Air Max", "Zapatilla deportiva", 100.0, categoria);
        producto.id = 1L;
    }

    @Test
    void obtenerTodos_devuelveTodosLosProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.obtenerTodos();

        assertThat(resultado).hasSize(1).containsExactly(producto);
    }

    @Test
    void obtenerPorId_existente_devuelveProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.obtenerPorId(1L);

        assertThat(resultado).isPresent().contains(producto);
    }

    @Test
    void obtenerPorId_inexistente_devuelveOptionalVacio() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = productoService.obtenerPorId(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void obtenerPorCategoria_delegaEnRepository() {
        when(productoRepository.findByCategoria_Id(1L)).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.obtenerPorCategoria(1L);

        assertThat(resultado).containsExactly(producto);
        verify(productoRepository).findByCategoria_Id(1L);
    }

    @Test
    void buscar_delegaEnBusquedaPorNombre() {
        when(productoRepository.findByNombreContainingIgnoreCase("air")).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.buscar("air");

        assertThat(resultado).containsExactly(producto);
    }

    @Test
    void crear_guardaYDevuelveElProducto() {
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.crear(producto);

        assertThat(resultado).isEqualTo(producto);
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizar_soloPisaCamposNoNulosOPositivos() {
        Producto cambios = new Producto();
        cambios.nombre = "Air Max 2";
        cambios.precio = 150.0;
        // descripcion, stock, descuento, categoria quedan sin setear (null/0)

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto resultado = productoService.actualizar(1L, cambios);

        assertThat(resultado.nombre).isEqualTo("Air Max 2");
        assertThat(resultado.precio).isEqualTo(150.0);
        // no se pisó porque cambios.descripcion era null
        assertThat(resultado.descripcion).isEqualTo("Zapatilla deportiva");
    }

    @Test
    void actualizar_productoInexistente_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizar(99L, producto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("no encontrado");
    }

    @Test
    void eliminar_delegaEnRepository() {
        productoService.eliminar(1L);

        verify(productoRepository).deleteById(1L);
    }
}
