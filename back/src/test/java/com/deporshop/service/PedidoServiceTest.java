package com.deporshop.service;

import com.deporshop.dto.ItemPedidoRequest;
import com.deporshop.exception.AccesoNoAutorizadoException;
import com.deporshop.exception.CarritoVacioException;
import com.deporshop.exception.RecursoNoEncontradoException;
import com.deporshop.model.Categoria;
import com.deporshop.model.Pedido;
import com.deporshop.model.Producto;
import com.deporshop.model.Usuario;
import com.deporshop.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.id = 1L;
        usuario.nombre = "Juan Perez";
        usuario.email = "juan@example.com";
        return usuario;
    }

    private Producto producto() {
        Producto producto = new Producto("Air Max", "Zapatilla deportiva", 100.0, new Categoria("Zapatillas"));
        producto.id = 1L;
        return producto;
    }

    private ItemPedidoRequest item(long productoId, int cantidad) {
        ItemPedidoRequest item = new ItemPedidoRequest();
        item.productoId = productoId;
        item.cantidad = cantidad;
        return item;
    }

    @Test
    void crear_conItemsValidos_recalculaPreciosDesdeElProductoReal() {
        when(usuarioService.buscarPorEmail("juan@example.com")).thenReturn(Optional.of(usuario()));
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(producto()));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.crear("juan@example.com", List.of(item(1L, 2)));

        assertThat(resultado.total).isEqualTo(200.0); // 100.0 * 2, no lo que mande el cliente
        assertThat(resultado.estado).isEqualTo("CONFIRMADO");
        assertThat(resultado.items).hasSize(1);
        assertThat(resultado.items.get(0).nombre).isEqualTo("Air Max");
        assertThat(resultado.items.get(0).precio).isEqualTo(100.0);
    }

    @Test
    void crear_conListaVacia_lanzaExcepcionYNoGuardaNada() {
        assertThatThrownBy(() -> pedidoService.crear("juan@example.com", Collections.emptyList()))
            .isInstanceOf(CarritoVacioException.class);

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crear_conProductoInexistente_lanzaRecursoNoEncontrado() {
        when(usuarioService.buscarPorEmail("juan@example.com")).thenReturn(Optional.of(usuario()));
        when(productoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.crear("juan@example.com", List.of(item(99L, 1))))
            .isInstanceOf(RecursoNoEncontradoException.class);

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_deOtroUsuario_lanzaAccesoNoAutorizado() {
        Pedido pedido = new Pedido();
        pedido.id = 5L;
        pedido.usuario = usuario(); // dueño: juan@example.com
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.obtenerPorId(5L, "otro@example.com"))
            .isInstanceOf(AccesoNoAutorizadoException.class);
    }

    @Test
    void obtenerPorId_inexistente_lanzaRecursoNoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.obtenerPorId(99L, "juan@example.com"))
            .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtenerPorUsuario_delegaEnRepositoryOrdenadoPorFecha() {
        when(usuarioService.buscarPorEmail("juan@example.com")).thenReturn(Optional.of(usuario()));
        when(pedidoRepository.findByUsuario_IdOrderByFechaDesc(1L)).thenReturn(List.of(new Pedido()));

        List<Pedido> resultado = pedidoService.obtenerPorUsuario("juan@example.com");

        assertThat(resultado).hasSize(1);
        verify(pedidoRepository).findByUsuario_IdOrderByFechaDesc(1L);
    }
}
