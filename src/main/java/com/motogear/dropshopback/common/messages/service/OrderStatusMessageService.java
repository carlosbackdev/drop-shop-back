package com.motogear.dropshopback.common.messages.service;

import com.motogear.dropshopback.common.messages.event.OrderEvent;
import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import com.motogear.dropshopback.shop.order.dto.UpdateOrderStatusRequest;
import com.motogear.dropshopback.shop.order.service.OrderService;
import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.catalog.service.ProductAvailabilityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusMessageService {

    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductAvailabilityService productAvailabilityService;

    @Transactional
    public void handleOrderPaid(Long orderId) {
        Order order = orderService.getOrderForUpdate(orderId);
        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("La orden " + orderId + " no está pendiente de pago");
        }

        productAvailabilityService.consumeStockForCartItems(order.getCartShadedIds());

        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
        updateRequest.setStatus(OrderStatus.PAID);

        orderService.updateOrderStatusBySystem(orderId, updateRequest);

        eventPublisher.publishEvent(new OrderEvent(this, orderId, OrderStatus.PAID));
    }

    @Transactional
    public void handleOrderDelivering(Long orderId) {
        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
        updateRequest.setStatus(OrderStatus.SHIPPED);

        orderService.updateOrderStatusBySystem(orderId, updateRequest);

        eventPublisher.publishEvent(new OrderEvent(this, orderId, OrderStatus.SHIPPED));
    }


}
