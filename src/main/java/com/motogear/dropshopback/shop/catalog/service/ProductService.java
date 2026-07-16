package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.components.ProductMapper;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.domain.ProductStatus;
import com.motogear.dropshopback.shop.catalog.repository.ProductRepository;
import com.motogear.dropshopback.shop.catalog.dto.ProductClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Set<ProductStatus> PUBLIC_STATUSES = Set.of(
            ProductStatus.COMING_SOON,
            ProductStatus.AVAILABLE,
            ProductStatus.OUT_OF_STOCK
    );

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProductClientResponse> findAllClient(Pageable pageable) {
        Page<Product> products = productRepository.findByStatusIn(PUBLIC_STATUSES, pageable);
        return products.map(productMapper::toClientResponse);
    }

    @Transactional(readOnly = true)
    public ProductClientResponse findProductClientById(Long id) {
        Product product = productRepository.findByIdAndStatusIn(id, PUBLIC_STATUSES)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id));
        return productMapper.toClientResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductClientResponse findProductClientBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatusIn(slug, PUBLIC_STATUSES)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con slug: " + slug));
        return productMapper.toClientResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductClientResponse> findProductByCategoryId(Integer categoryId, Pageable Pagable) {
        List<Product> products = productRepository.findByCategoryAndStatusIn(
                categoryId,
                PUBLIC_STATUSES,
                Pagable
        );
        return productMapper.toClientResponseList(products);
    }

    @Transactional(readOnly = true)
    public Page<ProductClientResponse> searchProducts(String query, Pageable pageable) {
        Page<Product> products = productRepository.findByKeywordsContainingIgnoreCaseAndStatusIn(
                query,
                PUBLIC_STATUSES,
                pageable
        );
        return products.map(productMapper::toClientResponse);
    }

}
