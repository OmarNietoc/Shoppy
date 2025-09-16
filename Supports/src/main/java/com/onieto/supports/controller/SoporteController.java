package com.onieto.supports.controller;

import com.onieto.supports.dto.EstadoDTO;
import com.onieto.supports.dto.SoporteRequestDTO;
import com.onieto.supports.model.Soporte;
import com.onieto.supports.service.SoporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supports")
public class SoporteController {

    @Autowired
    private SoporteService soporteService;

    @Operation(
            summary = "Crear una solicitud de soporte",
            description = "Registra una nueva solicitud de soporte con los datos del usuario que la reporta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud de soporte creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })

    @PostMapping
    public ResponseEntity<Soporte> crear(@Valid @RequestBody SoporteRequestDTO dto) {

        return ResponseEntity.status(201).body(soporteService.crear(dto));
    }

    @Operation(
            summary = "Obtener todas las solicitudes de soporte",
            description = "Devuelve una lista con todas las solicitudes de soporte registradas en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida exitosamente")
    })

    @GetMapping("/api/supports/all")
    public List<Soporte> obtenerTodas() {
        return soporteService.obtenerTodas();
    }

    @Operation(
            summary = "Obtener solicitud de soporte por ID",
            description = "Recupera una solicitud de soporte específica utilizando su ID único."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })

    @GetMapping("/api/supports/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(soporteService.ObtenerSoportePorId(id));
    }

    @Operation(
            summary = "Eliminar una solicitud de soporte",
            description = "Elimina una solicitud de soporte del sistema usando su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        soporteService.eliminar(id);
        return ResponseEntity.ok("Soporte eliminado");
    }

    @Operation(
            summary = "Actualizar el estado de una solicitud de soporte",
            description = "Modifica el estado actual de una solicitud (por ejemplo: 'EN_PROGRESO', 'RESUELTO'). Solo disponible para usuarios encargados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para el estado"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })

    @PatchMapping("/{id}")
    public Soporte actualizarEstado(@PathVariable Long id, @Valid @RequestBody EstadoDTO dto) {
        return soporteService.actualizarEstado(id, dto.getEstado());
    }

}
