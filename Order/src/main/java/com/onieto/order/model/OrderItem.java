package com.onieto.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    // Datos cacheados al añadir al carrito
    private String productName;
    private BigDecimal unitPrice;
    private String productImageUrl;

    @NotNull(message = "La cantidad no puede ser nula")
    private Integer quantity;

    @Column(name = "subtotal", insertable = false, updatable = false)
    private BigDecimal subtotal;

    // Metodo para cachear datos del producto
    public void cacheProductData(Product product) {
        this.productName = product.getName();
        this.productDescription = product.getDescription();
        this.unitPrice = product.getPrice();
        this.productImageUrl = product.getImageUrl();
    }
}
