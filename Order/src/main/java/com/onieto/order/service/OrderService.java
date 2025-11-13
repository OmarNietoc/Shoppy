package com.onieto.order.service;

import com.onieto.order.controller.response.CouponResponse;
import com.onieto.order.controller.response.MessageResponse;
import com.onieto.order.controller.response.OrderResponse;
import com.onieto.order.controller.response.UserResponseDto;
import com.onieto.order.model.OrderStatus;
import com.onieto.order.service.CouponService;
import com.onieto.order.dto.OrderDto;
import com.onieto.order.exception.ResourceNotFoundException;
import com.onieto.order.model.Coupon;
import com.onieto.order.model.Order;
import com.onieto.order.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CouponService couponService;
    private final UserValidatorService userValidatorService;


    public ResponseEntity<OrderResponse> getOrderDtoById(Long id) {
        Order order = getOrderById(id);
        return ResponseEntity.ok(convertToDTO(order));
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));
    }



    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    private OrderResponse convertToDTO(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .estado(order.getEstado())
                .coupon(order.getCoupon() != null ?
                        convertCouponToResponse(order.getCoupon()) : null)
                .finalPrice(order.getFinalPrice())
                .orderDate(order.getOrderDate())
                .items(order.getItems())
                .build();
    }

    private CourseResponse convertCourseToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())// Se obtiene del microservicio de usuarios
                .price(course.getPrice())
                .tags(course.getTags())
                .build();
    }

    private CouponResponse convertCouponToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountAmount(coupon.getDiscountAmount())
                .active(coupon.isActive())
                .build();
    }

    public ResponseEntity<MessageResponse> createOrder(@Valid OrderDto dto) {
        Order order = buildOrderFromDto(dto);
        orderRepository.save(order);
        return ResponseEntity.status(201).body(new MessageResponse("Orden creada exitosamente."));
    }

    public ResponseEntity<MessageResponse> updateOrder(Long id, @Valid OrderDto dto) {
        Order existing = getOrderById(id);
        Order updated = buildOrderFromDto(dto);
        updated.setId(existing.getId());
        orderRepository.save(updated);

        return ResponseEntity.ok(new MessageResponse("Orden actualizada exitosamente."));
    }

    public void updateOrderStatusById(Long id, OrderStatus status){

        Order order = getOrderById(id);
        if (status == null) {
            throw new IllegalArgumentException("El estado de la orden no puede ser nulo.");
        }
        order.setEstado(status);
        orderRepository.save(order);
    }

    public ResponseEntity<MessageResponse> deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));
        orderRepository.delete(order);
        return ResponseEntity.ok(new MessageResponse("Orden eliminada correctamente."));
    }

    private Order buildOrderFromDto(OrderDto dto) {
        UserResponseDto user = userValidatorService.getUserById(dto.getUserId());
        Course course = courseService.getCourseById(dto.getCourseId());
        BigDecimal coursePrice = course.getPrice();
        BigDecimal discount = BigDecimal.ZERO;
        Coupon coupon = null;

        if (dto.getCouponCode() != null && !dto.getCouponCode().isEmpty()) {
            coupon = couponService.getCouponByCode(dto.getCouponCode());

            if (!coupon.isActive()) {
                throw new IllegalArgumentException("Error al usar cupón.");
            }
            discount = coupon.getDiscountAmount();
            couponService.updateCouponStatusByCode(coupon.getCode(), false);
        }

        BigDecimal finalPrice = coursePrice.subtract(discount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        return Order.builder()
                .userId(user.getId())
                .course(course)
                .coupon(coupon)
                .finalPrice(finalPrice)
                .orderDate(LocalDateTime.now())
                .active(false)
                .build();
    }


}