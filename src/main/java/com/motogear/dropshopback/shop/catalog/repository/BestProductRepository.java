package com.motogear.dropshopback.shop.catalog.repository;

import com.motogear.dropshopback.shop.catalog.domain.BestProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestProductRepository extends JpaRepository<BestProduct, Long> {
    BestProduct findByProductId(Long productId);
}
