package com.onieto.catalog.config;

import com.onieto.catalog.model.Category;
import com.onieto.catalog.model.Product;
import com.onieto.catalog.model.Unit;
import com.onieto.catalog.repository.CategoryRepository;
import com.onieto.catalog.repository.ProductRepository;
import com.onieto.catalog.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Category frutas = categoryRepository.findByNameIgnoreCase("frutas")
                .orElseGet(() -> categoryRepository.save(new Category("frutas")));

        Unit kilogramo = unitRepository.findByNameIgnoreCase("kg")
                .orElseGet(() -> unitRepository.save(new Unit("kg")));

        Product producto = Product.builder()
                .id("FR002")
                .nombre("Naranjas Valencia")
                .descripcion("Jugosas y ricas en vitamina C, ideales para zumos frescos y refrescantes. Cultivadas en condiciones climáticas óptimas.")
                .precio(BigDecimal.valueOf(1000))
                .categoria(frutas)
                .unid(kilogramo)
                .imagen(null)
                .build();

        productRepository.save(producto);
    }
}
