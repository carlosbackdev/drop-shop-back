package com.motogear.dropshopback.common.messages.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motogear.dropshopback.shop.checkout.domain.Checkout;
import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.order.domain.Tracking;
import com.motogear.dropshopback.users.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para formatear el cuerpo de emails de pedido enviado con información de tracking
 */
@Component
public class FormatterEmailShipped {

    @Value("${app.store.name}")
    private String storeName;

    @Value("${app.store.url}")
    private String storeUrl;

    @Value("${app.support.email}")
    private String supportEmail;

    @Value("${app.support.phone}")
    private String supportPhone;

    @Value("${domain.name}")
    private String trackBaseUrl;

    @Value("${app.store.logo.url}")
    private String storeLogoUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String buildShippedEmailBody(User user, Order order, Checkout checkout, Tracking tracking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String orderId = String.valueOf(order.getId());
        String orderDate = order.getCreatedAt() != null ? order.getCreatedAt().format(dateFormatter) : "";
        String shippedDate = order.getUpdatedAt() != null ? order.getUpdatedAt().format(dateFormatter) : "";
        String customerName = safe(user.getFullName());
        String customerEmail = safe(user.getEmail());

        String trackingNumber = tracking != null ? safe(tracking.getTrackingNumber()) : "";
        String trackingStatus = tracking != null ? safe(tracking.getStatus()) : "";
        String trackingStatusDesc = tracking != null ? safe(tracking.getStatusDescription()) : "";
        String trackingOrigin = tracking != null ? safe(tracking.getOrigin()) : "";
        String trackingDestination = tracking != null ? safe(tracking.getDestination()) : "";
        String trackingDays = tracking != null && tracking.getDaysOnRoute() != null ? String.valueOf(tracking.getDaysOnRoute()) : "";
        String trackingCouriers = buildCouriersInline(tracking);
        String trackingLink = buildTrackingLink(trackingNumber);

        String shippingAddress = buildShippingAddress(checkout);

        String template = getShippedTemplate();

        template = template.replace("{{orderId}}", orderId)
                .replace("{{orderDate}}", orderDate)
                .replace("{{shippedDate}}", shippedDate)
                .replace("{{customerName}}", customerName)
                .replace("{{customerEmail}}", customerEmail)
                .replace("{{shippingAddress}}", shippingAddress)
                .replace("{{supportEmail}}", supportEmail)
                .replace("{{supportPhone}}", supportPhone)
                .replace("{{storeName}}", storeName)
                .replace("{{storeUrl}}", storeUrl)
                .replace("{{trackingNumber}}", trackingNumber)
                .replace("{{trackingStatus}}", trackingStatus)
                .replace("{{trackingStatusDesc}}", trackingStatusDesc)
                .replace("{{trackingOrigin}}", trackingOrigin)
                .replace("{{trackingDestination}}", trackingDestination)
                .replace("{{trackingDays}}", trackingDays)
                .replace("{{trackingCouriers}}", trackingCouriers)
                .replace("{{trackingLink}}", trackingLink)
                .replace("{{storeLogoUrl}}", storeLogoUrl);

        return template;
    }

    private String buildCouriersInline(Tracking tracking) {
        if (tracking == null || tracking.getCouriers() == null) return "";
        try {
            List<String> couriers = objectMapper.readValue(tracking.getCouriers(), new TypeReference<>(){});
            return String.join(", ", couriers);
        } catch (Exception e) {
            return tracking.getCouriers();
        }
    }

