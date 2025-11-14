package com.onieto.order.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El email del usuario es obligatorio")
    @Email(message = "El email del usuario debe ser válido")
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    private OrderStatus estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    @Nullable
    private Coupon coupon;

    @NotNull(message = "El precio final no puede ser nulo")
    private BigDecimal finalPrice;

    @NotNull(message = "El descuento aplicado no puede ser nulo")
    private BigDecimal discountApplied;

    @NotNull(message = "La fecha de Orden es obligatoria")
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items;



}
