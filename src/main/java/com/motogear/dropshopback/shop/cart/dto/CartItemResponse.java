package com.motogear.dropshopback.shop.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para items del carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item del carrito de compras")
public class CartItemResponse {

    @Schema(description = "ID del item en el carrito", example = "1")
    private Long id;

    @Schema(description = "ID del producto", example = "5")
    private Long productId;

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;

    @Schema(description = "Cantidad del producto", example = "2")
    private Integer quantity;

    @Schema(description = "Variante del producto", example = "rojo, talla M")
    private String variant;

    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}

