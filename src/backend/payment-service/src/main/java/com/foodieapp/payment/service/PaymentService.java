package com.foodieapp.payment.service;

import com.foodieapp.payment.model.*;
import com.foodieapp.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;

    public Transaction initiatePayment(Long orderId, Long userId, BigDecimal amount, PaymentMethod method) {
        // Simulate payment processing
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

    public Transaction confirmPayment(Long paymentId) {
        Transaction transaction = transactionRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        // Simulate success
        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setGatewayReference("GW-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        transaction = transactionRepository.save(transaction);
        log.info("Payment confirmed: {}", transaction.getTransactionId());
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
