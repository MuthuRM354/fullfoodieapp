package com.foodieapp.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class UPIService {

    public Map<String, Object> verifyUPI(String upiId, String transactionRef) {
        log.info("UPI verification for: {} ref: {}", upiId, transactionRef);
        // Simulate UPI verification
        return Map.of(
            "verified", true,
            "upiId", upiId,
            "transactionRef", transactionRef != null ? transactionRef : UUID.randomUUID().toString(),
            "status", "SUCCESS"
        );
    }
}
