package com.motogear.dropshopback.common.messages.event;

import com.motogear.dropshopback.shop.order.domain.OrderStatus;

public interface StatusEvent {
    OrderStatus getStatusType();
}
