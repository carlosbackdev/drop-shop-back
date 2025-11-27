package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.components.ProductMapper;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.repository.ProductRepository;
import com.motogear.dropshopback.shop.catalog.dto.ProductClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProductClientResponse> findAllClient(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toClientResponse);
    }

    @Transactional(readOnly = true)
    public ProductClientResponse findProductClientById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        return productMapper.toClientResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductClientResponse> findProductByCategoryId(Integer categoryId, Pageable Pagable) {
        List<Product> products = productRepository.findByCategory(categoryId,Pagable);
        return productMapper.toClientResponseList(products);
    }

    @Transactional(readOnly = true)
    public Page<ProductClientResponse> searchProducts(String query, Pageable pageable) {
        Page<Product> products = productRepository.findByKeywordsContainingIgnoreCase(query, pageable);
        return products.map(productMapper::toClientResponse);
    }

}
