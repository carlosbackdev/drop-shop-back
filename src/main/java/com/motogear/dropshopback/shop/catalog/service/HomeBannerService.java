package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.domain.HomeBanner;
import com.motogear.dropshopback.shop.catalog.repository.HomeBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeBannerService {
    private final HomeBannerRepository homeBannerRepository;

    public List<HomeBanner> getAllBanners() {
        return homeBannerRepository.findAll();
    }
    public HomeBanner getBannerById(Long id) {
        return homeBannerRepository.findById(id).orElse(null);
    }
    public HomeBanner saveBanner(HomeBanner banner) {
        return homeBannerRepository.save(banner);
    }
    public HomeBanner updateBanner(HomeBanner banner) {
        if (banner.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Banner id requerido para actualizar");
        }
        if (!homeBannerRepository.existsById(banner.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Banner no encontrado");
        }

        // Guardar y devolver
        return homeBannerRepository.save(banner);
    }
    public void deleteBanner(Long id) {
        homeBannerRepository.deleteById(id);
    }

}
