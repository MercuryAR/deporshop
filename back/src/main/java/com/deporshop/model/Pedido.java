package com.deporshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @ElementCollection
    @CollectionTable(name = "pedido_items", joinColumns = @JoinColumn(name = "pedido_id"))
    public List<PedidoItem> items = new ArrayList<>();

    @Column(nullable = false)
    public double total;

    @Column(nullable = false)
    public String estado;

    @Column(nullable = false)
    public LocalDateTime fecha;

    public Pedido() {
    }

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "CONFIRMADO";
        }
    }
}
