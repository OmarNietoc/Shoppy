package com.onieto.catalog.controller;

import com.onieto.catalog.controller.response.MessageResponse;
import com.onieto.catalog.dto.CourseDto;
import com.onieto.catalog.model.Course;
import com.onieto.catalog.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(
            summary = "Listar cursos con paginación y filtros opcionales",
            description = "Obtiene una lista paginada de cursos, con opción de filtrar por categoría o nivel."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cursos listados exitosamente"),
            @ApiResponse(responseCode = "204", description = "No se encontraron cursos")
    })
    @GetMapping
    public ResponseEntity<?> getCourses(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long levelId) {

        Page<Course> courses = courseService.getCourses(page, size, categoryId, levelId);
        List<Course> content = courses.getContent();
        if (content.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(content);
    }

    @Operation(
            summary = "Obtener curso por ID",
            description = "Devuelve el detalle de un curso específico según su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @Operation(
            summary = "Crear nuevo curso",
            description = "Registra un nuevo curso a partir de un objeto CourseDto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Curso creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseDto dto) {
        return courseService.createCourse(dto);
    }

    @Operation(
            summary = "Actualizar curso existente",
            description = "Actualiza los detalles de un curso a partir de su ID y un objeto CourseDto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDto courseDto) {
        return courseService.updateCourse(id, courseDto);
    }

    @Operation(
            summary = "Eliminar curso",
            description = "Elimina un curso por su ID si no está asociado a inscripciones activas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto: el curso tiene inscripciones asociadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id);
    }
}
