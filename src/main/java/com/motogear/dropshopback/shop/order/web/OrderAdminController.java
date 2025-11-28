package com.motogear.dropshopback.shop.order.web;

import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import com.motogear.dropshopback.shop.order.dto.CreateOrderRequest;
import com.motogear.dropshopback.shop.order.dto.OrderResponse;
import com.motogear.dropshopback.shop.order.dto.UpdateOrderStatusRequest;
import com.motogear.dropshopback.shop.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/admin")
@RequiredArgsConstructor
@Tag(name = "Órdenes", description = "Gestión de órdenes de compra")
public class OrderAdminController {

    private final OrderService orderService;
    private final Logger log = LoggerFactory.getLogger(getClass());


    @GetMapping("/status/{status}")
    @Operation(summary = "Listar órdenes por estado")
    public ResponseEntity<List<OrderResponse>> listOrdersByStatus(@PathVariable OrderStatus status) {
        log.info("SOLICITUD DESDE ADMIN - Listar órdenes por estado: {}", status);
        List<Order> orders = orderService.listAllOrdersByStatus(status);
        return ResponseEntity.ok(orders.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden específica")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderForSystem(id);
        return ResponseEntity.ok(toResponse(order));
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva orden")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request, Long userId) {
        Order created = orderService.createOrderBySystem(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Actualizar el estado de una orden")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        Order updated = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una orden (solo si está Penditente o Cancelada)")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .checkoutId(order.getCheckoutId())
                .cartShadedIds(order.getCartShadedIds())
                .status(order.getStatus())
                .total(order.getTotal())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
