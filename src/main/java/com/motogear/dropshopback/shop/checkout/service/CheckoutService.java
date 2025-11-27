package com.motogear.dropshopback.shop.checkout.service;

import com.motogear.dropshopback.shop.checkout.domain.Checkout;
import com.motogear.dropshopback.shop.checkout.dto.CheckoutRequest;
import com.motogear.dropshopback.shop.checkout.repository.CheckoutRepository;
import com.motogear.dropshopback.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Checkout> listMyCheckouts() {
        Long userId = userService.getCurrentUser().getId();
        return checkoutRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Checkout getCheckout(Long checkoutId) {
        Long userId = userService.getCurrentUser().getId();
        return checkoutRepository.findByIdAndUserId(checkoutId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkout no encontrado"));
    }

    @Transactional(readOnly = true)
    public Checkout getCheckoutById(Long checkoutId) {
        return checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkout no encontrado"));
    }

    @Transactional
    public Checkout createCheckout(CheckoutRequest request) {
        Long userId = userService.getCurrentUser().getId();

        Checkout checkout = Checkout.builder()
                .userId(userId)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .phoneNumber(request.getPhoneNumber())
                .build();

        return checkoutRepository.save(checkout);
    }

    @Transactional
    public Checkout updateCheckout(Long checkoutId, CheckoutRequest request) {
        Checkout checkout = getCheckout(checkoutId);

        checkout.setCustomerName(request.getCustomerName());
        checkout.setCustomerEmail(request.getCustomerEmail());
        checkout.setAddress(request.getAddress());
        checkout.setCity(request.getCity());
        checkout.setCountry(request.getCountry());
        checkout.setPostalCode(request.getPostalCode());
        checkout.setPhoneNumber(request.getPhoneNumber());

        return checkoutRepository.save(checkout);
    }

    @Transactional
    public void deleteCheckout(Long checkoutId) {
        Checkout checkout = getCheckout(checkoutId);
        checkoutRepository.delete(checkout);
    }
}

