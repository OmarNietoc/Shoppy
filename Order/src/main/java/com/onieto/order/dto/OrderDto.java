package com.onieto.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    @NotNull(message = "El userId no puede ser nulo")
    private Long userId;

    @NotNull(message = "El OrderId no puede ser nulo")
    private Long orderId;

    // Código de cupón opcional
    private String couponCode;


}
