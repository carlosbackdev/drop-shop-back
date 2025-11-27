package com.motogear.dropshopback.shop.catalog.repository;

import com.motogear.dropshopback.shop.catalog.domain.ImageProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageProductRepository extends JpaRepository<ImageProduct, Long> {
    List<ImageProduct> findByProductId(Long productId);
    ImageProduct findByProductIdAndIsPrimary(long productId, Boolean isPrimary);
    void deleteByProductId(Long productId);
    boolean existsByProductId(Long productId);
}
