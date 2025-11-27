package com.motogear.dropshopback.shop.cart.web;

import com.motogear.dropshopback.shop.cart.domain.Cart;
import com.motogear.dropshopback.shop.cart.service.CartService;
import com.motogear.dropshopback.shop.cart.dto.AddToCartRequest;
import com.motogear.dropshopback.shop.cart.dto.CartItemResponse;
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
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Endpoints para gestión del carrito de compras")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    /**
     * Obtiene todos los items del carrito del usuario actual
     */
    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Retorna todos los items del carrito del usuario autenticado")
    public ResponseEntity<List<CartItemResponse>> getCart() {
        List<Cart> cartItems = cartService.getCartItems();

        List<CartItemResponse> response = cartItems.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Agrega un producto al carrito
     */
    @PostMapping
    @Operation(summary = "Agregar al carrito", description = "Agrega un producto al carrito del usuario autenticado")
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        Cart cartItem = cartService.addToCart(request.getProductId(), request.getQuantity(),request.getVariant());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(cartItem));
    }

    /**
     * Actualiza la cantidad de un item del carrito
     */
    @PutMapping("/{cartItemId}")
    @Operation(summary = "Actualizar cantidad", description = "Actualiza la cantidad de un item específico del carrito")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {

        if (quantity < 1) {
            return ResponseEntity.badRequest().build();
        }

        Cart cartItem = cartService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(toResponse(cartItem));
    }

    /**
     * Elimina un producto específico del carrito
     */
    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Eliminar por producto", description = "Elimina un producto específico del carrito")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un item del carrito por su ID
     */
    @DeleteMapping("/{cartItemId}")
    @Operation(summary = "Eliminar item", description = "Elimina un item del carrito por su ID")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vacía completamente el carrito
     */
    @DeleteMapping("/clear")
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito del usuario")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte una entidad Cart a CartItemResponse
     */
    private CartItemResponse toResponse(Cart cart) {
        return CartItemResponse.builder()
                .id(cart.getId())
                .productId(cart.getProductId())
                .userId(cart.getUserId())
                .quantity(cart.getQuantity())
                .variant(cart.getVariant()) // <- incluir variant en la respuesta
                .createdAt(cart.getCreatedAt())
                .build();
    }
}
