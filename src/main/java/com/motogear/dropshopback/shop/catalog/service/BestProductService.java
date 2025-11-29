package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.components.ProductMapper;
import com.motogear.dropshopback.shop.catalog.domain.BestProduct;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.dto.ProductClientResponse;
import com.motogear.dropshopback.shop.catalog.repository.BestProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestProductService {
    private final BestProductRepository bestProductRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Transactional
    public List<ProductClientResponse> getBestProduct() {
        List<BestProduct> bestProducts = bestProductRepository.findAll();
        List<Product> products = bestProducts.stream()
                .map(BestProduct::getProduct)
                .collect(Collectors.toList());
        return productMapper.toClientResponseList(products);
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
