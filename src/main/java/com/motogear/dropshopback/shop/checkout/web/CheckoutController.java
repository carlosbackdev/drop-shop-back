package com.motogear.dropshopback.shop.checkout.web;

import com.motogear.dropshopback.shop.checkout.domain.Checkout;
import com.motogear.dropshopback.shop.checkout.dto.CheckoutRequest;
import com.motogear.dropshopback.shop.checkout.dto.CheckoutResponse;
import com.motogear.dropshopback.shop.checkout.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Gestión de órdenes de checkout")
@SecurityRequirement(name = "bearerAuth")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @GetMapping
    @Operation(summary = "Listar checkouts del usuario autenticado")
    public ResponseEntity<List<CheckoutResponse>> listMyCheckouts() {
        List<Checkout> checkouts = checkoutService.listMyCheckouts();
        return ResponseEntity.ok(checkouts.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un checkout específico")
    public ResponseEntity<CheckoutResponse> getCheckout(@PathVariable Long id) {
        Checkout checkout = checkoutService.getCheckout(id);
        return ResponseEntity.ok(toResponse(checkout));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo checkout")
    public ResponseEntity<CheckoutResponse> createCheckout(@Valid @RequestBody CheckoutRequest request) {
        System.out.println(request);
        Checkout created = checkoutService.createCheckout(request);
        System.out.println(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un checkout existente")
    public ResponseEntity<CheckoutResponse> updateCheckout(
            @PathVariable Long id,
            @Valid @RequestBody CheckoutRequest request) {
        Checkout updated = checkoutService.updateCheckout(id, request);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un checkout")
    public ResponseEntity<Void> deleteCheckout(@PathVariable Long id) {
        checkoutService.deleteCheckout(id);
        return ResponseEntity.noContent().build();
    }

    private CheckoutResponse toResponse(Checkout checkout) {
        return CheckoutResponse.builder()
                .id(checkout.getId())
                .userId(checkout.getUserId())
                .customerName(checkout.getCustomerName())
                .customerEmail(checkout.getCustomerEmail())
                .address(checkout.getAddress())
                .city(checkout.getCity())
                .country(checkout.getCountry())
                .postalCode(checkout.getPostalCode())
                .phoneNumber(checkout.getPhoneNumber())
                .createdAt(checkout.getCreatedAt())
                .updatedAt(checkout.getUpdatedAt())
                .build();
    }
}

