package com.motogear.dropshopback.shop.catalog.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "category", schema = "shop")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "logo")
    private String logo;
}
