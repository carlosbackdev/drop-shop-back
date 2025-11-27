package com.motogear.dropshopback.common.messages.event;

import com.motogear.dropshopback.shop.order.domain.OrderStatus;

public class OrderEvent implements StatusEvent {
    private final Object source;
    private final Long orderId;
    private final OrderStatus statusType;

    public OrderEvent(Object source, Long orderId, OrderStatus statusType) {
        this.source = source;
        this.orderId = orderId;
        this.statusType = statusType;
    }

    public Object getSource() {
        return source;
    }

    public Long getOrderId() {
        return orderId;
    }
    @Override
    public OrderStatus getStatusType() {
        return statusType;
    }

}
