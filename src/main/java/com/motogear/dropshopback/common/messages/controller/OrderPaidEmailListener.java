package com.motogear.dropshopback.common.messages.controller;

import com.motogear.dropshopback.common.messages.event.FormatterEmail;
import com.motogear.dropshopback.common.messages.event.FormatterEmailAdmin;
import com.motogear.dropshopback.common.messages.event.FormatterEmailShipped;
import com.motogear.dropshopback.common.messages.event.OrderEvent;
import com.motogear.dropshopback.common.messages.mail.MailServiceImpl;
import com.motogear.dropshopback.common.messages.service.DataMailService;
import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.checkout.domain.Checkout;
import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import com.motogear.dropshopback.shop.order.domain.Tracking;
import com.motogear.dropshopback.shop.order.service.TrackService;
import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import com.motogear.dropshopback.users.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderPaidEmailListener {

    private final MailServiceImpl mailService;
    private final DataMailService dataMailService;
    private final FormatterEmail formatEmailService;
    private final FormatterEmailShipped formatEmailShippedService;
    private final TrackService trackService;
    private final FormatterEmailAdmin formatEmailAdminService;

    @Value("${spring.mail.useradmin}")
    private String fromEmail;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPaidEvent(OrderEvent event) {
        if (event.getStatusType() != (OrderStatus.PAID)) {
            return;
        }
        Long orderId = event.getOrderId();
        Order order = dataMailService.getOrder(orderId);
        User user = dataMailService.getUser(order.getUserId());
        Checkout checkout = dataMailService.getCheckout(order);
        Map<CartShaded, Product> cartProductMap = dataMailService.getCartShadedAndProduct(order);


        String body = formatEmailService.buildBodyMail(user, order, checkout, cartProductMap);
        String bodyAdmin = formatEmailAdminService.buildBodyMail(user, order, checkout, cartProductMap);
        String subject = "Confirmación de pago – Pedido #" + orderId;

        mailService.sendEmail(fromEmail, subject, bodyAdmin);
        mailService.sendEmail(user.getEmail(), subject, body);
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderShippedEvent(OrderEvent event) {
        if (event.getStatusType() != OrderStatus.SHIPPED) {
            return;
        }
        Long orderId = event.getOrderId();
        Order order = dataMailService.getOrder(orderId);
        User user = dataMailService.getUser(order.getUserId());
        Checkout checkout = dataMailService.getCheckout(order);
        Tracking tracking = null;
        try {
            tracking = trackService.getTrackingByOrderId(orderId);
        } catch (Exception e) {
            // Si no existe tracking aún, se envía el correo sin datos detallados
        }

        String body = formatEmailShippedService.buildShippedEmailBody(user, order, checkout, tracking);
        String subject = "Tu pedido #" + orderId + " ha sido enviado";

        mailService.sendEmail(fromEmail, subject, body);
        mailService.sendEmail(user.getEmail(), subject, body);
    }

}
