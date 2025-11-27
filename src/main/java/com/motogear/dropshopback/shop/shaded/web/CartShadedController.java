package com.motogear.dropshopback.shop.shaded.web;

import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import com.motogear.dropshopback.shop.shaded.dto.AddToCartShadedRequest;
import com.motogear.dropshopback.shop.shaded.dto.CartShadedItemResponse;
import com.motogear.dropshopback.shop.shaded.service.CartShadedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/cart-shaded")
@RequiredArgsConstructor
@Tag(name = "Carrito shaded", description = "Endpoints para gestión del carrito de compras")
@SecurityRequirement(name = "bearerAuth")
public class CartShadedController {

    private final CartShadedService cartService;

    /**
     * Obtiene todos los items del carrito del usuario actual
     */
    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Retorna todos los items del carrito del usuario autenticado")
    public ResponseEntity<List<CartShadedItemResponse>> getCart() {
        List<CartShaded> cartItems = cartService.getCartItems();

        List<CartShadedItemResponse> response = cartItems.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Agrega un producto al carrito
     */
    @PostMapping
    @Operation(summary = "Agregar al carrito", description = "Agrega un producto al carrito del usuario autenticado")
    public ResponseEntity<CartShadedItemResponse> addToCart(@Valid @RequestBody AddToCartShadedRequest request) {
        CartShaded cartItem = cartService.addToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(cartItem));
    }

    /**
     * Vacía completamente el carrito
     */
    @DeleteMapping
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito shaded del usuario")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte una entidad Cart a CartItemResponse
     */
    private CartShadedItemResponse toResponse(CartShaded cart) {
        return CartShadedItemResponse.builder()
                .id(cart.getId())
                .productId(cart.getProductId())
                .userId(cart.getUserId())
                .quantity(cart.getQuantity())
                .variant(cart.getVariant()) // <- incluir variant en la respuesta
                .createdAt(cart.getCreatedAt())
                .build();
    }
}