    private String buildTrackingLink(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) return trackBaseUrl;
        // Añadimos como query param para que la página pueda autopoblarlo
        return trackBaseUrl + "?tracking=" + trackingNumber;
    }

    /**
     * Construye la dirección de envío formateada
     */
    private String buildShippingAddress(Checkout checkout) {
        StringBuilder sb = new StringBuilder();
        if (checkout.getAddress() != null) {
            sb.append(escapeHtml(safe(checkout.getAddress()))).append("<br>");
        }
        if (checkout.getPostalCode() != null || checkout.getCity() != null) {
            sb.append(escapeHtml(safe(checkout.getPostalCode())))
                    .append(" ")
                    .append(escapeHtml(safe(checkout.getCity())))
                    .append("<br>");
        }
        if (checkout.getCountry() != null) {
            sb.append(escapeHtml(safe(checkout.getCountry())));
        }
        if (checkout.getPhoneNumber() != null) {
            sb.append("<br>Tel: ").append(escapeHtml(safe(checkout.getPhoneNumber())));
        }
        return sb.toString();
    }

    /**
     * Plantilla HTML para email de pedido enviado
     */
    private String getShippedTemplate() {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Pedido enviado – Pedido {{orderId}}</title>
              <style>
                body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #222; font-size: 14px; line-height:1.6; background:#f4f4f4; margin:0; padding:0; }
                .email-wrapper { background:#f4f4f4; padding:20px 0; }
                .container { max-width:640px; margin:0 auto; background:#fff; border-radius:8px; box-shadow:0 2px 4px rgba(0,0,0,.1); overflow:hidden; }
                .header { background:#ffffff; padding:24px 32px; text-align:center; border-bottom:1px solid #ececec; }
                .header img { max-height:60px; display:block; margin:0 auto 8px; }
                .brand-name { font-size:22px; font-weight:700; margin:0; letter-spacing:.5px; color:#1a1a1a; }
                .order-info { background:#e8f5e9; border-left:4px solid #4caf50; padding:16px; margin-bottom:24px; }
                .status-badge { display:inline-block; background:#4caf50; color:#fff; padding:6px 16px; border-radius:20px; font-weight:600; font-size:13px; margin-top:8px; }
                h2 { font-size:18px; color:#1a1a1a; border-bottom:2px solid #e0e0e0; padding-bottom:8px; margin-top:32px; margin-bottom:16px; font-weight:600; }
                .tracking-section { background:#fff3cd; border:2px solid #ffc107; padding:20px; border-radius:6px; margin:24px 0; }
                .tracking-data-table { width:100%; border-collapse:collapse; margin-top:12px; }
                .tracking-data-table th, .tracking-data-table td { border:1px solid #e0e0e0; padding:8px 10px; font-size:12px; text-align:left; }
                .tracking-data-table th { background:#fff8e1; }
                .shipping-info { background:#f8f9fa; padding:16px; border-radius:4px; margin:16px 0; }
                .support-section { background:#f8f9fa; border:1px solid #dee2e6; padding:20px; border-radius:4px; margin:24px 0; }
                .footer { background:#f8f9fa; padding:24px 32px; text-align:center; font-size:12px; color:#777; border-top:1px solid #e0e0e0; }
                .footer a { color:#4caf50; text-decoration:none; font-weight:600; }
                .footer a:hover { text-decoration:underline; }
              </style>
            </head>
            <body>
              <div class="email-wrapper">
                <div class="container">
                  <div class="header">
                    <img src="{{storeLogoUrl}}" alt="Logo {{storeName}}" />
                    <h1 class="brand-name">{{storeName}}</h1>
                  </div>
                  <div class="content">
                    <div class="order-info">
                      <p><strong>Pedido:</strong> #{{orderId}}</p>
                      <p><strong>Fecha del pedido:</strong> {{orderDate}}</p>
                      <p><strong>Fecha de envío:</strong> {{shippedDate}}</p>
                      <p><span class="status-badge">✓ ENVIADO</span></p>
                    </div>
                    <h2>Seguimiento del envío</h2>
                    <div class="tracking-section">
                      <h3>📦 Detalles del Tracking</h3>
                      <table class="tracking-data-table">
                        <tr><th>Nº Seguimiento</th><td>{{trackingNumber}}</td></tr>
                        <tr><th>Estado</th><td>{{trackingStatus}}</td></tr>
                        <tr><th>Descripción</th><td>{{trackingStatusDesc}}</td></tr>
                        <tr><th>Origen</th><td>{{trackingOrigin}}</td></tr>
                        <tr><th>Destino</th><td>{{trackingDestination}}</td></tr>
                        <tr><th>Días en ruta</th><td>{{trackingDays}}</td></tr>
                        <tr><th>Couriers</th><td>{{trackingCouriers}}</td></tr>
                      </table>
                      <p style="margin-top:12px;">Consulta más actualizaciones: <a href="{{trackingLink}}">{{trackingLink}}</a></p>
                    </div>
                    <h2>Información de entrega</h2>
                    <div class="shipping-info">
                      <p><strong>Destinatario:</strong> {{customerName}}</p>
                      <p><strong>Email:</strong> {{customerEmail}}</p>
                      <p><strong>Dirección:</strong><br>{{shippingAddress}}</p>
                    </div>
                    <h2>¿Necesita ayuda?</h2>
                    <div class="support-section">
                      <h3>Atención al cliente</h3>
                      <p>Indique su Nº de pedido #{{orderId}} al escribir a <strong>{{supportEmail}}</strong>.</p>
                    </div>
                    <p style="margin-top:24px;">Gracias por su confianza en {{storeName}}.</p>
                  </div>
                  <div class="footer">
                    <p><strong>{{storeName}}</strong></p>
                    <p><a href="{{storeUrl}}">{{storeUrl}}</a></p>
                    <p style="margin-top:16px; font-size:11px;">Correo automático, no responder.</p>
                    <p style="margin-top:8px; font-size:11px;">© 2025 {{storeName}}. Todos los derechos reservados.</p>
                  </div>
                </div>
              </div>
            </body>
            </html>
            """;
    }

    /**
     * Retorna cadena vacía si el valor es null
     */
    private String safe(String value) {
        return value == null ? "" : value;
    }

    /**
     * Escapa caracteres HTML para prevenir XSS
     */
    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
