package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.scraping.dto.update.ProductPriceUpdateScrapingList;
import com.motogear.dropshopback.scraping.dto.update.UpdateProductItem;
import com.motogear.dropshopback.shop.catalog.domain.ImageProduct;
import com.motogear.dropshopback.shop.catalog.repository.ImageProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageProductService {

    private final ImageProductRepository imageProductRepository;
    @Value("${scraping.api.url}")
    private String API_URL;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public List<ImageProduct> findById(Long productId) {
        return imageProductRepository.findByProductId(productId);
    }
    @Transactional(readOnly = true)
    public ImageProduct findByProductIdAndIsPrimary(Long productId) {
        return imageProductRepository.findByProductIdAndIsPrimary(productId, true);
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

}
