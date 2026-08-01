package com.deporshop.repository;

import com.deporshop.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuario_IdOrderByFechaDesc(Long usuarioId);
}
