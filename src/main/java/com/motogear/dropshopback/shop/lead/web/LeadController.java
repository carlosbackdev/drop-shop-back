package com.motogear.dropshopback.shop.lead.web;

import com.motogear.dropshopback.shop.lead.domain.Lead;
import com.motogear.dropshopback.shop.lead.dto.LeadRequest;
import com.motogear.dropshopback.shop.lead.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Registro de interés de visitantes en productos todavía no disponibles (pre-lanzamiento)")
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    @Operation(summary = "Registrar interés de un visitante (early access o consulta de compatibilidad)")
    public ResponseEntity<Lead> createLead(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.ok(leadService.save(request));
    }

    @GetMapping("/admin")
    @Operation(summary = "Listar todos los leads registrados, más recientes primero (uso interno)")
    public ResponseEntity<List<Lead>> listLeads() {
        return ResponseEntity.ok(leadService.findAll());
    }
}
