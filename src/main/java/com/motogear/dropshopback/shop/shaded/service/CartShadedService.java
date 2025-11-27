package com.motogear.dropshopback.shop.shaded.service;

import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import com.motogear.dropshopback.shop.shaded.dto.AddToCartShadedRequest;
import com.motogear.dropshopback.shop.shaded.repository.CartShadedRepository;
import com.motogear.dropshopback.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión del carrito de compras
 */
@Service
@RequiredArgsConstructor
public class CartShadedService {

    private final CartShadedRepository cartRepository;
    private final UserService userService;

    public List<CartShaded> getCartItems() {
        Long userId = userService.getCurrentUser().getId();
        return cartRepository.findByUserId(userId);
    }

    @Transactional
    public CartShaded getCartItemById(Long cartItemId) {
        return cartRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Item de carrito no encontrado"));
    }

    @Transactional
    public CartShaded addToCart(AddToCartShadedRequest request) {
        CartShaded cartItem = CartShaded.builder()
                .userId(userService.getCurrentUser().getId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .variant(request.getVariant())
                .build();


        return cartRepository.save(cartItem);
    }

    @Transactional
    public void clearCart() {
        Long userId = userService.getCurrentUser().getId();
        cartRepository.deleteByUserId(userId);
    }

}

