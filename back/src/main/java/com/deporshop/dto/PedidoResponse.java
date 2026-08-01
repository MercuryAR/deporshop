package com.deporshop.dto;

import com.deporshop.model.Pedido;
import com.deporshop.model.PedidoItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponse {
    public Long id;
    public UsuarioResponse usuario;
    public List<PedidoItem> items;
    public double total;
    public String estado;
    public LocalDateTime fecha;

    public PedidoResponse() {
    }

    public static PedidoResponse desde(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.id = pedido.id;
        response.usuario = UsuarioResponse.desde(pedido.usuario);
        response.items = pedido.items;
        response.total = pedido.total;
        response.estado = pedido.estado;
        response.fecha = pedido.fecha;
        return response;
    }
}
