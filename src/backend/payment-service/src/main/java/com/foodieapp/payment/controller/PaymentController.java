package com.foodieapp.payment.controller;

import com.foodieapp.payment.model.PaymentMethod;
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
            // Auto-confirm for simulation
            txn = paymentService.confirmPayment(txn.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Payment successful", "data", txn));
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
