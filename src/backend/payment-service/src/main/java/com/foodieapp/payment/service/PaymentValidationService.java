package com.foodieapp.payment.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentValidationService {

    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid payment amount");
        }
        if (amount.compareTo(new BigDecimal("100000")) > 0) {
            throw new RuntimeException("Amount exceeds maximum limit");
        }
    }

    public void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new RuntimeException("Invalid order ID");
        }
    }
}
