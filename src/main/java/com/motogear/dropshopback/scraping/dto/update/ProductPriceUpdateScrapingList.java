package com.motogear.dropshopback.scraping.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceUpdateScrapingList {
    private List<UpdateProductItem> products;
}
