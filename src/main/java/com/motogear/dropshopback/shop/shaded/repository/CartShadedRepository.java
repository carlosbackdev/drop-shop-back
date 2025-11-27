package com.motogear.dropshopback.shop.shaded.repository;

import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión del carrito de compras
 */
@Repository
public interface CartShadedRepository extends JpaRepository<CartShaded, Long> {
    List<CartShaded> findByUserId(Long userId);
    Optional<CartShaded> findByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}

