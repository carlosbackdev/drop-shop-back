package com.motogear.dropshopback.shop.catalog.web;

import com.motogear.dropshopback.shop.catalog.domain.Review;
import com.motogear.dropshopback.shop.catalog.dto.ReviewRequest;
import com.motogear.dropshopback.shop.catalog.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/review")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Endpoints públicos para consulta de reseñas")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear review solo si está autenticado y ha comprado el producto")
    public ResponseEntity<Review> createReview(@Valid @RequestBody ReviewRequest review) {
        return ResponseEntity.ok(reviewService.save(review));
    }

    @GetMapping("/list/{productId}")
    @Operation(summary = "Listar reseñas de un producto")
    public ResponseEntity<List<Review>> listReviewsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProductId(productId));
    }

    @GetMapping("/can/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Verificar si el usuario autenticado puede dejar una reseña para un producto")
    public ResponseEntity<Boolean> canReviewProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.canSaveReview(productId));
    }

}
