package com.onieto.catalog.config;

import com.onieto.catalog.model.*;
import com.onieto.catalog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final LevelRepository levelRepository;
    private final CouponRepository couponRepository;
    private final EnrollmentRepository enrollmentRepository;



    @Override
    public void run(String... args) throws Exception {
        // Crear categorías
        Category dev = categoryRepository.save(new Category("Desarrollo"));
        Category legal = categoryRepository.save(new Category("Legal"));
        Category seguridad = categoryRepository.save(new Category("Seguridad y Vigilancia"));
        Category negocios = categoryRepository.save(new Category("Negocios y Estrategia"));
        Category ciencia = categoryRepository.save(new Category("Ciencia e Ingeniería"));

        // Crear niveles
        Level beginner = levelRepository.save(new Level("Principiante"));
        Level intermediate = levelRepository.save(new Level("Intermedio"));
        Level advanced = levelRepository.save(new Level("Avanzado"));

        // Crear cursos
        Course curso1 = Course.builder()
                .title("Java para principiantes con ejemplos legales")
                .description("Aprende Java desde cero con casos legales prácticos. Ideal para quienes quieren defender su código.")
                .price(BigDecimal.valueOf(49.99))
                .category(legal)
                .level(beginner)
                .instructorId(12L) // Por ejemplo, instructor ID de Saul Goodman
                .tags(Arrays.asList("java", "legal", "poo"))
                .build();

        Course curso2 = Course.builder()
                .title("Seguridad avanzada para instalaciones de pollos")
                .description("Curso enfocado en la seguridad y vigilancia de instalaciones de producción alimentaria altamente secretas.")
                .price(BigDecimal.valueOf(79.99))
                .category(seguridad)
                .level(advanced)
                .instructorId(14L) // Por ejemplo, instructor ID de Mike Ehrmantraut
                .tags(Arrays.asList("seguridad", "vigilancia", "estrategia"))
                .build();

        Course curso3 = Course.builder()
                .title("Negociación Estratégica y Persuasión")
                .description("Aprende técnicas avanzadas de negociación inspiradas en grandes personajes de la industria legal y comercial.")
                .price(BigDecimal.valueOf(59.99))
                .category(negocios)
                .level(intermediate)
                .instructorId(13L)
                .tags(Arrays.asList("negociación", "persuasión", "estrategia"))
                .build();

        Course curso4 = Course.builder()
                .title("Ciencia Interdimensional e Inventiva")
                .description("Explora teorías y experimentos poco convencionales que desafían la física moderna.")
                .price(BigDecimal.valueOf(99.99))
                .category(ciencia)
                .level(advanced)
                .instructorId(15L) // Rick
                .tags(Arrays.asList("ciencia", "inventos", "realidades alternas"))
                .build();

        Course curso5 = Course.builder()
                .title("Hacking Ético para Instructores con Pasado Turbio")
                .description("Aprende técnicas de hacking ético con casos basados en experiencias poco tradicionales.")
                .price(BigDecimal.valueOf(69.99))
                .category(seguridad)
                .level(intermediate)
                .instructorId(16L) // Gus
                .tags(Arrays.asList("hacking", "ético", "seguridad"))
                .build();

        List<Course> courses = courseRepository.saveAll(Arrays.asList(curso1, curso2, curso3, curso4, curso5));

// Crear 12 cupones
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Coupon coupon = Coupon.builder()
                    .code("DESC" + String.format("%02d", i))
                    .discountAmount(BigDecimal.valueOf(5 + i)) // de 6.00 a 17.00
                    .active(true) // todos inician como activos
                    .build();
            coupons.add(coupon);
        }
        coupons = couponRepository.saveAll(coupons);

    }
}
