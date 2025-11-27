package com.motogear.dropshopback.shop.catalog.repository;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Integer category, Pageable pageable);
    List<Product> findAllBySourceUrlIsNotNull();
    Page<Product> findByKeywordsContainingIgnoreCase(String keywords, Pageable pageable);
}
