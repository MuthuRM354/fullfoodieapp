package com.foodieapp.payment.controller;

import com.foodieapp.payment.model.PaymentMethod;
import com.foodieapp.payment.model.PaymentStatus;
import com.foodieapp.payment.model.Transaction;
import com.foodieapp.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.parseLong(request.get("orderId").toString());
            Long userId = Long.parseLong(request.get("userId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            PaymentMethod method = PaymentMethod.valueOf(
                request.getOrDefault("paymentMethod", "CASH").toString());

            Transaction txn = paymentService.initiatePayment(orderId, userId, amount, method);
            // Immediately route through the (simulated) gateway rather than
            // deferring confirmation to a separate webhook/callback step.
            txn = paymentService.confirmPayment(txn.getId());

            boolean success = txn.getStatus() == PaymentStatus.SUCCESS;
            String message = success
                    ? "Payment successful"
                    : "Payment failed" + (txn.getFailureReason() != null ? ": " + txn.getFailureReason() : "");

            return ResponseEntity.status(success ? HttpStatus.CREATED : HttpStatus.PAYMENT_REQUIRED)
                    .body(Map.of("success", success, "message", message, "data", txn));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPayment(@PathVariable Long paymentId) {
        try {
            Transaction txn = paymentService.getPayment(paymentId);
            return ResponseEntity.ok(Map.of("success", true, "data", txn));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrder(@PathVariable Long orderId) {
        try {
            Transaction txn = paymentService.getPaymentByOrder(orderId);
            return ResponseEntity.ok(Map.of("success", true, "data", txn));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long paymentId) {
        try {
            Transaction txn = paymentService.confirmPayment(paymentId);
            return ResponseEntity.ok(Map.of("success", true, "data", txn));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
