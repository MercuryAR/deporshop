package com.deporshop.service;

import com.deporshop.dto.ItemPedidoRequest;
import com.deporshop.exception.AccesoNoAutorizadoException;
import com.deporshop.exception.CarritoVacioException;
import com.deporshop.exception.RecursoNoEncontradoException;
import com.deporshop.model.Pedido;
import com.deporshop.model.PedidoItem;
import com.deporshop.model.Producto;
import com.deporshop.model.Usuario;
import com.deporshop.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Los precios se recalculan siempre a partir del Producto real en el servidor
     * (nunca se confía en un precio que mande el cliente), y el pedido se arma con
     * los items que el cliente indica en el request en lugar de depender del
     * carrito de sesión HTTP, que no persiste de forma confiable entre recargas
     * de página cuando frontend y backend están en orígenes distintos.
     */
    public Pedido crear(String emailUsuario, List<ItemPedidoRequest> itemsSolicitados) {
        if (itemsSolicitados == null || itemsSolicitados.isEmpty()) {
            throw new CarritoVacioException("El pedido debe tener al menos un producto");
        }

        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        List<PedidoItem> items = itemsSolicitados.stream()
            .map(this::resolverItem)
            .collect(Collectors.toList());

        double total = items.stream().mapToDouble(item -> item.subtotal).sum();

        Pedido pedido = new Pedido();
        pedido.usuario = usuario;
        pedido.items = items;
        pedido.total = total;
        pedido.estado = "CONFIRMADO";

        return pedidoRepository.save(pedido);
    }

    private PedidoItem resolverItem(ItemPedidoRequest solicitado) {
        Producto producto = productoService.obtenerPorId(solicitado.productoId)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "Producto no encontrado: " + solicitado.productoId));

        double precio = producto.getPrecioConDescuento();
        double subtotal = precio * solicitado.cantidad;
        return new PedidoItem(producto.id, producto.nombre, precio, solicitado.cantidad, subtotal);
    }

    public List<Pedido> obtenerPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return pedidoRepository.findByUsuario_IdOrderByFechaDesc(usuario.id);
    }

    public Pedido obtenerPorId(Long id, String emailUsuario) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        if (!pedido.usuario.email.equals(emailUsuario)) {
            throw new AccesoNoAutorizadoException("No tenés acceso a este pedido");
        }

        return pedido;
    }
}
