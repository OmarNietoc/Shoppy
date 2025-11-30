package com.onieto.catalog.config;

import com.onieto.catalog.model.Category;
import com.onieto.catalog.model.Product;
import com.onieto.catalog.model.Unit;
import com.onieto.catalog.repository.CategoryRepository;
import com.onieto.catalog.repository.ProductRepository;
import com.onieto.catalog.repository.UnitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;

    public DatabaseInitializer(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               UnitRepository unitRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("📦 Iniciando carga de productos (Idempotente)...");

        // --- Frutas ---
        crearProducto(
                "FR001",
                "Manzanas Fuji",
                "Crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres.",
                1200,
                "apples2.jpg",
                "frutas",
                "kg",
                "Oferta"
        );

        crearProducto(
                "FR002",
                "Naranjas Valencia",
                "Jugosas y ricas en vitamina C, ideales para zumos frescos y refrescantes. Cultivadas en condiciones climáticas óptimas.",
                1000,
                "oranges2.jpg",
                "frutas",
                "kg",
                null
        );

        crearProducto(
                "FR003",
                "Plátanos Cavendish",
                "Maduros y dulces, perfectos para el desayuno o como snack energético. Ricos en potasio y vitaminas.",
                800,
                "bananas.jpg",
                "frutas",
                "kg",
                null
        );

        // --- Verduras ---
        crearProducto(
                "VR001",
                "Zanahorias Orgánicas",
                "Cultivadas sin pesticidas en la Región de O'Higgins. Excelente fuente de vitamina A y fibra, ideales para ensaladas y jugos.",
                900,
                "carrots.jpg",
                "verduras",
                "kg",
                null
        );

        crearProducto(
                "VR002",
                "Espinacas Frescas",
                "Frescas y nutritivas, perfectas para ensaladas y batidos verdes. Cultivadas bajo prácticas orgánicas que garantizan su calidad.",
                700,
                "spinach.jpg",
                "verduras",
                "kg",
                "Nuevo"
        );

        crearProducto(
                "VR003",
                "Pimientos Tricolores",
                "Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos. Ricos en antioxidantes y vitaminas.",
                1500,
                "peppers.jpg",
                "verduras",
                "kg",
                "Nuevo"
        );

        // --- Organicos ---
        crearProducto(
                "PO001",
                "Miel Orgánica",
                "Miel pura y orgánica producida por apicultores locales. Rica en antioxidantes y con un sabor inigualable.",
                5000,
                "honey.jpg",
                "organicos",
                "500g",
                null
        );

        crearProducto(
                "PO003",
                "Quinua Orgánica",
                "Quinua orgánica de alta calidad, rica en proteínas y nutrientes esenciales. Perfecta para una alimentación saludable.",
                3500,
                "quinoa.jpg",
                "organicos",
                "kg",
                null
        );

        // --- Lacteos ---
        crearProducto(
                "PL001",
                "Leche Entera",
                "Leche entera fresca de vacas criadas en praderas naturales. Rica en calcio y vitaminas esenciales.",
                1800,
                "milk.jpg",
                "lacteos",
                "L",
                null
        );

        System.out.println("✅ Carga de productos finalizada.");
    }

    private void crearProducto(String id, String nombre, String descripcion, Integer precio,
                               String imageName, String categoriaNombre, String unidadNombre, String oferta) {

        // Si el producto ya existe, no hacer nada (idempotente sin actualizar registros existentes)
        if (productRepository.existsById(id)) {
            // System.out.println("⏭️ Producto " + nombre + " ya existe. Saltando...");
            return;
        }

        try {
            // 2. Buscar Entidades Relacionadas (Categoria y Unidad)
            Optional<Category> categoriaOpt = categoryRepository.findByName(categoriaNombre);
            if (categoriaOpt.isEmpty()) {
                System.err.println("⚠️ Categoría no encontrada: " + categoriaNombre);
                return;
            }

            Optional<Unit> unitOpt = unitRepository.findByName(unidadNombre);
            if (unitOpt.isEmpty()) {
                System.err.println("⚠️ Unidad no encontrada: " + unidadNombre);
                return;
            }

            byte[] imageBytes = loadImageFromResources(imageName);

            // 4. Construir y Guardar el Producto
            Product product = Product.builder()
                    .id(id)
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .precio(precio)
                    .stock(100)       // Requisito: Stock 100
                    .stockMinimo(15)  // Requisito: Stock Mínimo 15
                    .activo(1)        // Default activo
                    .categoria(categoriaOpt.get())
                    .unid(unitOpt.get())
                    .imagen(imageBytes) // Guardamos los bytes directos
                    .oferta(oferta)
                    .build();

            productRepository.save(product);
            System.out.println("✅ Producto creado: " + nombre);

        } catch (Exception e) {
            System.err.println("❌ Error crítico creando producto " + nombre + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private byte[] loadImageFromResources(String imageName) {
        // Primero busca en la carpeta img/products (donde están las imágenes reales)
        String[] candidatePaths = {"img/products/" + imageName, "img/" + imageName};
        for (String candidatePath : candidatePaths) {
            try {
                ClassPathResource imgFile = new ClassPathResource(candidatePath);
                if (imgFile.exists()) {
                    return StreamUtils.copyToByteArray(imgFile.getInputStream());
                }
            } catch (IOException e) {
                System.err.println("❌ Error leyendo imagen " + imageName + " desde " + candidatePath + ": " + e.getMessage());
            }
        }

        System.err.println("⚠️ Imagen no encontrada en resources/img/ ni img/products/: " + imageName);
        return null;
    }
}
