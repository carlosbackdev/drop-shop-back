package com.motogear.dropshopback.shop.catalog.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ScriptingProductRequest {
    private String title;
    private String details;
    private Map<String, Object> specifications;
    private Double basePrice;
    private Double originalPrice;
    private Integer discount;
    private String currency;
    private List<String> images;
    private Double shippingCost;
    private DeliveryEstimate deliveryEstimateDays;
    private List<Variant> variants;
    private String sellerName;
    private String externalId;
    private String sourceUrl;

    @Data
    public static class DeliveryEstimate {
        private Integer min;
        private Integer max;
    }

    @Data
    public static class Variant {
        private String groupName;
        private List<VariantOption> options;
    }

    @Data
    public static class VariantOption {
        private String value;
        private Double extraPrice;
        private String image;
    }
}

