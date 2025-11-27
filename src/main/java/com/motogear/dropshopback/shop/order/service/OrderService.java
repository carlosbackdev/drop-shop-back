package com.motogear.dropshopback.shop.order.service;

import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import com.motogear.dropshopback.shop.order.dto.CreateOrderRequest;
import com.motogear.dropshopback.shop.order.dto.UpdateOrderStatusRequest;
import com.motogear.dropshopback.shop.order.repository.OrderRepository;
import com.motogear.dropshopback.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Order> listMyOrders() {
        Long userId = userService.getCurrentUser().getId();
        return orderRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> listMyOrdersByStatus(OrderStatus status) {
        Long userId = userService.getCurrentUser().getId();
        return orderRepository.findByUserIdAndStatus(userId, status);
    }
    @Transactional(readOnly = true)
    public List<Order> listAllOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        Long userId = userService.getCurrentUser().getId();
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }
    @Transactional(readOnly = true)
    public Order getOrderForSystem(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }

    @Transactional
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Long userId = userService.getCurrentUser().getId();

        // Validar que los cartItemIds no estén vacíos
        if (request.getCartShadedItemIds() == null || request.getCartShadedItemIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden debe contener al menos un item del carrito");
        }

        // Crear la orden
        Order order = Order.builder()
                .userId(userId)
                .checkoutId(request.getCheckoutId())
                .cartShadedIds(request.getCartShadedItemIds())
                .total(request.getTotal())
                .notes(request.getNotes())
                .status(OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }
    @Transactional
    public Order createOrderBySystem(CreateOrderRequest request, Long userId) {

        // Validar que los cartItemIds no estén vacíos
        if (request.getCartShadedItemIds() == null || request.getCartShadedItemIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden debe contener al menos un item del carrito");
        }

        // Crear la orden
        Order order = Order.builder()
                .userId(userId)
                .checkoutId(request.getCheckoutId())
                .cartShadedIds(request.getCartShadedItemIds())
                .total(request.getTotal())
                .notes(request.getNotes())
                .status(OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = getOrder(orderId);
        order.setStatus(request.getStatus());
        // Actualizar notas si se proporcionan
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            order.setNotes(request.getNotes());
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatusBySystem(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        order.setStatus(request.getStatus());

        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            order.setNotes(request.getNotes());
        }

        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrder(orderId);

        // Solo se puede cancelar si está en estado PENDING o PAID
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede cancelar una orden en estado " + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = getOrder(orderId);

        // Solo se puede eliminar si está cancelada o pendiente
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.PENDING) {
            orderRepository.delete(order);
        }else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se pueden eliminar órdenes canceladas");
        }
    }
    @Transactional
    public void deleteOrderBySystem(Long orderId) {
        Order order = getOrderForSystem(orderId);
        orderRepository.delete(order);
    }
}

