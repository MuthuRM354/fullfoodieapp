package com.foodieapp.payment.service;

import com.foodieapp.payment.gateway.AbstractMockGateway;
import com.foodieapp.payment.gateway.GatewayRouter;
import com.foodieapp.payment.model.*;
import com.foodieapp.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final GatewayRouter gatewayRouter;

    @Transactional
    public Transaction initiatePayment(Long orderId, Long userId, BigDecimal amount, PaymentMethod method) {
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Transaction transaction = Transaction.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .paymentMethod(method)
                .status(PaymentStatus.PENDING)
                .transactionId(txnId)
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Payment initiated: {} for order {} amount {}", txnId, orderId, amount);
        return transaction;
    }

    /**
     * Confirms a pending payment by routing it through the appropriate
     * simulated gateway (see GatewayRouter/AbstractMockGateway) for its
     * payment method. CASH (cash-on-delivery) skips the gateway entirely and
     * is always SUCCESS immediately, matching real COD flows. CARD/UPI/WALLET
     * go through a gateway call that can genuinely fail (declined, invalid
     * amount, etc.) — the transaction's final status reflects that outcome
     * rather than being hardcoded.
     */
    @Transactional
    public Transaction confirmPayment(Long paymentId) {
        Transaction transaction = transactionRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        AbstractMockGateway gateway = gatewayRouter.resolve(transaction.getPaymentMethod());

        if (gateway == null) {
            // CASH — settled on delivery, no gateway involved.
            transaction.setStatus(PaymentStatus.SUCCESS);
            transaction.setGatewayReference("COD-" + transaction.getTransactionId());
        } else {
            Map<String, Object> result = gateway.initiatePayment(
                    transaction.getOrderId(), transaction.getAmount(), transaction.getTransactionId());

            boolean success = "SUCCESS".equals(result.get("status"));
            transaction.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            transaction.setGatewayReference((String) result.get("transactionId"));
            if (!success) {
                transaction.setFailureReason((String) result.get("reason"));
            }
        }

        transaction = transactionRepository.save(transaction);
        log.info("Payment {}: {} ({})", transaction.getStatus(), transaction.getTransactionId(),
                transaction.getGatewayReference());
        return transaction;
    }

    public Transaction getPayment(Long paymentId) {
        return transactionRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }

    public Transaction getPaymentByOrder(Long orderId) {
        return transactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    public List<Transaction> getPaymentsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
}
