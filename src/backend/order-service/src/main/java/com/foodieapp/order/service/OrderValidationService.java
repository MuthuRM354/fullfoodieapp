package com.foodieapp.order.service;

import com.foodieapp.order.model.Cart;
import org.springframework.stereotype.Service;

@Service
public class OrderValidationService {

    public void validate(Cart cart, String deliveryAddress) {
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new RuntimeException("Delivery address is required");
        }
    }
}
