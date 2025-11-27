package com.motogear.dropshopback.shop.checkout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear o actualizar un checkout")
public class CheckoutRequest {

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez")
    private String customerName;

    @NotBlank
    @Email
    @Size(max = 150)
    @Schema(description = "Email del cliente", example = "juan@example.com")
    private String customerEmail;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Dirección completa", example = "Av. Siempre Viva 742")
    private String address;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Ciudad", example = "Madrid")
    private String city;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "País", example = "España")
    private String country;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Código postal", example = "28001")
    private String postalCode;

    @NotBlank
    @Size(max = 25)
    @Pattern(regexp = "^[0-9+()\\-\\s]{6,25}$", message = "Formato de teléfono inválido")
    @Schema(description = "Teléfono de contacto", example = "+34 600 123 456")
    private String phoneNumber;
}

