package com.onieto.users.controller;

import com.onieto.users.controller.response.MessageResponse;
import com.onieto.users.dto.UserDto;
import com.onieto.users.repository.RoleRepository;
import com.onieto.users.service.RoleService;
import com.onieto.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.onieto.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import com.onieto.users.model.User;
import com.onieto.users.model.Role;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Listar todos los usuarios", description = "Retorna todos los usuarios registrados en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios listados correctamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna los detalles de un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Agregar nuevo usuario", description = "Crea un nuevo usuario en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos enviados en la solicitud")
    })
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addUser(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Usuario agregado exitosamente."));
    }

    @Operation(summary = "Actualizar usuario por ID", description = "Actualiza la información de un usuario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDetails) {
        userService.updateUser(id, userDetails);
        return ResponseEntity.ok(new MessageResponse("Usuario actualizado exitosamente."));
    }

    @Operation(summary = "Eliminar usuario por ID", description = "Elimina un usuario específico de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(new MessageResponse("Usuario eliminado correctamente."));
    }

    @Operation(summary = "Actualizar estado del usuario", description = "Actualiza el estado (activo/inactivo) de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del usuario actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (status < 0 || status > 1) {
            return ResponseEntity.badRequest().body(new MessageResponse("El 'status' debe ser 1 o 0."));
        }
        userService.updateUserStatus(id, status);
        return ResponseEntity.ok(new MessageResponse("Estado del usuario actualizado correctamente."));
    }


}
