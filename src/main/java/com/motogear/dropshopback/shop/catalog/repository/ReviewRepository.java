package com.motogear.dropshopback.shop.catalog.repository;

import com.motogear.dropshopback.shop.catalog.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByProductId(Long productId);

    Review findByUserIdAndProductId(Long userId, Long productId);
}
