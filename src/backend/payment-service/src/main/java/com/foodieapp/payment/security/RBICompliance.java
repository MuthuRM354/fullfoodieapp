package com.foodieapp.payment.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * RBI Compliance checks stub.
 * In production, implement actual RBI mandate validations.
 */
@Component
@Slf4j
public class RBICompliance {

    public boolean isTransactionAllowed(BigDecimal amount, String paymentMethod) {
        log.debug("RBI compliance check: amount={} method={}", amount, paymentMethod);
        // Stub: always allowed
        return true;
    }

    public boolean requiresTwoFactorAuth(BigDecimal amount) {
        return amount.compareTo(new BigDecimal("5000")) > 0;
    }
}
