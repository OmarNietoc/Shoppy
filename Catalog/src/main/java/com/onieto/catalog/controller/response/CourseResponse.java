package com.onieto.catalog.controller.response;

import com.onieto.catalog.model.Category;
import com.onieto.catalog.model.Level;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private Level level;
    private Long instructorId; // Se obtiene del microservicio de usuarios
    private BigDecimal price;
    private List<@Size(min = 1, max = 20) String> tags;

}