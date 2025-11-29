package com.motogear.dropshopback.shop.catalog.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "best_products")
@Data
public class BestProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
