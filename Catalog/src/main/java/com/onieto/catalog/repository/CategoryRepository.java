package com.onieto.catalog.repository;

import com.onieto.catalog.model.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(@NotBlank(message = "El nombre de la categoría es obligatorio") String name);

    boolean existsByNameIgnoreCaseAndIdNot(@NotBlank(message = "El nombre de la categoría es obligatorio") String name, Long id);
}
