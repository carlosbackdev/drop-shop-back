package com.motogear.dropshopback.shop.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la respuesta del tracking desde la API de scraping
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponseDto {
    private Boolean success;
    private String message;
    private TrackingData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingData {
        private String trackingNumber;
        private String status;
        private String statusDescription;
        private String origin;
        private String destination;
        private String weight;
        private List<String> couriers;
        private Integer daysOnRoute;
        private List<TimelineEvent> timeline;
        private String sourceUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineEvent {
        private String date;
        // desactuvado por poenr Aliexpress
        // private String courier;
        private String title;
        private String location;
        private Boolean isActive;
    }
}

