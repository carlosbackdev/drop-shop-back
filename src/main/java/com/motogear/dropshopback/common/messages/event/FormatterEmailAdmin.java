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

@Component
public class FormatterEmailAdmin {

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
        Locale locale = Locale.of("es","ES");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String orderId = String.valueOf(order.getId());
        String orderDate = order.getCreatedAt() != null ? order.getCreatedAt().format(dateFormatter) : "";
        String customerName = safe(user.getFullName());
        String customerEmail = safe(user.getEmail());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal shippingAccumulated = BigDecimal.ZERO;

        for (Map.Entry<CartShaded, Product> entry : cartProductMap.entrySet()) {
            CartShaded cart = entry.getKey();
            Product product = entry.getValue();
            BigDecimal unit = product.getSellPrice() != null ? product.getSellPrice() : BigDecimal.ZERO;
            BigDecimal line = unit.multiply(BigDecimal.valueOf(cart.getQuantity()));
            subtotal = subtotal.add(line);
            BigDecimal shipUnit = product.getShippingCost() != null ? product.getShippingCost() : BigDecimal.ZERO;
            shippingAccumulated = shippingAccumulated.add(shipUnit.multiply(BigDecimal.valueOf(cart.getQuantity())));
        }
        BigDecimal total = order.getTotal() != null ? order.getTotal() : subtotal.add(shippingAccumulated);

        StringBuilder itemsTable = buildAdminItemsTable(cartProductMap, currencyFormatter);
        String shippingAddress = buildShippingAddress(checkout);

