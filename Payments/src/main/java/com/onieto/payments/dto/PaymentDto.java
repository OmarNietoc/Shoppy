package com.onieto.payments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentDto {


    @NotNull(message = "El ID de usuario no puede ser nulo")
    private Long enrollmentId;

}
