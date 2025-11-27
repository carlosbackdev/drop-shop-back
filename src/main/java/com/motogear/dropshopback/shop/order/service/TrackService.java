package com.motogear.dropshopback.shop.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motogear.dropshopback.shop.order.domain.Tracking;
import com.motogear.dropshopback.shop.order.dto.TrackingResponseDto;
import com.motogear.dropshopback.shop.order.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackService {
    private final RestTemplate restTemplate;
    private final TrackingRepository trackingRepository;
    private final ObjectMapper objectMapper;

    @Value("${scraping.api.url}")
    private String API_URL;

    public Tracking scrapeTrack(String trackingNumber,Tracking tracking) {
        String scrapingApiUrl = API_URL + "/tracking";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of("trackingNumber", trackingNumber);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<TrackingResponseDto> response = restTemplate.postForEntity(
                    scrapingApiUrl,
                    request,
                    TrackingResponseDto.class
            );

            if (response.getBody() != null && response.getBody().getSuccess()) {
                TrackingResponseDto.TrackingData data = response.getBody().getData();

                return buildTrackingFromDto(data, trackingNumber, tracking);
            } else {
                log.error("Error al obtener tracking: {}", response.getBody() != null ? response.getBody().getMessage() : "Sin respuesta");
                throw new RuntimeException("Error al obtener información de tracking");
            }
        } catch (Exception e) {
            log.error("Error al hacer scraping del tracking {}: {}", trackingNumber, e.getMessage());
            throw new RuntimeException("Error al procesar tracking: " + e.getMessage());
        }
    }

    @Transactional
    public Tracking getTrackingByNumber(String trackingNumber) {
        return trackingRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Tracking no encontrado"));
    }

    @Transactional
    public Tracking getTrackingByOrderId(Long orderId) {
        return trackingRepository.findByOrderId(orderId)
                .orElse(null);
    }

    @Transactional
    public void saveTracking(Tracking tracking, Long orderId) {
        tracking.setOrderId(orderId);
        trackingRepository.save(tracking);
    }

    private Tracking buildTrackingFromDto(TrackingResponseDto.TrackingData data,String trackingNumber,Tracking tracking) {
        tracking.setTrackingNumber(trackingNumber);
        tracking.setStatus(data.getStatus());
        tracking.setStatusDescription(data.getStatusDescription());
        tracking.setOrigin(data.getOrigin());
        tracking.setDestination(data.getDestination());
        tracking.setWeight(data.getWeight());
        tracking.setDaysOnRoute(data.getDaysOnRoute());
        tracking.setSourceUrl(data.getSourceUrl());
        try {
            tracking.setCouriers(objectMapper.writeValueAsString(data.getCouriers()));
            tracking.setTimeline(objectMapper.writeValueAsString(data.getTimeline()));
        } catch (Exception e) {
            log.error("Error al convertir listas a JSON strings para el tracking {}: {}", data.getTrackingNumber(), e.getMessage());
        }
        return tracking;
    }
}
