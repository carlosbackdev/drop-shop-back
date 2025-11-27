package com.motogear.dropshopback.shop.payment.service;

import com.motogear.dropshopback.config.StripeProperties;
import com.motogear.dropshopback.shop.cart.repository.CartRepository;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.repository.ProductRepository;
import com.motogear.dropshopback.shop.order.dto.OrderResponse;
import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import com.motogear.dropshopback.shop.shaded.repository.CartShadedRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentService {

    private final StripeProperties stripeProperties;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartShadedRepository cartShadedRepository;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripeProperties.getApiKey();
    }

    public String createCheckoutSession(OrderResponse orderResponse) {
        if (orderResponse.getTotal() == null || orderResponse.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto de la orden debe ser mayor a 0");
        }

        if (orderResponse.getCartShadedIds() == null || orderResponse.getCartShadedIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden debe contener al menos un producto");
        }

        try {
            // Obtener los items del carrito
            List<CartShaded> cartItems = cartShadedRepository.findAllById(orderResponse.getCartShadedIds());

            if (cartItems.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontraron productos en el carrito");
            }

            String successUrl = stripeProperties.getCheckoutSuccessUrl();
            String cancelUrl = stripeProperties.getCheckoutCancelUrl();
            cancelUrl+="/" + orderResponse.getId();

            // Construir los line items
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .putMetadata("orderId", orderResponse.getId().toString())
                    .putMetadata("userId", orderResponse.getUserId().toString());

            // Agregar cada producto como line item
            for (CartShaded cartItem : cartItems) {
                Product product = productRepository.findById(cartItem.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Producto no encontrado: " + cartItem.getProductId()));

                paramsBuilder.addLineItem(buildLineItem(product, cartItem));
            }

            // Agregar gastos de envío si el total es menor a 50€
            if (orderResponse.getTotal().compareTo(new BigDecimal("50")) < 0) {
                paramsBuilder.addLineItem(buildShippingLineItem());
            }

            SessionCreateParams params = paramsBuilder.build();
            Session session = Session.create(params);

            log.info("Sesión de Stripe creada exitosamente para orden #{}", orderResponse.getId());
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Error creando sesión de Stripe para orden #{}: {}", orderResponse.getId(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo iniciar el pago con Stripe");
        }
    }

    private SessionCreateParams.LineItem buildLineItem(Product product, CartShaded cartItem) {
        // Usar el precio de venta, o base si no hay precio de venta
        BigDecimal price = product.getSellPrice() != null ? product.getSellPrice() : product.getBasePrice();
        if (price == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El producto no tiene un precio válido: " + product.getId());
        }
        // Convertir el precio a céntimos
        long priceInCents = price.movePointRight(2).longValue();

        // Construir el nombre del producto (incluyendo variante si existe)
        String productName = product.getName();

        // Construir descripción del producto (usar details)
        String description = "";
        if (cartItem.getVariant() != null) {
            description = "Opcion: " + cartItem.getVariant();
        }else {
            description="Detalles: ";
            description=product.getDetails()!=null ? product.getDetails() : "sin detalles";
        }

        var productDataBuilder = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productName);
        if (!description.isBlank()) {
            productDataBuilder.setDescription(description);
        }

        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) cartItem.getQuantity())
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(priceInCents)
                                .setProductData(productDataBuilder.build())
                                .build()
                )
                .build();
    }

    private SessionCreateParams.LineItem buildShippingLineItem() {
        BigDecimal shippingCost = new BigDecimal("1.99");
        long shippingCostInCents = shippingCost.movePointRight(2).longValue();

        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(shippingCostInCents)
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Gastos de envío")
                                                .setDescription("Envío estándar")
                                                .build()
                                )
                                .build()
                )
                .build();
    }
    public String getMeatdata(String sessionId, String key) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return session.getMetadata().get(key);

    }
}
