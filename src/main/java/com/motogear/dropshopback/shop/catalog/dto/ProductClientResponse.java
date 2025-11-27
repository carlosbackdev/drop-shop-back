package com.motogear.dropshopback.shop.catalog.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProductClientResponse {
    private Long id;
    private String name;
    private String details;
    private String specifications;
    private BigDecimal originalPrice;
    private BigDecimal sellPrice;
    private Integer discount;
    private String currency;
    private BigDecimal shippingCost;
    private String deliveryEstimateDays;
    private LocalDate deliveryMinDate;
    private LocalDate deliveryMaxDate;
    private String variant;
    private String sellerName;
    private Integer category;
    private String keywords;
    private List<String> images;
}