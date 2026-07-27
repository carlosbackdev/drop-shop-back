package com.motogear.dropshopback.shop.lead.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Interés registrado por un visitante que todavía no puede comprar
 * (producto en estado COMING_SOON / DRAFT) pero quiere que le avisen
 * o quiere consultar la compatibilidad de su moto.
 *
 * Sustituye al antiguo flujo "mailto:" del formulario de contacto, que
 * no dejaba ningún rastro estructurado en el backend.
 */
@Entity
@Table(name = "leads", schema = "shop")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "motorcycle_model", nullable = false)
    private String motorcycleModel;

    @Column(name = "motorcycle_year")
    private String motorcycleYear;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private LeadSource source;

    /** Slug del producto sobre el que se registra el interés (ej. "ordenador-bordo-kawasaki"). */
    @Column(name = "product_slug")
    private String productSlug;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
