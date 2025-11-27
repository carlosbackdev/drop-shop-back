package com.motogear.dropshopback.shop.catalog.repository;

import com.motogear.dropshopback.shop.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
