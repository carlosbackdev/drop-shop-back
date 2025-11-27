package com.motogear.dropshopback.shop.shaded.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para agregar un producto al carrito
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para agregar un producto al carrito")
public class AddToCartShadedRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto a agregar", example = "1")
    private Long productId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad del producto", example = "2")
    private Integer quantity = 1;
    @Schema(description = "Variante del producto (opcional)", example = "Rojo, Talla M")
    private String variant;
}

