package com.motogear.dropshopback.shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Solicitud para crear una orden")
public class CreateOrderRequest {

    @NotNull(message = "El ID de checkout es obligatorio")
    @Schema(description = "ID del checkout asociado a la orden", example = "1")
    private Long checkoutId;

    @NotEmpty(message = "Debe incluir al menos un producto del carrito")
    @Schema(description = "Lista de IDs de items del carrito", example = "[1, 2, 3]")
    private List<Long> cartShadedItemIds;

    @Schema(description = "Total de la orden", example = "159.99")
    private BigDecimal total;

    @Schema(description = "Notas adicionales para la orden", example = "Entregar por la mañana")
    private String notes;
}

