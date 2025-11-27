package com.motogear.dropshopback.shop.catalog.web;

import com.motogear.dropshopback.shop.catalog.domain.ImageProduct;
import com.motogear.dropshopback.shop.catalog.service.ImageProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/products-images")
@RequiredArgsConstructor
@Tag(name = "imagenes de productos", description = "Endpoints públicos para consulta de productos")
public class ProductImageController {

    private final ImageProductService imageProductService;
    private final String imageBaseUrl = "A:\\PROGRAMACION\\E-commerce\\aliexpres-scrapping\\uploads\\products";

    @PostMapping("/get-image/{productId}")
    public ResponseEntity<List<ImageProduct>> getProductImage(
            @Parameter(description = "ID del producto a buscar", required = true)
            @PathVariable Long productId) {
        List<ImageProduct> imageProducts = imageProductService.findById(productId);
        if (!imageProducts.isEmpty()) {
        return ResponseEntity.ok(imageProducts);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PostMapping("/get-image/home/{productId}")
    public ResponseEntity<ImageProduct> getProductImagePrimary(
            @Parameter(description = "ID del producto a buscar", required = true)
            @PathVariable Long productId) {
        ImageProduct imageProducts = imageProductService.findByProductIdAndIsPrimary(productId);
        if (imageProducts !=null) {
            return ResponseEntity.ok(imageProducts);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

}
