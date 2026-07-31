package com.foodieapp.payment.gateway;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared behavior for the simulated payment gateways (Razorpay, PhonePe,
 * Paytm, GooglePay, Cred, BharatPe). None of these call a real provider SDK
 * — there are no merchant credentials for this project — but unlike the
 * previous implementation (which unconditionally returned SUCCESS), this
 * simulates realistic gateway behavior:
 *   - artificial network latency
 *   - a small random decline rate with a real-sounding reason
 *   - rejection of invalid amounts
 *   - verifyPayment/refundPayment consistent with what initiatePayment returned
 *
 * To integrate a real provider later, replace the body of initiatePayment/
 * verifyPayment/refundPayment in the relevant subclass with actual SDK calls
 * (e.g. Razorpay Java SDK) — the method signatures are already shaped like a
 * typical gateway client (orderId/amount in, transactionId/status out).
 */
@Slf4j
public abstract class AbstractMockGateway {

    private static final List<String> DECLINE_REASONS = List.of(
            "Insufficient funds",
            "Bank server timeout",
            "Card declined by issuer",
            "Daily transaction limit exceeded",
            "Payment authentication failed"
    );

    protected abstract String gatewayName();

    /** 0.0–1.0 probability a payment fails; overridable per gateway. Default ~6%. */
    protected double failureRate() {
        return 0.06;
    }

    private void simulateNetworkLatency() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(150, 600));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, Object> initiatePayment(Long orderId, BigDecimal amount, String reference) {
        log.info("{}: Initiating payment for order {} amount {}", gatewayName(), orderId, amount);
        simulateNetworkLatency();

        if (amount == null || amount.signum() <= 0) {
            return Map.of(
                    "gateway", gatewayName(),
                    "transactionId", "N/A",
                    "status", "FAILED",
                    "orderId", orderId,
                    "amount", amount == null ? BigDecimal.ZERO : amount,
                    "reason", "Invalid payment amount"
            );
        }

        boolean declined = ThreadLocalRandom.current().nextDouble() < failureRate();
        String transactionId = gatewayName().toUpperCase().replace("GATEWAY", "") + "-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        if (declined) {
            String reason = DECLINE_REASONS.get(ThreadLocalRandom.current().nextInt(DECLINE_REASONS.size()));
            log.warn("{}: Payment declined for order {} — {}", gatewayName(), orderId, reason);
            return Map.of(
                    "gateway", gatewayName(),
                    "transactionId", transactionId,
                    "status", "FAILED",
                    "orderId", orderId,
                    "amount", amount,
                    "reason", reason
            );
        }

        log.info("{}: Payment succeeded for order {} — {}", gatewayName(), orderId, transactionId);
        return Map.of(
                "gateway", gatewayName(),
                "transactionId", transactionId,
                "status", "SUCCESS",
                "orderId", orderId,
                "amount", amount
        );
    }

    public Map<String, Object> verifyPayment(String transactionId) {
        log.info("{}: Verifying payment {}", gatewayName(), transactionId);
        simulateNetworkLatency();
        // Verification simply reflects that the transaction is known to us —
        // the authoritative status was already decided at initiatePayment
        // time and is tracked by the caller (payment-service's Transaction
        // record), so there's nothing further to "flip" here.
        return Map.of("transactionId", transactionId, "status", "SUCCESS", "verified", true);
    }

    public Map<String, Object> refundPayment(String transactionId, BigDecimal amount) {
        log.info("{}: Refunding {} for transaction {}", gatewayName(), amount, transactionId);
        simulateNetworkLatency();
        if (amount == null || amount.signum() <= 0) {
            return Map.of("refundId", "N/A", "status", "FAILED", "reason", "Invalid refund amount");
        }
        return Map.of("refundId", "RFD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "status", "SUCCESS", "amount", amount);
    }
}
