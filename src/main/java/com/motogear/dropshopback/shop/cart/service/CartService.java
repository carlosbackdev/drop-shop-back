package com.motogear.dropshopback.shop.cart.service;

import com.motogear.dropshopback.shop.cart.domain.Cart;
import com.motogear.dropshopback.shop.cart.repository.CartRepository;
import com.motogear.dropshopback.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Servicio para la gestión del carrito de compras
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;

    public List<Cart> getCartItems() {
        Long userId = userService.getCurrentUser().getId();
        return cartRepository.findByUserId(userId);
    }

    @Transactional
    public Cart addToCart(Long productId, Integer quantity, String variant) {
        Long userId = userService.getCurrentUser().getId();

        // Verificar si el producto ya está en el carrito
        var existingItem = cartRepository.findByUserIdAndProductIdAndVariant(userId, productId, variant);

        if (existingItem.isPresent()) {
            // Si existe, actualizar la cantidad
            Cart cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartRepository.save(cartItem);
        } else {
            // Si no existe, crear nuevo item
            Cart newItem = Cart.builder()
                    .userId(userId)
                    .productId(productId)
                    .quantity(quantity)
                    .variant(variant)
                    .build();
            return cartRepository.save(newItem);
        }
    }

    @Transactional
    public Cart updateCartItem(Long cartItemId, Integer quantity) {
        Long userId = userService.getCurrentUser().getId();

        Cart cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item no encontrado"));

        // Verificar que el item pertenece al usuario actual
        if (!cartItem.getUserId().equals(userId)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No tienes permiso para modificar este item");
        }

        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    /**
     * Elimina un producto específico del carrito del usuario actual
     *
     * @param productId ID del producto a eliminar
     */
    @Transactional
    public void removeFromCart(Long productId) {
        Long userId = userService.getCurrentUser().getId();
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * Elimina un item del carrito por su ID
     *
     * @param cartItemId ID del item del carrito
     */
    @Transactional
    public void removeCartItem(Long cartItemId) {
        Long userId = userService.getCurrentUser().getId();

        Cart cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item no encontrado"));

        // Verificar que el item pertenece al usuario actual
        if (!cartItem.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No tienes permiso para modificar este item");
        }

        cartRepository.deleteById(cartItemId);
    }


    @Transactional
    public void clearCart() {
        Long userId = userService.getCurrentUser().getId();
        cartRepository.deleteByUserId(userId);
    }

}

