package com.motogear.dropshopback.shop.lead.service;

import com.motogear.dropshopback.common.messages.mail.MailService;
import com.motogear.dropshopback.shop.lead.domain.Lead;
import com.motogear.dropshopback.shop.lead.dto.LeadRequest;
import com.motogear.dropshopback.shop.lead.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;
    private final MailService mailService;

    @Value("${app.store.name}")
    private String storeName;

    @Value("${app.support.email}")
    private String supportEmail;

    /**
     * Registra un lead nuevo o actualiza uno existente (mismo email + mismo producto)
     * para no duplicar entradas cuando alguien reenvía el formulario con datos corregidos.
     * Solo se notifica al equipo cuando el lead es nuevo, para no generar ruido.
     */
    @Transactional
    public Lead save(LeadRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        Optional<Lead> existing = leadRepository.findByEmailIgnoreCaseAndProductSlug(normalizedEmail, request.getProductSlug());

        boolean isNewLead = existing.isEmpty();
        Lead lead = existing.map(current -> applyUpdate(current, request, normalizedEmail))
                .orElseGet(() -> buildLead(request, normalizedEmail));

        Lead saved = leadRepository.save(lead);

        if (isNewLead) {
            notifyTeam(saved);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Lead> findAll() {
        return leadRepository.findAllByOrderByCreatedAtDesc();
    }

    private Lead buildLead(LeadRequest request, String normalizedEmail) {
        return Lead.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .motorcycleModel(request.getMotorcycleModel().trim())
                .motorcycleYear(blankToNull(request.getMotorcycleYear()))
                .message(blankToNull(request.getMessage()))
                .source(request.getSource())
                .productSlug(blankToNull(request.getProductSlug()))
                .build();
    }

    private Lead applyUpdate(Lead lead, LeadRequest request, String normalizedEmail) {
        lead.setName(request.getName().trim());
        lead.setEmail(normalizedEmail);
        lead.setMotorcycleModel(request.getMotorcycleModel().trim());
        lead.setMotorcycleYear(blankToNull(request.getMotorcycleYear()));
        lead.setSource(request.getSource());
        if (blankToNull(request.getMessage()) != null) {
            lead.setMessage(request.getMessage().trim());
        }
        return lead;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private void notifyTeam(Lead lead) {
        try {
            String subject = "Nuevo interés en " + storeName + " · " + lead.getMotorcycleModel();
            mailService.sendEmail(supportEmail, subject, buildNotificationBody(lead));
        } catch (Exception e) {
            // Un fallo de email nunca debe tirar abajo el registro del lead:
            // el dato ya está guardado, lo peor que pasa es que no llega el aviso.
            log.error("No se pudo enviar la notificación de nuevo lead (id={})", lead.getId(), e);
        }
    }

    private String buildNotificationBody(Lead lead) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String sourceLabel = switch (lead.getSource()) {
            case EARLY_ACCESS -> "Acceso anticipado / lanzamiento";
            case COMPATIBILITY -> "Consulta de compatibilidad";
        };

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"></head>
                <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color:#222; font-size:14px; line-height:1.6;">
                  <div style="max-width:560px; margin:0 auto; border:1px solid #e2e2e2; border-radius:8px; overflow:hidden;">
                    <div style="background:#0a0b0b; color:#fff; padding:18px 24px;">
                      <strong style="font-size:16px;">Nuevo interés registrado</strong>
                    </div>
                    <div style="padding:20px 24px;">
                      <table style="width:100%%; border-collapse:collapse;">
                        <tr><td style="padding:6px 0; color:#666; width:140px;">Nombre</td><td style="padding:6px 0;"><strong>%s</strong></td></tr>
                        <tr><td style="padding:6px 0; color:#666;">Email</td><td style="padding:6px 0;"><a href="mailto:%s">%s</a></td></tr>
                        <tr><td style="padding:6px 0; color:#666;">Moto</td><td style="padding:6px 0;">%s%s</td></tr>
                        <tr><td style="padding:6px 0; color:#666;">Origen</td><td style="padding:6px 0;">%s</td></tr>
                        <tr><td style="padding:6px 0; color:#666;">Producto</td><td style="padding:6px 0;">%s</td></tr>
                        <tr><td style="padding:6px 0; color:#666; vertical-align:top;">Mensaje</td><td style="padding:6px 0;">%s</td></tr>
                        <tr><td style="padding:6px 0; color:#666;">Fecha</td><td style="padding:6px 0;">%s</td></tr>
                      </table>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(lead.getName()),
                escapeHtml(lead.getEmail()), escapeHtml(lead.getEmail()),
                escapeHtml(lead.getMotorcycleModel()),
                lead.getMotorcycleYear() != null ? " (" + escapeHtml(lead.getMotorcycleYear()) + ")" : "",
                escapeHtml(sourceLabel),
                lead.getProductSlug() != null ? escapeHtml(lead.getProductSlug()) : "—",
                lead.getMessage() != null ? escapeHtml(lead.getMessage()) : "—",
                lead.getCreatedAt().format(dateFormatter)
        );
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
