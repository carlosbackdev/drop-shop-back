package com.motogear.dropshopback.shop.checkout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@Schema(description = "Respuesta con información de checkout")
public class CheckoutResponse {
    Long id;
    Long userId;
    String customerName;
    String customerEmail;
    String address;
    String city;
    String country;
    String postalCode;
    String phoneNumber;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

