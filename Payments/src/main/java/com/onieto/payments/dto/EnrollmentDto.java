package com.onieto.payments.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnrollmentDto {
    private Long id;
    private Long userId;
    private Long courseId;
    private String cuponCode;
    private BigDecimal FinalPrice;


}
