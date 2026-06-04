package com.foodieapp.payment.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class BharatPeGateway {

    public Map<String, Object> initiatePayment(Long orderId, BigDecimal amount, String reference) {
        log.info("BharatPeGateway: Initiating payment for order {} amount {}", orderId, amount);
        return Map.of(
            "gateway", "BharatPeGateway",
            "transactionId", UUID.randomUUID().toString(),
            "status", "SUCCESS",
            "orderId", orderId,
            "amount", amount
        );
    }

    public Map<String, Object> verifyPayment(String transactionId) {
        log.info("BharatPeGateway: Verifying payment {}", transactionId);
        return Map.of("transactionId", transactionId, "status", "SUCCESS", "verified", true);
    }

    public Map<String, Object> refundPayment(String transactionId, BigDecimal amount) {
        log.info("BharatPeGateway: Refunding {} for transaction {}", amount, transactionId);
        return Map.of("refundId", UUID.randomUUID().toString(), "status", "SUCCESS", "amount", amount);
    }
}
