package com.foodieapp.payment.service;

import com.foodieapp.payment.model.*;
import com.foodieapp.payment.repository.RefundRepository;
import com.foodieapp.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final RefundRepository refundRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public RefundRequest requestRefund(Long paymentId, String reason) {
        Transaction transaction = transactionRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        RefundRequest refund = RefundRequest.builder()
                .paymentId(paymentId)
                .orderId(transaction.getOrderId())
                .amount(transaction.getAmount())
                .reason(reason)
                .status(RefundStatus.PENDING)
                .build();

        refund = refundRepository.save(refund);
        log.info("Refund requested for payment: {}", paymentId);

        // Auto-approve simulation — both saves must be atomic
        refund.setStatus(RefundStatus.APPROVED);
        transaction.setStatus(PaymentStatus.REFUNDED);
        transactionRepository.save(transaction);
        return refundRepository.save(refund);
    }

    public RefundRequest getRefund(Long refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found: " + refundId));
    }
}
