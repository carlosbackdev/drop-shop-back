package com.motogear.dropshopback.shop.checkout.repository;

import com.motogear.dropshopback.shop.checkout.domain.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    List<Checkout> findByUserId(Long userId);

    Optional<Checkout> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}

