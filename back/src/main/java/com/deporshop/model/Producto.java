package com.deporshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "productos")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    public String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false)
    public String descripcion;

    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    public double precio;

    @PositiveOrZero(message = "El descuento no puede ser negativo")
    @Column(columnDefinition = "DOUBLE DEFAULT 0")
    public double descuento;

    @Column(name = "imagen_url")
    public String imagenUrl;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    public int stock;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    public Categoria categoria;

    @Column(columnDefinition = "DOUBLE DEFAULT 0")
    public double rating;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    public int resenas;

    public Producto() {
    }

    public Producto(String nombre, String descripcion, double precio, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.descuento = 0;
        this.stock = 10;
        this.rating = 0;
        this.resenas = 0;
    }

    public double calcularDescuento() {
        return precio * 0.90;
    }

    public double getPrecioConDescuento() {
        return precio - (precio * descuento / 100);
    }
}
