package com.motogear.dropshopback.scraping.dto;

import lombok.Data;

@Data
public class AiProductResponse {
    private boolean success;
    private Long id;
    private String name;
    private String keywords;
    private String details;
}
