package com.motogear.dropshopback.shop.banner.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "home_banner", schema = "shop")
public class HomeBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;
    @Column(name = "linkUrl", nullable = false)
    private String linkUrl;
    @Column(name = "link_name", nullable = false)
    private String linkName;
}