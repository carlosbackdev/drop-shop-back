package com.motogear.dropshopback.common.messages.service;

import com.motogear.dropshopback.common.messages.event.OrderEvent;
import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import com.motogear.dropshopback.shop.order.dto.UpdateOrderStatusRequest;
import com.motogear.dropshopback.shop.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusMessageService {

    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handleOrderPaid(Long orderId) {
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
