package com.onieto.order.service;

import com.onieto.order.client.ProductClient;
import com.onieto.order.controller.response.OrderResponse;
import com.onieto.order.controller.response.UserResponseDto;
import com.onieto.order.dto.OrderDto;
import com.onieto.order.dto.OrderItemRequestDto;
import com.onieto.order.dto.ProductResponseDto;
import com.onieto.order.model.Coupon;
import com.onieto.order.model.Order;
import com.onieto.order.model.OrderItem;
import com.onieto.order.model.OrderStatus;
import com.onieto.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private UserValidatorService userValidatorService;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_WithValidPayload_ShouldPersistAndDisableCoupon() {
        OrderDto dto = buildOrderDto("SAVE5");
        UserResponseDto user = new UserResponseDto(1L, "Ana", "ana@shop.com", null, 1);
        Coupon coupon = Coupon.builder()
                .id(10L)
                .code("SAVE5")
                .discountAmount(new BigDecimal("5.00"))
                .active(true)
                .build();

        when(userValidatorService.getUserByEmail(dto.getUserEmail())).thenReturn(user);
        when(couponService.getCouponByCode("SAVE5")).thenReturn(coupon);
        when(productClient.getProductById("SKU-1")).thenReturn(buildProduct("SKU-1", "10.00"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<OrderResponse> response = orderService.createOrder(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ana@shop.com", response.getBody().getUserEmail());
        assertEquals(new BigDecimal("5.00"), response.getBody().getDiscountApplied());
        assertEquals(new BigDecimal("5.00"), response.getBody().getFinalPrice());
        verify(orderRepository).save(any(Order.class));
        verify(couponService).updateCouponStatusByCode("SAVE5", false);
    }

    @Test
    void updateOrder_WhenCouponCodeMissing_ShouldReuseExistingCoupon() {
        Long orderId = 4L;
        Coupon existingCoupon = Coupon.builder()
                .id(3L)
                .code("KEEP")
                .discountAmount(new BigDecimal("3.00"))
                .active(true)
                .build();
        Order existingOrder = Order.builder()
                .id(orderId)
                .userEmail("stored@shop.com")
                .estado(OrderStatus.PAID)
                .coupon(existingCoupon)
                .finalPrice(new BigDecimal("7.00"))
                .discountApplied(new BigDecimal("3.00"))
                .orderDate(LocalDateTime.of(2024, 1, 1, 10, 0))
                .items(List.of())
                .build();

        OrderDto dto = buildOrderDto(null);
        UserResponseDto user = new UserResponseDto(9L, "Luis", dto.getUserEmail(), null, 1);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(userValidatorService.getUserByEmail(dto.getUserEmail())).thenReturn(user);
        when(productClient.getProductById("SKU-1")).thenReturn(buildProduct("SKU-1", "10.00"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<OrderResponse> response = orderService.updateOrder(orderId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.PAID, response.getBody().getEstado());
        assertEquals("KEEP", response.getBody().getCoupon().getCode());
        assertEquals(new BigDecimal("3.00"), response.getBody().getDiscountApplied());
        verify(couponService, never()).getCouponByCode(anyString());
        verify(couponService, never()).updateCouponStatusByCode(anyString(), anyBoolean());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderStatusById_ShouldPersistStatusChange() {
        Order order = Order.builder()
                .id(8L)
                .estado(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = orderService.updateOrderStatusById(order.getId(), OrderStatus.SHIPPED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.SHIPPED, order.getEstado());
        verify(orderRepository).save(order);
    }

    @Test
    void getAllOrders_ShouldReturnMappedResponses() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("ACTIVE")
                .discountAmount(new BigDecimal("2.00"))
                .active(true)
                .build();
        OrderItem item = OrderItem.builder()
                .id(2L)
                .productId("SKU-1")
                .productName("Keyboard")
                .productDescription("Mechanical keyboard")
                .unitPrice(new BigDecimal("20.00"))
                .quantity(1)
                .subtotal(new BigDecimal("20.00"))
                .build();
        Order order = Order.builder()
                .id(5L)
                .userEmail("buyer@shop.com")
                .estado(OrderStatus.PENDING)
                .coupon(coupon)
                .finalPrice(new BigDecimal("18.00"))
                .discountApplied(new BigDecimal("2.00"))
                .orderDate(LocalDateTime.now())
                .items(List.of(item))
                .build();
        item.setOrder(order);

        when(orderRepository.findAll()).thenReturn(List.of(order));

        ResponseEntity<List<OrderResponse>> response = orderService.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("buyer@shop.com", response.getBody().get(0).getUserEmail());
        assertEquals("ACTIVE", response.getBody().get(0).getCoupon().getCode());
        verify(orderRepository).findAll();
    }

    private OrderDto buildOrderDto(String couponCode) {
        OrderDto dto = new OrderDto();
        dto.setUserEmail("ana@shop.com");
        dto.setItems(List.of(new OrderItemRequestDto("SKU-1", 1)));
        dto.setCouponCode(couponCode);
        return dto;
    }

    private ProductResponseDto buildProduct(String id, String price) {
        ProductResponseDto product = new ProductResponseDto();
        product.setId(id);
        product.setNombre("Sample");
        product.setDescripcion("Sample product");
        product.setPrecio(new BigDecimal(price));
        product.setImagen(new byte[]{1, 2, 3});
        return product;
    }
}
