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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Órdenes", description = "Gestión de órdenes de compra")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar todas mis órdenes")
    public ResponseEntity<List<OrderResponse>> listMyOrders() {
        List<Order> orders = orderService.listMyOrders();
        return ResponseEntity.ok(orders.stream().map(this::toResponse).toList());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar órdenes por estado")
    public ResponseEntity<List<OrderResponse>> listOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.listMyOrdersByStatus(status);
        return ResponseEntity.ok(orders.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden específica")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(toResponse(order));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva orden")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order created = orderService.createOrder(request);
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

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar una orden")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        Order cancelled = orderService.getOrder(id);
        return ResponseEntity.ok(toResponse(cancelled));
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
