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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100,message = "El título debe tener entre 5 y 100 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, message = "La descripción debe tener al menos 10 caracteres")
    private String description;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;

    @NotNull(message = "El ID del nivel es obligatorio")
    private Long levelId;

    @NotNull(message = "El ID del instructor es obligatorio")
    private Long instructorId;

    @PositiveOrZero
    private BigDecimal price;

    private List<@Size(min = 1, max = 20) String> tags;
}

