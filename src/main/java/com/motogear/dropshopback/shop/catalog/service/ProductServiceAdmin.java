package com.motogear.dropshopback.shop.catalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.motogear.dropshopback.shop.catalog.components.ProductMapper;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.domain.ProductStatus;
import com.motogear.dropshopback.shop.catalog.repository.ProductRepository;
import com.motogear.dropshopback.common.util.FormatterObjectRaw;
import com.motogear.dropshopback.config.global.ConfigService;
import com.motogear.dropshopback.scraping.ScrapingService;
import com.motogear.dropshopback.scraping.dto.AiProductResponse;
import com.motogear.dropshopback.scraping.dto.ScrapingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.text.Normalizer;
import java.util.Locale;

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
    public Product saveProduct(Product product) {
        prepareForSave(product);
        return productRepository.save(product);
    }
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
        if(aiResponse != null && aiResponse.isSuccess()) {
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
        existing.setSku(request.getSku());
        existing.setSlug(request.getSlug());
        existing.setStatus(request.getStatus());
        existing.setStockQuantity(request.getStockQuantity());
        existing.setLowStockThreshold(request.getLowStockThreshold());
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
        prepareForSave(existing);

        return productRepository.save(existing);
    }

    private void prepareForSave(Product product) {
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.DRAFT);
        }
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        if (product.getLowStockThreshold() == null) {
            product.setLowStockThreshold(5);
        }
        if (product.getCurrency() == null || product.getCurrency().isBlank()) {
            product.setCurrency("EUR");
        }
        if (product.getExternalId() != null && !product.getExternalId().isBlank()) {
            product.setKeywords(addKeyword(product.getKeywords(), "drop"));
        }

        if (product.getStockQuantity() < 0) {
            throw badRequest("El stock no puede ser negativo");
        }
        if (product.getLowStockThreshold() < 0) {
            throw badRequest("El aviso de stock bajo no puede ser negativo");
        }
        if (product.getSellPrice() != null && product.getSellPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw badRequest("El precio de venta no puede ser negativo");
        }

        String slug = product.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = product.getName();
        }
        product.setSlug(toSlug(slug));

        if (product.getSku() != null) {
            product.setSku(product.getSku().trim().toUpperCase(Locale.ROOT));
        }

        Long currentId = product.getId();
        if (product.getSku() != null && !product.getSku().isBlank()
                && productRepository.existsBySkuAndIdNot(product.getSku(), currentId)) {
            throw badRequest("Ya existe un producto con el SKU " + product.getSku());
        }
        if (productRepository.existsBySlugAndIdNot(product.getSlug(), currentId)) {
            throw badRequest("Ya existe un producto con el slug " + product.getSlug());
        }

        if (product.getStatus() == ProductStatus.AVAILABLE) {
            if (product.getSku() == null || product.getSku().isBlank()) {
                throw badRequest("Un producto disponible necesita un SKU");
            }
            if (product.getSellPrice() == null || product.getSellPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw badRequest("Un producto disponible necesita un precio de venta mayor que cero");
            }
            if (!product.isDropshipping() && product.getStockQuantity() <= 0) {
                throw badRequest("Un producto disponible necesita stock mayor que cero");
            }
        }
    }

    private String addKeyword(String keywords, String keyword) {
        if (keywords == null || keywords.isBlank()) {
            return keyword;
        }
        boolean alreadyPresent = java.util.Arrays.stream(keywords.split("[,;]"))
                .map(String::trim)
                .anyMatch(keyword::equalsIgnoreCase);
        return alreadyPresent ? keywords : keywords.trim() + ", " + keyword;
    }

    private String toSlug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");

        if (normalized.isBlank()) {
            throw badRequest("No se ha podido generar un slug válido");
        }
        return normalized;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
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
