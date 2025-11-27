package com.motogear.dropshopback.scraping.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductItem {
    private String productId;
    private String url;
}