        String template = getAdminTemplate();
        template = template.replace("{{orderId}}", orderId)
                .replace("{{orderDate}}", orderDate)
                .replace("{{customerName}}", customerName)
                .replace("{{customerEmail}}", customerEmail)
                .replace("{{itemsTable}}", itemsTable.toString())
                .replace("{{subtotal}}", currencyFormatter.format(subtotal))
                .replace("{{shippingTotal}}", currencyFormatter.format(shippingAccumulated))
                .replace("{{total}}", currencyFormatter.format(total))
                .replace("{{shippingAddress}}", shippingAddress)
                .replace("{{storeLogoUrl}}", storeLogoUrl)
                .replace("{{storeName}}", storeName)
                .replace("{{storeUrl}}", storeUrl)
                .replace("{{supportEmail}}", supportEmail)
                .replace("{{supportPhone}}", supportPhone);
        return template;
    }

    private StringBuilder buildAdminItemsTable(Map<CartShaded, Product> cartProductMap, NumberFormat cf) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='items'>")
          .append("<thead><tr>")
          .append("<th>ID</th><th>ExtID</th><th>Producto</th><th>Base</th><th>Sell</th><th>Envío</th><th>Días</th><th>Variante</th><th>Cant</th><th>Total línea</th>")
          .append("</tr></thead><tbody>");
        for (Map.Entry<CartShaded, Product> entry : cartProductMap.entrySet()) {
            CartShaded cart = entry.getKey();
            Product p = entry.getValue();
            int q = cart.getQuantity();
            BigDecimal base = p.getBasePrice() != null ? p.getBasePrice() : BigDecimal.ZERO;
            BigDecimal sell = p.getSellPrice() != null ? p.getSellPrice() : BigDecimal.ZERO;
            BigDecimal ship = p.getShippingCost() != null ? p.getShippingCost() : BigDecimal.ZERO;
            BigDecimal line = sell.multiply(BigDecimal.valueOf(q));
            sb.append("<tr>")
              .append("<td>").append(p.getId()).append("</td>")
              .append("<td>").append(escapeHtml(safe(p.getExternalId()))).append("</td>")
              .append("<td>").append(escapeHtml(safe(p.getName()))).append("</td>")
              .append("<td style='text-align:right'>").append(cf.format(base)).append("</td>")
              .append("<td style='text-align:right'>").append(cf.format(sell)).append("</td>")
              .append("<td style='text-align:right'>").append(cf.format(ship)).append("</td>")
              .append("<td>").append(escapeHtml(safe(p.getDeliveryEstimateDays()))).append("</td>")
              .append("<td>").append(escapeHtml(safe(cart.getVariant()))).append("</td>")
              .append("<td style='text-align:center'>").append(q).append("</td>")
              .append("<td style='text-align:right'>").append(cf.format(line)).append("</td>")
              .append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb;
    }

    private String getAdminTemplate() {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Resumen interno – Pedido {{orderId}}</title>
              <style>
                body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color:#222; font-size:14px; line-height:1.6; background:#f4f4f4; margin:0; padding:0; }
                .email-wrapper { background:#f4f4f4; padding:20px 0; }
                .container { max-width: 820px; margin:0 auto; background:#ffffff; padding:0; border-radius:8px; box-shadow:0 2px 4px rgba(0,0,0,0.08); overflow:hidden; }
                .header { background:#ffffff; padding:24px 32px 12px; text-align:center; border-bottom:1px solid #e6e6e6; }
                .header img { max-height:60px; display:block; margin:0 auto 8px; }
                .brand { font-size:24px; font-weight:700; margin:0; letter-spacing:.6px; color:#ff5722; }
                .admin-note { font-size:12px; color:#666; margin:4px 0 0; }
                .order-info { background:#f8f9fa; border-left:4px solid #ff5722; padding:14px 18px; margin:20px 32px 0; font-size:13px; }
                .order-info p { margin:4px 0; }
                h2 { font-size:16px; margin:28px 32px 12px; font-weight:600; color:#333; border-bottom:2px solid #e0e0e0; padding-bottom:6px; }
                table.items { width:100%; border-collapse:collapse; margin:0 32px 24px; font-size:12px; }
                table.items thead th { background:#222; color:#fff; padding:6px 8px; text-align:left; font-weight:600; letter-spacing:.4px; }
                table.items tbody td { border:1px solid #e2e2e2; padding:6px 8px; vertical-align:top; }
                table.items tbody tr:nth-child(even) { background:#fafafa; }
                .totals { margin:0 32px 24px; }
                .totals table { width:320px; border-collapse:collapse; font-size:13px; }
                .totals td { padding:6px 8px; }
                .totals .label { color:#555; }
                .totals .value { text-align:right; font-weight:600; }
                .totals .grand .label { font-size:14px; font-weight:600; }
                .totals .grand .value { font-size:16px; font-weight:700; color:#ff5722; }
                .shipping-info { background:#f8f9fa; margin:0 32px 32px; border:1px solid #e6e6e6; padding:14px 18px; border-radius:6px; font-size:12.5px; line-height:1.5; }
                .footer { background:#f8f9fa; padding:22px 32px; text-align:center; font-size:11px; color:#666; border-top:1px solid #e0e0e0; }
                a { color:#ff5722; text-decoration:none; }
                a:hover { text-decoration:underline; }
                @media (max-width:680px){ .container{ width:100%; } table.items{ margin:0 16px 24px;} .order-info, .shipping-info, .totals{ margin:0 16px 24px;} h2{ margin:24px 16px 10px;} }
              </style>
            </head>
            <body>
              <div class="email-wrapper">
                <div class="container">
                  <div class="header">
                    <img src="{{storeLogoUrl}}" alt="Logo {{storeName}}" />
                    <p class="brand">{{storeName}}</p>
                    <p class="admin-note">Resumen interno | Pedido #{{orderId}} | Cliente: {{customerName}} ({{customerEmail}})</p>
                  </div>
                  <div class="order-info">
                    <p><strong>Pedido:</strong> #{{orderId}}</p>
                    <p><strong>Fecha:</strong> {{orderDate}}</p>
                    <p><strong>Subtotal calculado:</strong> {{subtotal}}</p>
                    <p><strong>Envío acumulado:</strong> {{shippingTotal}}</p>
                    <p><strong>Total:</strong> {{total}}</p>
                  </div>
                  <h2>Listado de productos (incluye ID, External ID, Base Price, Días estimados)</h2>
                  {{itemsTable}}
                  <div class="totals">
                    <table>
                      <tr><td class="label">Subtotal:</td><td class="value">{{subtotal}}</td></tr>
                      <tr><td class="label">Envío acumulado:</td><td class="value">{{shippingTotal}}</td></tr>
                      <tr class="grand"><td class="label">TOTAL:</td><td class="value">{{total}}</td></tr>
                    </table>
                  </div>
                  <div class="shipping-info">
                    <strong style="display:block; margin-bottom:6px;">Dirección de envío</strong>
                    {{shippingAddress}}
                  </div>
                  <div class="footer">
                    Uso interno administrativo · <a href="{{storeUrl}}" target="_blank" rel="noopener">{{storeUrl}}</a><br>
                    Soporte: {{supportEmail}} | Tel: {{supportPhone}}
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

    private String buildShippingAddress(Checkout checkout) {
        StringBuilder sb = new StringBuilder();
        if (checkout != null) {
            if (checkout.getAddress() != null) sb.append(escapeHtml(safe(checkout.getAddress()))).append("<br>");
            if (checkout.getPostalCode() != null || checkout.getCity() != null) {
                sb.append(escapeHtml(safe(checkout.getPostalCode()))).append(" ")
                  .append(escapeHtml(safe(checkout.getCity()))).append("<br>");
            }
            if (checkout.getCountry() != null) sb.append(escapeHtml(safe(checkout.getCountry())));
            if (checkout.getPhoneNumber() != null) sb.append("<br>Tel: ").append(escapeHtml(safe(checkout.getPhoneNumber())));
        }
        return sb.toString();
    }
}
