package com.deporshop.controller;

import com.deporshop.model.Categoria;
import com.deporshop.model.Producto;
import com.deporshop.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private Producto producto;

    @Test
    void obtenerTodos_sinFiltros_devuelveTodosLosProductos() {
        producto = new Producto("Air Max", "Zapatilla", 100.0, new Categoria("Zapatillas"));
        when(productoService.obtenerTodos()).thenReturn(List.of(producto));

        ResponseEntity<List<Producto>> response = productoController.obtenerTodos(null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(producto);
    }

    @Test
    void obtenerTodos_conBuscar_delegaEnBusqueda() {
        producto = new Producto("Air Max", "Zapatilla", 100.0, new Categoria("Zapatillas"));
        when(productoService.buscar("air")).thenReturn(List.of(producto));

        ResponseEntity<List<Producto>> response = productoController.obtenerTodos(null, "air");

        assertThat(response.getBody()).containsExactly(producto);
        verify(productoService).buscar("air");
    }

    @Test
    void obtenerTodos_conCategoriaId_delegaEnFiltroPorCategoria() {
        producto = new Producto("Air Max", "Zapatilla", 100.0, new Categoria("Zapatillas"));
        when(productoService.obtenerPorCategoria(1L)).thenReturn(List.of(producto));

        ResponseEntity<List<Producto>> response = productoController.obtenerTodos(1L, null);

        assertThat(response.getBody()).containsExactly(producto);
        verify(productoService).obtenerPorCategoria(1L);
    }

    @Test
    void obtenerPorId_existente_devuelve200() {
        producto = new Producto("Air Max", "Zapatilla", 100.0, new Categoria("Zapatillas"));
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(producto));

        ResponseEntity<Producto> response = productoController.obtenerPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(producto);
    }

    @Test
    void obtenerPorId_inexistente_devuelve404() {
        when(productoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Producto> response = productoController.obtenerPorId(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void eliminar_devuelve204() {
        ResponseEntity<Void> response = productoController.eliminar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productoService).eliminar(1L);
    }
}
