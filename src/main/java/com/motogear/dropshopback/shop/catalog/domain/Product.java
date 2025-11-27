package com.motogear.dropshopback.shop.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "product", schema = "shop")
@ToString(exclude = "images")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "title")
    private String name;
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;
    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    @Column(name = "sell_price", precision = 10, scale = 2)
    private BigDecimal sellPrice;
    @Column(name = "discount")
    private Integer discount;
    @Column(name = "currency", length = 10)
    private String currency;
    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost;
    @Column(name = "delivery_estimate_days")
    private String deliveryEstimateDays;
    @Column(name = "variants", columnDefinition = "TEXT")
    private String variants;
    @Column (name = "seller_name")
    private String sellerName;
    @Column(name = "external_id", unique = true)
    private String externalId;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column (name = "category")
    private Integer category;
    @Column(name = "delivery_min_date")
    private LocalDate deliveryMinDate;
    @Column(name = "delivery_max_date")
    private LocalDate deliveryMaxDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ImageProduct> images = new ArrayList<>();


}
