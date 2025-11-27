package com.motogear.dropshopback.shop.order.dto;

import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Solicitud para actualizar el estado de una orden")
public class UpdateOrderStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Nuevo estado de la orden", example = "PAID")
    private OrderStatus status;

    @Schema(description = "Notas adicionales", example = "Pago confirmado")
    private String notes;
}

