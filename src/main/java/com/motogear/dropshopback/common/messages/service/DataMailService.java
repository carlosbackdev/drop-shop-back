package com.motogear.dropshopback.common.messages.service;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.service.ProductService;
import com.motogear.dropshopback.shop.checkout.domain.Checkout;
import com.motogear.dropshopback.shop.checkout.service.CheckoutService;
import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.order.service.OrderService;
import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import com.motogear.dropshopback.shop.shaded.service.CartShadedService;
import com.motogear.dropshopback.users.domain.User;
import com.motogear.dropshopback.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataMailService {

    private final OrderService orderService;
    private final CheckoutService checkoutService;
    private final CartShadedService cartShadedService;
    private final ProductService productService;
    private final UserService userService;

    @Transactional
    public User getUser(long id) {
        return userService.findById(id);
    }

    @Transactional
    public Order getOrder(long orderId) {
        return orderService.getOrderById(orderId);
    }

    @Transactional
    public Checkout getCheckout(Order order) {
       return checkoutService.getCheckoutById(order.getCheckoutId());
    }

    @Transactional
    public Map<CartShaded, Product> getCartShadedAndProduct(Order order) {
        Map<CartShaded, Product> cartProductMap = new HashMap<>();
        for (Long cartId : order.getCartShadedIds()) {
            CartShaded cart = cartShadedService.getCartItemById(cartId);
            Product product = productService.findProductById(cart.getProductId());
            cartProductMap.put(cart, product);
        }
        return cartProductMap;
    }
}
