package com.motogear.dropshopback.scraping.dto;

import com.motogear.dropshopback.shop.catalog.dto.ScriptingProductRequest;
import lombok.Data;

@Data
public class ScrapingResponse {
    private boolean success;
    private ScriptingProductRequest data;
}
