package com.onieto.payments.dto;

import lombok.Data;



@Data
public class EnrollmentDto {
    private Long id;
    private Long userId;
    private Long courseId;
    private String cuponCode;
    private Integer FinalPrice;


}
