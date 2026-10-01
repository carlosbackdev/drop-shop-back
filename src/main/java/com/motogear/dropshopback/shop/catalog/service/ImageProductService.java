package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.blog.domain.Post;
import com.motogear.dropshopback.shop.blog.service.PostService;
import com.motogear.dropshopback.shop.catalog.domain.HomeBanner;
import com.motogear.dropshopback.shop.catalog.domain.ImageProduct;
import com.motogear.dropshopback.shop.catalog.repository.ImageProductRepository;
import com.motogear.dropshopback.shop.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Comparator;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProductService {

    private final ImageProductRepository imageProductRepository;
    private final ProductRepository productRepository;
    private final HomeBannerService homeBannerService;
    private final PostService postService;
    @Value("${scraping.api.url}")
    private String API_URL;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public List<ImageProduct> findById(Long productId) {
        return imageProductRepository.findByProductId(productId).stream()
                .sorted(Comparator.comparing((ImageProduct image) -> !Boolean.TRUE.equals(image.getIsPrimary()))
                        .thenComparing(ImageProduct::getId))
                .toList();
    }
    @Transactional(readOnly = true)
    public ImageProduct findByProductIdAndIsPrimary(Long productId) {
        ImageProduct primary = imageProductRepository.findByProductIdAndIsPrimary(productId, true);
        if (primary != null) return primary;
        return findById(productId).stream().findFirst().orElse(null);
    }

    @Transactional
    public ImageProduct addImage(Long productId, String imageUrl) {
        validateNewImageUrl(imageUrl);
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        var images = imageProductRepository.findByProductId(productId);
        for (ImageProduct image : images) {
            if (imageUrl.equals(image.getImageUrl())) return image;
        }
        // Las fotos importadas más antiguas conservan la portada al añadir fotos nuevas.
        if (!images.isEmpty() && images.stream().noneMatch(image -> Boolean.TRUE.equals(image.getIsPrimary()))) {
            images.stream().min(Comparator.comparing(ImageProduct::getId)).ifPresent(image -> {
                image.setIsPrimary(true);
                imageProductRepository.save(image);
            });
        }
        ImageProduct image = new ImageProduct();
        image.setProduct(product);
        image.setImageUrl(imageUrl);
        image.setIsPrimary(images.isEmpty());
        return imageProductRepository.save(image);
    }

    @Transactional
    public ImageProduct selectPrimaryImage(Long productId, Integer imageId) {
        var images = imageProductRepository.findByProductId(productId);
        ImageProduct selected = images.stream()
                .filter(image -> imageId.equals(image.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada en este producto"));
        for (ImageProduct image : images) {
            image.setIsPrimary(image == selected);
        }
        imageProductRepository.saveAll(images);
        return selected;
    }

    @Transactional
    public void deleteImage(Long productId, Integer imageId) {
        var images = imageProductRepository.findByProductId(productId);
        ImageProduct selected = images.stream()
                .filter(image -> imageId.equals(image.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada en este producto"));
        imageProductRepository.delete(selected);

        var remaining = images.stream().filter(image -> image != selected).toList();
        if (remaining.stream().noneMatch(image -> Boolean.TRUE.equals(image.getIsPrimary()))) {
            remaining.stream().min(Comparator.comparing(ImageProduct::getId)).ifPresent(image -> {
                image.setIsPrimary(true);
                imageProductRepository.save(image);
            });
        }

        // Solo se borra el archivo físico de las subidas propias. Las fotos importadas
        // pueden estar referenciadas también desde las variantes del producto.
        String url = selected.getImageUrl();
        if (url != null && url.matches("^/uploads/products/[0-9a-fA-F-]{36}\\.(jpg|png|webp|gif)$")) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        if (!imageProductRepository.existsByImageUrl(url)) {
                            restTemplate.postForEntity(API_URL + "/api/banner-images/delete",
                                    Map.of("imageUrl", url), String.class);
                        }
                    } catch (Exception error) {
                        log.warn("No se pudo limpiar el archivo de la imagen {} tras eliminarla del producto", url, error);
                    }
                }
            });
        }
    }

    @Transactional
    public ImageProduct setPrimaryImage(Long productId, String imageUrl) {
        ImageProduct image = addImage(productId, imageUrl);
        return selectPrimaryImage(productId, image.getId());
    }

    private void validateNewImageUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.matches("^/uploads/products/[0-9a-fA-F-]{36}\\.(jpg|png|webp|gif)$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ruta de imagen no válida");
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByProductId(Long productId) {
        return imageProductRepository.existsById(productId);
    }

    public void deleteImagesApiByProductId(Long productId) {
        String url = API_URL + "/api/products-images/delete";

        List<ImageProduct> imagesRequest = imageProductRepository.findByProductId(productId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<ImageProduct>> request = new HttpEntity<>(imagesRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        System.out.println("Response from image deletion API: " + response.getBody());
    }
    public void deleteImagesByBannerId(Long bannerId) {
        String url = API_URL + "/api/banner-images/delete";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HomeBanner body = homeBannerService.getBannerById(bannerId);

        HttpEntity<HomeBanner> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        System.out.println("Response from image deletion API: " + response.getBody());
    }

    public void deleteImagesByPostId(Long productId) {
        String url = API_URL + "/api/banner-images/delete";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Post body = postService.findPostById(productId);
        HttpEntity<Post> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        System.out.println("Response from image deletion API: " + response.getBody());
    }

}
