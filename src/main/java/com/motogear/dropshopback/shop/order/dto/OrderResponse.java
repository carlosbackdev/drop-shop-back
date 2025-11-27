package com.motogear.dropshopback.shop.order.dto;

import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@Schema(description = "Respuesta con información de una orden")
public class OrderResponse {
    Long id;
    Long userId;
    Long checkoutId;
    List<Long> cartShadedIds;
    OrderStatus status;
    BigDecimal total;
    String notes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
