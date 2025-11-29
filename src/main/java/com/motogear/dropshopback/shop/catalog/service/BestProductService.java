package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.domain.BestProduct;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.repository.BestProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BestProductService {
    private final BestProductRepository bestProductRepository;
    private final ProductService productService;

    @Transactional
    public List<Product> getBestProduct() {
        var bestProducts = bestProductRepository.findAll();
        return bestProducts.stream()
                .map(BestProduct::getProduct)
                .toList();
    }
    @Transactional
    public Long saveBestProduct(long id) {
        var bestProduct = new BestProduct();
        bestProduct.setProduct(productService.findProductById(id));
        var savedBestProduct = bestProductRepository.save(bestProduct);
        return savedBestProduct.getId();
    }
    @Transactional
    public void deleteBestProduct(Long id) {
        BestProduct bestProduct = bestProductRepository.findByProductId(id);
        bestProductRepository.deleteById(bestProduct.getId());
    }
}
