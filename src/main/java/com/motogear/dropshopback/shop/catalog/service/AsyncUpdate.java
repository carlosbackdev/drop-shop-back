package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncUpdate {
    private final ProductServiceAdmin productService;

    @Async
    public void executeUpdate() {
        List<Product> products = productService.findAllWithSourceUrl();
        List<Product> updated = productService.updateProductPrices(products);
        productService.saveAllProducts(updated);
    }
}
