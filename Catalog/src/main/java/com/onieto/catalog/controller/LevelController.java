package com.onieto.catalog.controller;

import com.onieto.catalog.controller.response.MessageResponse;
import com.onieto.catalog.model.Level;
import com.onieto.catalog.service.LevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    // GET: Obtener todos los niveles
    @Operation(
            summary = "Listar niveles",
            description = "Devuelve una lista con todos los niveles registrados en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Niveles listados exitosamente")
    })

    @GetMapping
    public ResponseEntity<List<Level>> getAllLevels() {
        return ResponseEntity.ok(levelService.getAllLevels());
    }

    // GET: Obtener nivel por ID
    @Operation(
            summary = "Obtener nivel por ID",
            description = "Busca y devuelve un nivel específico a partir de su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nivel encontrado"),
            @ApiResponse(responseCode = "404", description = "Nivel no encontrado")
    })

    @GetMapping("/{id}")
    public ResponseEntity<?> getLevelById(@PathVariable Long id) {
        Level level = levelService.getLevelById(id);
        return ResponseEntity.ok(level);
    }

    // POST: Crear nivel
    @Operation(
            summary = "Crear nuevo nivel",
            description = "Crea un nuevo nivel validando que no exista otro con el mismo nombre."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nivel creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Nombre del nivel ya existente")
    })

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> createLevel(@Valid @RequestBody Level level) {
        levelService.createLevel(level);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Level creado exitosamente."));
    }

    // PUT: Actualizar nivel
    @Operation(
            summary = "Actualizar nivel",
            description = "Actualiza la información de un nivel existente por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nivel actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Nombre duplicado o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Nivel no encontrado")
    })

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateLevel(@PathVariable Long id, @Valid @RequestBody Level updatedLevel) {
        levelService.updateLevel(id, updatedLevel);
        return ResponseEntity.ok(new MessageResponse("Level actualizado exitosamente."));
    }

    // DELETE: Eliminar nivel
    @Operation(
            summary = "Eliminar nivel",
            description = "Elimina un nivel por ID, solo si no tiene cursos asociados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nivel eliminado exitosamente"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar el nivel porque hay cursos asociados"),
            @ApiResponse(responseCode = "404", description = "Nivel no encontrado")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteLevel(@PathVariable Long id) {
        levelService.deleteLevel(id);
        return ResponseEntity.ok(new MessageResponse("Level eliminado exitosamente."));
    }
}