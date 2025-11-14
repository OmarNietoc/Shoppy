package com.onieto.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponseDto {
    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private byte[] imagen;
}
