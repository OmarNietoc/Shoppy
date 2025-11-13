package com.onieto.order.controller.response;

import com.onieto.order.model.Coupon;
import com.onieto.order.model.OrderItem;
import com.onieto.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long userId;
    private OrderStatus estado;
    private CouponResponse coupon;
    private BigDecimal finalPrice;
    private LocalDateTime orderDate;
    private List<OrderItem> items;

}