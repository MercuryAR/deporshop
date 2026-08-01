package com.deporshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemPedidoRequest {

    @NotNull(message = "El producto es obligatorio")
    public Long productoId;

    @Positive(message = "La cantidad debe ser mayor a 0")
    public int cantidad;
}
