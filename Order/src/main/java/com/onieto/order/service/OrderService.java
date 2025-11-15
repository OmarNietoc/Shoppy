package com.onieto.order.service;

import com.onieto.order.client.ProductClient;
import com.onieto.order.controller.response.CouponResponse;
import com.onieto.order.controller.response.MessageResponse;
import com.onieto.order.controller.response.OrderResponse;
import com.onieto.order.controller.response.UserResponseDto;
import com.onieto.order.dto.OrderDto;
import com.onieto.order.dto.OrderItemRequestDto;
import com.onieto.order.dto.ProductResponseDto;
import com.onieto.order.exception.ResourceNotFoundException;
import com.onieto.order.model.Coupon;
import com.onieto.order.model.Order;
import com.onieto.order.model.OrderItem;
import com.onieto.order.model.OrderStatus;
import com.onieto.order.repository.OrderRepository;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final OrderRepository orderRepository;
    private final CouponService couponService;
    private final UserValidatorService userValidatorService;
    private final ProductClient productClient;

    public ResponseEntity<OrderResponse> getOrderDtoById(Long id) {
        Order order = getOrderById(id);
        return ResponseEntity.ok(convertToDTO(order));
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));
    }

    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responses = orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    public ResponseEntity<OrderResponse> createOrder(@Valid OrderDto dto) {
        Order order = buildOrderFromDto(dto, null);
        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(order));
    }

    public ResponseEntity<OrderResponse> updateOrder(Long id, @Valid OrderDto dto) {
        Order existing = getOrderById(id);
        Order updated = buildOrderFromDto(dto, existing);
        updated.setId(existing.getId());
        orderRepository.save(updated);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    public ResponseEntity<MessageResponse> updateOrderStatusById(Long id, OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado de la orden no puede ser nulo.");
        }
        Order order = getOrderById(id);
        order.setEstado(status);
        orderRepository.save(order);
        return ResponseEntity.ok(new MessageResponse("Estado de la orden actualizado exitosamente: " + status));
    }

    public ResponseEntity<MessageResponse> deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
        return ResponseEntity.ok(new MessageResponse("Orden eliminada correctamente."));
    }

    private OrderResponse convertToDTO(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userEmail(order.getUserEmail())
                .estado(order.getEstado())
                .coupon(order.getCoupon() != null ? convertCouponToResponse(order.getCoupon()) : null)
                .discountApplied(order.getDiscountApplied())
                .finalPrice(order.getFinalPrice())
                .orderDate(order.getOrderDate())
                .items(order.getItems())
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

    private Order buildOrderFromDto(OrderDto dto, Order currentOrder) {
        UserResponseDto user = userValidatorService.getUserByEmail(dto.getUserEmail());

        List<OrderItemRequestDto> itemRequests = dto.getItems();
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new IllegalArgumentException("La orden debe contener al menos un producto.");
        }

        List<OrderItem> items = itemRequests.stream()
                .map(this::buildOrderItem)
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(ZERO, BigDecimal::add);

        Coupon coupon = null;
        BigDecimal desiredDiscount = ZERO;
        boolean newCouponApplied = false;

        if (StringUtils.hasText(dto.getCouponCode())) {
            coupon = couponService.getCouponByCode(dto.getCouponCode().trim());
            if (!coupon.isActive()) {
                throw new IllegalArgumentException("El cupón proporcionado no está disponible.");
            }
            desiredDiscount = coupon.getDiscountAmount();
            newCouponApplied = true;
        } else if (currentOrder != null && currentOrder.getCoupon() != null) {
            coupon = currentOrder.getCoupon();
            desiredDiscount = coupon.getDiscountAmount();
        }

        BigDecimal discountApplied = desiredDiscount.compareTo(subtotal) > 0 ? subtotal : desiredDiscount;
        BigDecimal finalPrice = subtotal.subtract(discountApplied);

        Order order = Order.builder()
                .userEmail(user.getEmail())
                .estado(currentOrder != null ? currentOrder.getEstado() : OrderStatus.PENDING)
                .coupon(coupon)
                .finalPrice(finalPrice)
                .discountApplied(discountApplied)
                .orderDate(currentOrder != null ? currentOrder.getOrderDate() : LocalDateTime.now())
                .items(items)
                .build();

        if (newCouponApplied && coupon != null) {
            couponService.updateCouponStatusByCode(coupon.getCode(), false);
        }

        items.forEach(item -> item.setOrder(order));
        return order;
    }

    private OrderItem buildOrderItem(OrderItemRequestDto itemDto) {
        ProductResponseDto product = fetchProduct(itemDto.getProductId());
        Integer productPrice = product.getPrecio();
        if (productPrice == null) {
            throw new IllegalArgumentException("El producto " + product.getId() + " no tiene un precio definido.");
        }

        BigDecimal unitPrice = BigDecimal.valueOf(productPrice);
        BigDecimal quantity = BigDecimal.valueOf(itemDto.getQuantity());
        BigDecimal subtotal = unitPrice.multiply(quantity);
        String imageBase64 = product.getImagen() != null
                ? Base64.getEncoder().encodeToString(product.getImagen())
                : null;

        return OrderItem.builder()
                .productId(product.getId())
                .productName(product.getNombre())
                .productDescription(product.getDescripcion())
                .unitPrice(unitPrice)
                .productImageUrl(imageBase64)
                .quantity(itemDto.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    private ProductResponseDto fetchProduct(String productId) {
        try {
            ProductResponseDto product = productClient.getProductById(productId);
            if (product == null) {
                throw new ResourceNotFoundException("Producto no encontrado: " + productId);
            }
            return product;
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Producto no encontrado: " + productId);
        } catch (FeignException e) {
            throw new IllegalArgumentException("Error al obtener el producto: " + e.getMessage());
        }
    }
}
