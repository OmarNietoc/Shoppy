package com.onieto.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    @NotBlank(message = "El id es obligatorio")
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, message = "La descripción debe tener al menos 20 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero
    private BigDecimal precio;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;

    @NotNull(message = "El ID de la unidad es obligatorio")
    private Long unitId;

    private byte[] imagen;
}
