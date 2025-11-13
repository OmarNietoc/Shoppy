package com.onieto.payments.dto;

import com.onieto.payments.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDto {

    @NotNull(message = "El estado es obligatorio.")
    private PaymentStatus status;
}
