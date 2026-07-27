package com.motogear.dropshopback.shop.lead.domain;

/**
 * Punto de la web desde el que un usuario ha registrado su interés.
 * Permite segmentar la demanda por intención sin depender de texto libre.
 */
public enum LeadSource {
    /** CTA "Reservar mi plaza en el lanzamiento" (home / cierre de página). */
    EARLY_ACCESS,
    /** CTA "Consultar mi Kawasaki" (sección de compatibilidad). */
    COMPATIBILITY
}
