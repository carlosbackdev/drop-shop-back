package com.motogear.dropshopback.shop.catalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.motogear.dropshopback.shop.catalog.components.ProductMapper;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.repository.ProductRepository;
import com.motogear.dropshopback.common.util.FormatterObjectRaw;
import com.motogear.dropshopback.config.global.ConfigService;
import com.motogear.dropshopback.scraping.ScrapingService;
import com.motogear.dropshopback.scraping.dto.AiProductResponse;
import com.motogear.dropshopback.scraping.dto.ScrapingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceAdmin {

    private final ProductRepository productRepository;
    private final ScrapingService scrapingService;
    private final ProductMapper productMapper;
    private final ConfigService configService;

    private FormatterObjectRaw jsonFormatter = new FormatterObjectRaw();

    public Optional<Product> findById(Long id) {return productRepository.findById(id);}
    public List<Product> findAll() {return productRepository.findAll();}
    public List<Product> findAllWithSourceUrl() {return productRepository.findAllBySourceUrlIsNotNull();}
    public Product saveProduct(Product product) {return productRepository.save(product);}
    public void  saveAllProducts(List<Product> products) {productRepository.saveAll(products);}
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product importProductFromUrl(String url) throws JsonProcessingException {

        ScrapingResponse response = scrapingService.scrapeProduct(url);

        if (!response.isSuccess()) {
            throw new RuntimeException("Error al realizar scraping del producto");
        }
        return productMapper.toEntity(response.getData());
    }

    @Transactional
    public Product getChangesAiService(Product product) {
        AiProductResponse aiResponse = scrapingService.enhanceProductAi(product);
        if(aiResponse != null || aiResponse.isSuccess()) {
            product.setName(aiResponse.getName());
            product.setKeywords(aiResponse.getKeywords());
            product.setDetails(aiResponse.getDetails());
        }
        return product;
    }

    @Transactional
    public Product updateProduct(Product request) {
        Product existing = productRepository.findById(request.getId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + request.getId()));

        // Actualiza solo los campos necesarios
        existing.setName(request.getName());
        existing.setDetails(request.getDetails());
        existing.setSpecifications(request.getSpecifications());
        existing.setKeywords(request.getKeywords());
        existing.setBasePrice(request.getBasePrice());
        existing.setOriginalPrice(request.getOriginalPrice());
        existing.setSellPrice(request.getSellPrice());
        existing.setDiscount(request.getDiscount());
        existing.setCurrency(request.getCurrency());
        existing.setShippingCost(request.getShippingCost());
        existing.setDeliveryEstimateDays(request.getDeliveryEstimateDays());
        existing.setDeliveryMinDate(request.getDeliveryMinDate());
        existing.setDeliveryMaxDate(request.getDeliveryMaxDate());
        existing.setVariants(request.getVariants());
        existing.setSellerName(request.getSellerName());
        existing.setExternalId(request.getExternalId());
        existing.setSourceUrl(request.getSourceUrl());
        existing.setCategory(request.getCategory());
        // Si tienes más campos, añádelos aquí

        return productRepository.save(existing);
    }

    @Transactional
    public List<Product> updateProductPrices(List<Product> products) {
        String raw = scrapingService.updateProductPrices(products);
        if (raw == null) return null;

        var root = jsonFormatter.parsePricesResponse(raw);
        var data = root.path("data");
        if (!data.isArray()) return null;

        data.forEach(item -> {
            String productIdStr = item.path("productId").asText(null);
            if (productIdStr == null) return;

            Long productId;
            try {
                productId = Long.parseLong(productIdStr);
            } catch (NumberFormatException e) {
                return;
            }

            products.stream()
                    .filter(p -> p.getId() == productId)
                    .findFirst()
                    .ifPresent(p -> {
                        if (item.has("basePrice") ) {
                            var priceValue = item.get("basePrice").decimalValue();
                            BigDecimal defaultPrice = new BigDecimal("0.00");
                            if (priceValue.compareTo(defaultPrice) <= 0) {
                                return;
                            }
                            p.setBasePrice(item.get("basePrice").decimalValue());
                            p.setSellPrice(configService.calculateSellPrice(p.getBasePrice()));
                        }
                        if (item.has("originalPrice")) {
                            var priceValue = item.get("originalPrice").decimalValue();
                            BigDecimal defaultPrice = new BigDecimal("0.00");
                            if (priceValue.compareTo(defaultPrice) <= 0) {
                                return;
                            }
                            p.setOriginalPrice(item.get("originalPrice").decimalValue());
                        }
                        if (item.has("discount")) {
                            var priceValue = item.get("discount").decimalValue();
                            BigDecimal defaultPrice = new BigDecimal("0.00");
                            if (priceValue.compareTo(defaultPrice) <= 0) {
                                return;
                            }
                            p.setDiscount(item.get("discount").asInt());
                        }


                    });
        });

        return products;
    }
}
