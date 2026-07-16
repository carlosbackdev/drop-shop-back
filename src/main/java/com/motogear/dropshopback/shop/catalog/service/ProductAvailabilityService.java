package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.domain.ProductStatus;
import com.motogear.dropshopback.shop.catalog.repository.ProductRepository;
import com.motogear.dropshopback.shop.shaded.domain.CartShaded;
import com.motogear.dropshopback.shop.shaded.repository.CartShadedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductAvailabilityService {

    private final ProductRepository productRepository;
    private final CartShadedRepository cartShadedRepository;

    @Transactional(readOnly = true)
    public Product requirePurchasable(Long productId, int requestedQuantity) {
        if (requestedQuantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser al menos 1");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        validateAvailableStock(product, requestedQuantity);
        return product;
    }

    @Transactional(readOnly = true)
    public void validateCartItems(List<CartShaded> items) {
        Map<Long, Integer> requestedByProduct = quantitiesByProduct(items);
        for (Map.Entry<Long, Integer> entry : requestedByProduct.entrySet()) {
            requirePurchasable(entry.getKey(), entry.getValue());
        }
    }

    @Transactional
    public void consumeStockForCartItems(List<Long> cartItemIds) {
        List<CartShaded> items = cartShadedRepository.findAllById(cartItemIds);
        if (items.size() != cartItemIds.size()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden contiene productos que ya no existen");
        }

        Map<Long, Integer> requestedByProduct = quantitiesByProduct(items);

        for (Map.Entry<Long, Integer> entry : requestedByProduct.entrySet()) {
            Product product = productRepository.findByIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
            validateAvailableStock(product, entry.getValue());

            int remaining = product.getStockQuantity() - entry.getValue();
            product.setStockQuantity(remaining);
            if (remaining == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }
            productRepository.save(product);
        }
    }

    private Map<Long, Integer> quantitiesByProduct(List<CartShaded> items) {
        Map<Long, Integer> requestedByProduct = new LinkedHashMap<>();
        for (CartShaded item : items) {
            if (item.getQuantity() == null || item.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser al menos 1");
            }
            requestedByProduct.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        return requestedByProduct;
    }

    private void validateAvailableStock(Product product, int requestedQuantity) {
        if (!product.isPurchasable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El producto no está disponible para compra");
        }
        if (product.getStockQuantity() < requestedQuantity) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente. Unidades disponibles: " + product.getStockQuantity()
            );
        }
    }
}
