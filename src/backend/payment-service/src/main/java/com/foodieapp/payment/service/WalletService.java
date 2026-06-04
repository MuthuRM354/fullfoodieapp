package com.foodieapp.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class WalletService {

    public Map<String, Object> getWalletBalance(Long userId) {
        // Stub: return mock wallet balance
        log.info("Wallet balance requested for user: {}", userId);
        return Map.of(
            "userId", userId,
            "balance", new BigDecimal("250.00"),
            "currency", "INR"
        );
    }

    public Map<String, Object> debit(Long userId, BigDecimal amount) {
        log.info("Wallet debit: user={} amount={}", userId, amount);
        return Map.of("success", true, "deducted", amount, "userId", userId);
    }
}
