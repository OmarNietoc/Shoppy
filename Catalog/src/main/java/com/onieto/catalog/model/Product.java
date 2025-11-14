package com.onieto.catalog.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(length = 20, nullable = false, unique = true)
    @NotBlank(message = "El id del producto es obligatorio")
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, message = "La descripción debe tener al menos 20 caracteres")
    @Column(length = 1000)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private BigDecimal precio;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "La categoría es obligatoria")
    private Category categoria;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    @NotNull(message = "La unidad es obligatoria")
    private Unit unid;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] imagen;
}
