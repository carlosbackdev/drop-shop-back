package com.motogear.dropshopback.shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Solicitud para obtener información de tracking")
@Data
public class TrackRequest {
    @NotBlank
    @Schema(description = "Número de seguimiento del pedido", example = "280320000056634969")
    private String trackingNumber;

    @NotNull
    @Schema(description = "ID de la orden asociada")
    private Long orderId;
}

