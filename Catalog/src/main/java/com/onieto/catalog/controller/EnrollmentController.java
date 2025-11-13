package com.onieto.catalog.controller;

import com.onieto.catalog.controller.response.EnrollmentResponse;
import com.onieto.catalog.controller.response.MessageResponse;
import com.onieto.catalog.dto.EnrollmentDto;
import com.onieto.catalog.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(
            summary = "Listar inscripciones",
            description = "Obtiene una lista con todas las inscripciones realizadas, incluyendo información del curso y usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscripciones listadas exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @Operation(
            summary = "Obtener inscripción por ID",
            description = "Devuelve los detalles de una inscripción específica a partir de su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscripción encontrada"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {

        return enrollmentService.getEnrollmentDtoById(id);
    }

    @Operation(
            summary = "Crear nueva inscripción",
            description = "Registra una nueva inscripción a un curso con un usuario, validando cupones si corresponde."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inscripción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o cupón inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario o curso no encontrado")
    })
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> createEnrollment(@Valid @RequestBody EnrollmentDto dto) {
        return enrollmentService.createEnrollment(dto);
    }

    @Operation(
            summary = "Actualizar inscripción",
            description = "Actualiza los datos de una inscripción específica, incluyendo usuario, curso y cupón."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscripción actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Inscripción, curso o usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateEnrollment(@PathVariable Long id, @Valid @RequestBody EnrollmentDto dto) {
        return enrollmentService.updateEnrollment(id, dto);
    }

    @Operation(
            summary = "Eliminar inscripción",
            description = "Elimina una inscripción existente por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscripción eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteEnrollment(@PathVariable Long id) {
        return enrollmentService.deleteEnrollment(id);
    }

    @Operation(
            summary = "Actualizar estado de inscripción",
            description = "Cambia el estado de una inscripción (activa/inactiva) por ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de inscripción actualizado"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam boolean active
    ) {
        enrollmentService.updateEnrollmentStatusById(id, active);
        return ResponseEntity.ok(new MessageResponse("Estado de inscripción actualizado exitosamente:" + active));
    }

}