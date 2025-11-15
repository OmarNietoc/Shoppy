package com.onieto.catalog.config;

import com.onieto.catalog.model.Category;
import com.onieto.catalog.model.Product;
import com.onieto.catalog.model.Unit;
import com.onieto.catalog.repository.CategoryRepository;
import com.onieto.catalog.repository.ProductRepository;
import com.onieto.catalog.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductRepository productRepository;

    private static final List<ProductSeed> PRODUCT_SEEDS = List.of(
            new ProductSeed(
                    "FR001",
                    "Manzanas Fuji",
                    "Crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres.",
                    1200,
                    "frutas",
                    "kg",
                    "apples2.jpg",
                    15,
                    25
            ),
            new ProductSeed(
                    "FR002",
                    "Naranjas Valencia",
                    "Jugosas y ricas en vitamina C, ideales para zumos frescos y refrescantes. Cultivadas en condiciones climaticas optimas.",
                    1000,
                    "frutas",
                    "kg",
                    "oranges2.jpg",
                    5,
                    25
            ),
            new ProductSeed(
                    "FR003",
                    "Platanos Cavendish",
                    "Maduros y dulces, perfectos para el desayuno o como snack energetico. Ricos en potasio y vitaminas.",
                    800,
                    "frutas",
                    "kg",
                    "bananas.jpg",
                    5,
                    22
            ),
            new ProductSeed(
                    "VR001",
                    "Zanahorias Organicas",
                    "Cultivadas sin pesticidas en la Region de O'Higgins. Excelente fuente de vitamina A y fibra, ideales para ensaladas y jugos.",
                    900,
                    "verduras",
                    "kg",
                    "carrots.jpg",
                    5,
                    14
            ),
            new ProductSeed(
                    "VR002",
                    "Espinacas Frescas",
                    "Frescas y nutritivas, perfectas para ensaladas y batidos verdes. Cultivadas bajo practicas organicas que garantizan su calidad.",
                    700,
                    "verduras",
                    "kg",
                    "spinach.jpg",
                    15,
                    52
            ),
            new ProductSeed(
                    "VR003",
                    "Pimientos Tricolores",
                    "Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos. Ricos en antioxidantes y vitaminas.",
                    1500,
                    "verduras",
                    "kg",
                    "peppers.jpg",
                    5,
                    45
            ),
            new ProductSeed(
                    "PO001",
                    "Miel Organica",
                    "Miel pura y organica producida por apicultores locales. Rica en antioxidantes y con un sabor inigualable.",
                    5000,
                    "organicos",
                    "500g",
                    "honey.jpg",
                    10,
                    15
            ),
            new ProductSeed(
                    "PO003",
                    "Quinua Organica",
                    "Quinua organica de alta calidad, rica en proteinas y nutrientes esenciales. Perfecta para una alimentacion saludable.",
                    3500,
                    "organicos",
                    "kg",
                    "quinoa.jpg",
                    5,
                    13
            ),
            new ProductSeed(
                    "PL001",
                    "Leche Entera",
                    "Leche entera fresca de vacas criadas en praderas naturales. Rica en calcio y vitaminas esenciales.",
                    1800,
                    "lacteos",
                    "L",
                    "milk.jpg",
                    5,
                    22
            )
    );

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Map<String, Category> categoryCache = new HashMap<>();
        Map<String, Unit> unitCache = new HashMap<>();

        PRODUCT_SEEDS.stream()
                .map(seed -> Product.builder()
                        .id(seed.id())
                        .nombre(seed.nombre())
                        .descripcion(seed.descripcion())
                        .precio(seed.precio())
                        .stock(seed.stock())
                        .stockMinimo(seed.stockMinimo())
                        .activo(Product.DEFAULT_ACTIVO)
                        .categoria(resolveCategory(seed.categoria(), categoryCache))
                        .unid(resolveUnit(seed.unidad(), unitCache))
                        .imagen(loadImage("img/products/" + seed.imagen()))
                        .build())
                .forEach(productRepository::save);
    }

    private Category resolveCategory(String categoryName, Map<String, Category> cache) {
        return cache.computeIfAbsent(categoryName, name ->
                categoryRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> categoryRepository.save(new Category(name))));
    }

    private Unit resolveUnit(String unitName, Map<String, Unit> cache) {
        return cache.computeIfAbsent(unitName, name ->
                unitRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> unitRepository.save(new Unit(name))));
    }

    private byte[] loadImage(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                throw new IllegalStateException("No se encontro la imagen " + path);
            }
            return resource.getContentAsByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer la imagen " + path, e);
        }
    }

    private record ProductSeed(
            String id,
            String nombre,
            String descripcion,
            int precio,
            String categoria,
            String unidad,
            String imagen,
            int stockMinimo,
            int stock
    ) {
    }
}
