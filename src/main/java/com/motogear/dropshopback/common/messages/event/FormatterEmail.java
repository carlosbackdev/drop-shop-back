package com.motogear.dropshopback.common.messages.event;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.checkout.domain.Checkout;
import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import com.motogear.dropshopback.users.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Servicio para formatear el cuerpo de emails de la tienda
 */
@Component
public class FormatterEmail {

    @Value("${app.store.name}")
    private String storeName;

    @Value("${app.store.url}")
    private String storeUrl;

    @Value("${app.support.email}")
    private String supportEmail;

    @Value("${app.support.phone}")
    private String supportPhone;

    @Value("${app.store.logo.url}")
    private String storeLogoUrl;


    public String buildBodyMail(User user, Order order, Checkout checkout, Map<CartShaded, Product> cartProductMap) {
        // Configuración regional y formateadores
        Locale locale = Locale.of("es","ES");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Extraer datos básicos
        String orderId = String.valueOf(order.getId());
        String orderDate = order.getCreatedAt() != null
                ? order.getCreatedAt().format(dateFormatter)
                : "";
        String customerName = safe(user.getFullName());
        String customerEmail = safe(user.getEmail());

        // Calcular subtotal y costos de envío
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalShippingCost = BigDecimal.valueOf(1.99);

        for (Map.Entry<CartShaded, Product> entry : cartProductMap.entrySet()) {
            CartShaded cart = entry.getKey();
            Product product = entry.getValue();

            BigDecimal unitPrice = product.getSellPrice() != null ? product.getSellPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        // Construir tabla de productos HTML
        StringBuilder itemsTable = buildItemsTable(cartProductMap, currencyFormatter);

        // Obtener total final
        BigDecimal total = order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO;

        // Formatear montos
        String subtotalStr = currencyFormatter.format(subtotal);
        String shippingStr = currencyFormatter.format(totalShippingCost);
        String totalStr = currencyFormatter.format(total);

        // Construir dirección de envío
        String shippingAddress = buildShippingAddress(checkout);

        // Obtener plantilla HTML
        String template = getBaseTemplate();

        // Reemplazar placeholders
        template = template.replace("{{orderId}}", orderId);
        template = template.replace("{{orderDate}}", orderDate);
        template = template.replace("{{customerName}}", customerName);
        template = template.replace("{{customerEmail}}", customerEmail);
        template = template.replace("{{itemsTable}}", itemsTable.toString());
        template = template.replace("{{subtotal}}", subtotalStr);
        template = template.replace("{{shippingCost}}", shippingStr);
        template = template.replace("{{total}}", totalStr);
        template = template.replace("{{shippingAddress}}", shippingAddress);
        template = template.replace("{{supportEmail}}", supportEmail);
        template = template.replace("{{supportPhone}}", supportPhone);
        template = template.replace("{{storeName}}", storeName);
        template = template.replace("{{storeUrl}}", storeUrl);
        template = template.replace("{{storeLogoUrl}}", storeLogoUrl);

        return template;
    }

    /**
     * Construye la tabla HTML con los items del pedido
     */
    private StringBuilder buildItemsTable(Map<CartShaded, Product> cartProductMap, NumberFormat currencyFormatter) {
        StringBuilder itemsTable = new StringBuilder();
        itemsTable.append("<table>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>Producto</th>")
                .append("<th>Variante</th>")
                .append("<th>Cantidad</th>")
                .append("<th>Precio unitario</th>")
                .append("<th>Total</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        for (Map.Entry<CartShaded, Product> entry : cartProductMap.entrySet()) {
            CartShaded cart = entry.getKey();
            Product product = entry.getValue();

            int quantity = cart.getQuantity();
            BigDecimal unitPrice = product.getSellPrice() != null ? product.getSellPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            String productName = escapeHtml(safe(product.getName()));
            String variant = cart.getVariant() != null ? escapeHtml(safe(cart.getVariant())) : "—";
            String quantityStr = String.valueOf(quantity);
            String unitPriceStr = currencyFormatter.format(unitPrice);
            String lineTotalStr = currencyFormatter.format(lineTotal);

            itemsTable.append("<tr>")
                    .append("<td>").append(productName).append("</td>")
                    .append("<td>").append(variant).append("</td>")
                    .append("<td style='text-align: center;'>").append(quantityStr).append("</td>")
                    .append("<td style='text-align: right;'>").append(unitPriceStr).append("</td>")
                    .append("<td style='text-align: right;'>").append(lineTotalStr).append("</td>")
                    .append("</tr>");
        }

        itemsTable.append("</tbody></table>");
        return itemsTable;
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
     * Plantilla HTML base con estilo muy formal de ecommerce
     */
    private String getBaseTemplate() {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Confirmación de pago – Pedido {{orderId}}</title>
              <style>
                body {
                  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                  color: #222222;
                  font-size: 14px;
                  line-height: 1.6;
                  background-color: #f4f4f4;
                  margin: 0;
                  padding: 0;
                }
                .email-wrapper { background-color: #f4f4f4; padding: 20px 0; }
                .container { max-width: 640px; margin: 0 auto; background-color: #ffffff; padding: 0; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }
                .header { background-color: #ffffff; color: #1a1a1a; padding: 24px 32px; text-align: center; border-bottom: 1px solid #ececec; }
                .header img { max-height: 60px; display: block; margin: 0 auto 8px; }
                .brand-name { font-size: 22px; font-weight: 700; margin: 0; letter-spacing: 0.5px; }
                .content { padding: 32px; }
                .greeting { font-size: 16px; margin-bottom: 24px; color: #333333; }
                .order-info { background-color: #f8f9fa; border-left: 4px solid #ff5722; padding: 16px; margin-bottom: 24px; }
                .order-info p { margin: 4px 0; font-size: 14px; }
                .order-info strong { color: #1a1a1a; }
                h2 { font-size: 18px; color: #1a1a1a; border-bottom: 2px solid #e0e0e0; padding-bottom: 8px; margin-top: 32px; margin-bottom: 16px; font-weight: 600; }
                table { width: 100%; border-collapse: collapse; margin-top: 16px; margin-bottom: 16px; }
                th, td { border: 1px solid #e0e0e0; padding: 12px; text-align: left; font-size: 13px; }
                th { background-color: #f8f9fa; font-weight: 600; color: #1a1a1a; text-transform: uppercase; font-size: 12px; letter-spacing: 0.5px; }
                tbody tr:nth-child(even) { background-color: #fafafa; }
                .totals { margin-top: 24px; border-top: 2px solid #e0e0e0; padding-top: 16px; }
                .totals td { border: none; padding: 8px 12px; }
                .totals .label { text-align: right; font-weight: 600; color: #555555; width: 70%; }
                .totals .value { text-align: right; color: #222222; width: 30%; }
                .totals .total-row .label { font-size: 16px; color: #1a1a1a; }
                .totals .total-row .value { font-size: 18px; color: #ff5722; font-weight: 700; }
                .shipping-info { background-color: #f8f9fa; padding: 16px; border-radius: 4px; margin: 16px 0; }
                .support-section { background-color: #fff9e6; border: 1px solid #ffd700; padding: 20px; border-radius: 4px; margin: 24px 0; }
                .footer { background-color: #f8f9fa; padding: 24px 32px; text-align: center; font-size: 12px; color: #777777; border-top: 1px solid #e0e0e0; }
                .footer a { color: #ff5722; text-decoration: none; font-weight: 600; }
                .footer a:hover { text-decoration: underline; }
                .divider { height: 1px; background-color: #e0e0e0; margin: 24px 0; }
                @media (max-width: 640px) { .content { padding: 20px; } .header { padding: 20px; } }
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
                      <p><strong>Pedido confirmado:</strong> #{{orderId}}</p>
                      <p><strong>Fecha:</strong> {{orderDate}}</p>
                      <p><strong>Estado:</strong> Pago recibido correctamente</p>
                    </div>
                    <p class="greeting">Estimado/a <strong>{{customerName}}</strong>,</p>
                    <p>Le informamos que hemos recibido correctamente el pago correspondiente a su pedido <strong>#{{orderId}}</strong>. Su transacción ha sido procesada satisfactoriamente y su pedido se encuentra en proceso de preparación.</p>
                    <h2>Resumen del pedido</h2>
                    {{itemsTable}}
                    <div class="totals">
                      <table>
                        <tr><td class="label">Subtotal:</td><td class="value">{{subtotal}}</td></tr>
                        <tr><td class="label">Gastos de envío:</td><td class="value">{{shippingCost}}</td></tr>
                        <tr class="total-row"><td class="label">TOTAL PAGADO:</td><td class="value">{{total}}</td></tr>
                      </table>
                    </div>
                    <h2>Información de envío</h2>
                    <div class="shipping-info">
                      <p><strong>Destinatario:</strong> {{customerName}}</p>
                      <p><strong>Email de contacto:</strong> {{customerEmail}}</p>
                      <p><strong>Dirección de entrega:</strong><br>{{shippingAddress}}</p>
                    </div>
                    <div class="divider"></div>
                    <h2>Próximos pasos</h2>
                    <p>Su pedido será procesado en un plazo máximo de <strong>24-48 horas laborables</strong>. Una vez que el pedido sea expedido, recibirá un correo electrónico adicional con el número de seguimiento para que pueda consultar el estado de su envío.</p>
                    <div class="support-section">
                      <h3>¿Necesita asistencia?</h3>
                      <p>Si tiene alguna consulta o incidencia relacionada con su pedido, indique su número <strong>#{{orderId}}</strong> al escribirnos:</p>
                      <p><strong>Correo electrónico:</strong> {{supportEmail}}</p>
                      <p style="margin-top: 12px; font-size: 11px; color: #666666;">Horario de atención: Lunes a Viernes, 9:00 - 18:00 h (GMT+1)</p>
                    </div>
                    <p style="margin-top: 24px;">Gracias por su confianza en {{storeName}}. Es un placer poder atenderle.</p>
                  </div>
                  <div class="footer">
                    <p><strong>{{storeName}}</strong></p>
                    <p><a href="{{storeUrl}}">{{storeUrl}}</a></p>
                    <p style="margin-top: 16px; font-size: 11px;">Este es un correo electrónico automático, no responda directamente.</p>
                    <p style="margin-top: 8px; font-size: 11px;">© 2025 {{storeName}}. Todos los derechos reservados.</p>
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
