package com.motogear.dropshopback.shop.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear un producto mediante importación")
public class ImportRequest {
    @NotNull(message = "url de aliexpress")
    private String url;
    @NotNull(message = "EL id de la categoria es obligatorio")
    private Integer categoryId;
}
