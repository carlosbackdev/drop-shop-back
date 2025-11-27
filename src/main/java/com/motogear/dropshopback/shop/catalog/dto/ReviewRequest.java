package com.motogear.dropshopback.shop.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear una Reseña")
public class ReviewRequest {
    @NotNull(message = "El ID de producto es obligatorio")
    private Long productId;
    @NotNull(message = "EL contenido es obligatorio")
    private String content;
    @NotNull(message = "El raiting es obligatorio")
    @Schema(description = "numero del 1-5", example = "1")
    private Integer rating;
}
