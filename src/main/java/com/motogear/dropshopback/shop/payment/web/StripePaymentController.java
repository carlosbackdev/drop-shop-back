package com.motogear.dropshopback.shop.payment.web;

import com.motogear.dropshopback.common.messages.service.OrderStatusMessageService;
import com.motogear.dropshopback.shop.order.dto.OrderResponse;
import com.motogear.dropshopback.shop.payment.service.StripePaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Integración con Stripe Checkout")
@Slf4j
public class StripePaymentController {

    private final StripePaymentService stripePaymentService;
    private final OrderStatusMessageService orderStatusMessageService;
    @Value("${front.url}")
    private String frontendUrl;

    @PostMapping("/create-checkout-session")
    @Operation(
        summary = "Crear sesión de Stripe Checkout",
        description = "Recibe una orden completa y crea una sesión de pago en Stripe con todos los productos detallados. Devuelve la URL de redirección."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@Valid @RequestBody OrderResponse orderResponse) {
        System.out.println("Received order for checkout session: " + orderResponse);
        String url = stripePaymentService.createCheckoutSession(orderResponse);
        return ResponseEntity.ok(Map.of("url", url));
    }
    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam("session_id") String sessionId) {
        log.info("Procesando pago exitoso para sessionId: {}", sessionId);
        try {
            String metadata = stripePaymentService.getMeatdata(sessionId, "orderId");
            Long orderId=Long.parseLong(metadata);
            //guardar el cambio de estado de la orden y enviar notificaciones
            orderStatusMessageService.handleOrderPaid(orderId);

            String successUrl = frontendUrl + "/success?orderId=" + orderId;

            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create(successUrl))
                    .build();        }
        catch (StripeException e) {
            log.error("Error recuperando sesión de Stripe {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("http://localhost:8081/error"))
                    .build();
        } catch (NumberFormatException e) {
            log.error("orderId inválido", e);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("http://localhost:8081/error"))
                    .build();
        }
    }


    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity.ok("❌ Pago cancelado. Puedes intentar nuevamente.");
    }
}
