package com.motogear.dropshopback.shop.catalog.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.domain.ImageProduct;
import com.motogear.dropshopback.shop.catalog.dto.ProductClientResponse;
import com.motogear.dropshopback.shop.catalog.dto.ScriptingProductRequest;
import com.motogear.dropshopback.config.global.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ObjectMapper objectMapper;
    private final ConfigService configService;

    public ProductClientResponse toClientResponse(Product product) {
        ProductClientResponse response = new ProductClientResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDetails(product.getDetails());
        response.setSpecifications(product.getSpecifications());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setSellPrice(product.getSellPrice());
        response.setDiscount(product.getDiscount());
        response.setCurrency(product.getCurrency());
        response.setShippingCost(product.getShippingCost());
        response.setDeliveryEstimateDays(product.getDeliveryEstimateDays());
        response.setVariant(product.getVariants());
        response.setSellerName(product.getSellerName());
        response.setDeliveryMinDate(product.getDeliveryMinDate());
        response.setDeliveryMaxDate(product.getDeliveryMaxDate());
        response.setCategory(product.getCategory());
        response.setKeywords(product.getKeywords());
        response.setImages(product.getImages().stream()
                .map(ImageProduct::getImageUrl)
                .collect(Collectors.toList()));
        return response;
    }

    public List<ProductClientResponse> toClientResponseList(List<Product> products) {

        return products.stream()
                .map(this::toClientResponse)
                .collect(Collectors.toList());
    }

    public Product toEntity(ScriptingProductRequest request) throws JsonProcessingException {
        Product product = new Product();
        product.setName(request.getTitle());
        product.setDetails(request.getDetails());

        // Guardar specifications como JSON
        product.setSpecifications(objectMapper.writeValueAsString(request.getSpecifications()));

        // Guardar variants como JSON
        product.setVariants(objectMapper.writeValueAsString(request.getVariants()));

        // Precio base
        product.setBasePrice(BigDecimal.valueOf(request.getBasePrice()));

        // Calcular sellPrice con margen automático
        product.setSellPrice(configService.calculateSellPrice(product.getBasePrice()));

        product.setOriginalPrice(BigDecimal.valueOf(request.getOriginalPrice()));
        product.setDiscount(request.getDiscount());
        product.setCurrency(request.getCurrency());
        product.setShippingCost(BigDecimal.valueOf(request.getShippingCost()));

        // Formatear delivery
        if (request.getDeliveryEstimateDays() != null) {
            int min = request.getDeliveryEstimateDays().getMin();
            int max = request.getDeliveryEstimateDays().getMax();

            // Hoy como referencia
            LocalDate today = LocalDate.now();

            // Guardas las fechas reales estimadas
            product.setDeliveryMinDate(today.plusDays(min));
            product.setDeliveryMaxDate(today.plusDays(max));

            // Sigues guardando el string como hasta ahora
            String estimate = String.format("%d-%d días", min, max);
            product.setDeliveryEstimateDays(estimate);
        }

        product.setSellerName(request.getSellerName());
        product.setExternalId(request.getExternalId());
        product.setSourceUrl(request.getSourceUrl());

        // Mapear imágenes
        if (request.getImages() != null) {
            List<ImageProduct> images = request.getImages().stream()
                    .map(url -> {
                        ImageProduct img = new ImageProduct();
                        img.setImageUrl(url);
                        img.setProduct(product);
                        return img;
                    })
                    .collect(Collectors.toList());
            product.setImages(images);
        }
        return product;
    }
}
