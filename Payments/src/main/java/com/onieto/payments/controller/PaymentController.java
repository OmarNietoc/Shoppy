package com.onieto.payments.controller;

import com.onieto.payments.controller.response.MessageResponse;
import com.onieto.payments.dto.PaymentDto;
import com.onieto.payments.model.Payment;
import com.onieto.payments.model.PaymentStatus;
import com.onieto.payments.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Obtener todos los pagos
    @Operation(
            summary = "Obtener todos los pagos",
            description = "Retorna una lista con todos los pagos registrados en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    })

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // Obtener un pago por ID
    @Operation(
            summary = "Obtener pago por ID",
            description = "Busca un pago en el sistema utilizando su ID único."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // Crear un nuevo pago
    @Operation(
            summary = "Crear un nuevo pago",
            description = "Registra un nuevo pago en el sistema con los datos proporcionados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para crear el pago")
    })

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> createPayment(@Valid @RequestBody PaymentDto dto) {
        return paymentService.createPayment(dto);
    }

    @Operation(
            summary = "Actualizar un pago existente",
            description = "Modifica los datos de un pago existente identificado por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentDto paymentDto) {
        return paymentService.updatePayment(id, paymentDto);
    }

    @Operation(
            summary = "Actualizar estado de pago",
            description = "Modifica el estado de un pago existente (por ejemplo: PENDIENTE, COMPLETADO, CANCELADO)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del pago actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado o estado inválido")
    })

    @PatchMapping("/{id}/status/{newStatus}")
    public ResponseEntity<MessageResponse> updatePaymentStatus(
            @PathVariable Long id,
            @PathVariable PaymentStatus newStatus) {

        return paymentService.updatePaymentStatus(id, newStatus);
    }
    // Eliminar un pago
    @Operation(
            summary = "Eliminar un pago",
            description = "Elimina un pago del sistema utilizando su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletePayment(@PathVariable Long id) {
        paymentService.deletePaymentById(id);
        return ResponseEntity.ok(new MessageResponse("Pago eliminado exitosamente."));
    }
}
