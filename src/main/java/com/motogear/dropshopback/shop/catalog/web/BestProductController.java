package com.motogear.dropshopback.shop.catalog.web;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.service.BestProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/best")
@Tag(name = "Mejores Productos", description = "Endpoints públicos y de admin para consulta de mejores productos")
public class BestProductController {
    private final BestProductService bestProductService;

    @GetMapping("/get-products")
    public ResponseEntity<List<Product>> getBestProducts() {
        return ResponseEntity.ok(bestProductService.getBestProduct());
    }
    @PostMapping("admin/set-products/{id}")
    public ResponseEntity<Long> setBestProducts(@PathVariable long id) {
        return ResponseEntity.ok(bestProductService.saveBestProduct(id));
    }
    @PostMapping("admin/delete/{id}")
    public ResponseEntity<Void> deleteBestProducts(@PathVariable long id) {
        bestProductService.deleteBestProduct(id);
        return ResponseEntity.ok().build();
    }
}
