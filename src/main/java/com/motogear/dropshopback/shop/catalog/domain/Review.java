package com.motogear.dropshopback.shop.catalog.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "review", schema = "shop")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer rating;
}
