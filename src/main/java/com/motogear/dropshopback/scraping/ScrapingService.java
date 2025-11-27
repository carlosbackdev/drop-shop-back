package com.motogear.dropshopback.scraping;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.scraping.dto.AiProductResponse;
import com.motogear.dropshopback.scraping.dto.ScrapingResponse;
import com.motogear.dropshopback.scraping.dto.update.ProductPriceUpdateScrapingList;
import com.motogear.dropshopback.scraping.dto.update.UpdateProductItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScrapingService {

    private final RestTemplate restTemplate;
    @Value("${scraping.api.url}")
    private String API_URL;

    public ScrapingResponse scrapeProduct(String url) {
        String scrapingApiUrl =API_URL+"/scrape";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, String> requestBody = Map.of("url", url);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ScrapingResponse> response = restTemplate.postForEntity(
                scrapingApiUrl,
                request,
                ScrapingResponse.class
        );
        return response.getBody();
    }
    public AiProductResponse enhanceProductAi(Product product) {
        String url = API_URL + "/enhance-title";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Product> request = new HttpEntity<>(product, headers);

        ResponseEntity<AiProductResponse> response = restTemplate.postForEntity(url, request, AiProductResponse.class);
        return response.getBody();
    }
    public String updateProductPrices(List<Product> products) {
        String url = API_URL + "/update-prices";

        List<UpdateProductItem> items = products.stream()
                .map(product -> new UpdateProductItem(
                        String.valueOf(product.getId()),
                        product.getSourceUrl()
                ))
                .toList();

        ProductPriceUpdateScrapingList requestBody = new ProductPriceUpdateScrapingList(items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductPriceUpdateScrapingList> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return response.getBody();
    }

}
