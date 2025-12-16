package com.motogear.dropshopback.shop.blog.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "post", schema = "shop")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "slug", nullable = false)
    private String slug;
    @Column(name = "excerpt", nullable = false)
    private String excerpt;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "author")
    private String author;
    @Column(name = "published_at")
    private Date date;
    @Column (name = "image_url")
    private String imageUrl;
    @Column(name = "tags", nullable = false, columnDefinition = "TEXT")
    private String tags;
    @Column(name = "read_time")
    private Integer readTime;
}
