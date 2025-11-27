package com.motogear.dropshopback.shop.order.domain;

/**
 * Estados posibles de una orden
 */
public enum OrderStatus {
    PENDING,      // Pendiente de pago
    PAID,         // Pagada
    PROCESSING,   // En procesamiento
    SHIPPED,      // Enviada
    DELIVERED,    // Entregada
    CANCELLED     // Cancelada
}

