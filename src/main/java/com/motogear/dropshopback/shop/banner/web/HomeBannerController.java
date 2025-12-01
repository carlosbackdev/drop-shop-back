package com.motogear.dropshopback.shop.banner.web;

import com.motogear.dropshopback.shop.banner.domain.HomeBanner;
import com.motogear.dropshopback.shop.banner.service.HomeBannerService;
import com.motogear.dropshopback.shop.catalog.service.ImageProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home-banners")
@RequiredArgsConstructor
public class HomeBannerController {
    private final HomeBannerService homeBannerService;
    private final ImageProductService imageProductService;

    @GetMapping("/get")
    @Operation(
            summary = "Obtener todos los banners de la página principal",
            description = "Retorna una lista de todos los banners configurados para la página principal"
    )
    public ResponseEntity<List<HomeBanner>> getAllBanners() {
        return ResponseEntity.ok(homeBannerService.getAllBanners());
    }

    @PostMapping("/admin/create")
    @Operation(
            summary = "Crear un nuevo banner para la página principal",
            description = "Permite a los administradores crear un nuevo banner para la página principal"
    )
    public ResponseEntity<HomeBanner> createBanner(@Valid @RequestBody HomeBanner banner) {
        HomeBanner savedBanner = homeBannerService.saveBanner(banner);
        return ResponseEntity.ok(savedBanner);
    }
    @DeleteMapping("/admin/delete/{id}")
    @Operation(
            summary = "Eliminar un banner de la página principal",
            description = "Permite a los administradores eliminar un banner existente de la página principal"
    )
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        imageProductService.deleteImagesByBannerId(id);
        homeBannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/admin/update")
    @Operation(
            summary = "Actualizar un banner de la página principal",
            description = "Permite a los administradores actualizar un banner existente de la página principal")
    public ResponseEntity<HomeBanner> updateBanner(HomeBanner banner) {
        HomeBanner updatedBanner = homeBannerService.updateBanner(banner);
        return ResponseEntity.ok(updatedBanner);
    }

}
