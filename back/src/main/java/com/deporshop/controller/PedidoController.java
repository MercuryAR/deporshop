package com.deporshop.controller;

import com.deporshop.dto.CrearPedidoRequest;
import com.deporshop.dto.PedidoResponse;
import com.deporshop.model.Pedido;
import com.deporshop.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody CrearPedidoRequest request,
                                                 Authentication authentication) {
        Pedido pedido = pedidoService.crear(authentication.getName(), request.items);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.desde(pedido));
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponse>> obtenerMisPedidos(Authentication authentication) {
        List<PedidoResponse> pedidos = pedidoService.obtenerPorUsuario(authentication.getName()).stream()
            .map(PedidoResponse::desde)
            .collect(Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id, Authentication authentication) {
        Pedido pedido = pedidoService.obtenerPorId(id, authentication.getName());
        return ResponseEntity.ok(PedidoResponse.desde(pedido));
    }
}
