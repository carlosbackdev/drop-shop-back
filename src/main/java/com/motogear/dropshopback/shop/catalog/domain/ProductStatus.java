package com.motogear.dropshopback.shop.catalog.domain;

/**
 * Estado comercial del producto. Permite preparar ficha, precio y stock sin
 * publicar la venta antes de que el hardware esté listo.
 */
public enum ProductStatus {
    DRAFT,
    COMING_SOON,
    AVAILABLE,
    OUT_OF_STOCK,
    ARCHIVED;

    public boolean isPubliclyVisible() {
        return this == COMING_SOON || this == AVAILABLE || this == OUT_OF_STOCK;
    }
}
