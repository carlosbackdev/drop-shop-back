package com.motogear.dropshopback.shop.catalog.repository;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.domain.ProductStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Integer category, Pageable pageable);
    List<Product> findAllBySourceUrlIsNotNull();
    Page<Product> findByKeywordsContainingIgnoreCase(String keywords, Pageable pageable);
    Page<Product> findByStatusIn(Collection<ProductStatus> statuses, Pageable pageable);
    Optional<Product> findByIdAndStatusIn(Long id, Collection<ProductStatus> statuses);
    Optional<Product> findBySlugAndStatusIn(String slug, Collection<ProductStatus> statuses);
    List<Product> findByCategoryAndStatusIn(
            Integer category,
            Collection<ProductStatus> statuses,
            Pageable pageable
    );
    Page<Product> findByKeywordsContainingIgnoreCaseAndStatusIn(
            String keywords,
            Collection<ProductStatus> statuses,
            Pageable pageable
    );
    boolean existsBySkuAndIdNot(String sku, Long id);
    boolean existsBySlugAndIdNot(String slug, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
