package com.foodieapp.payment.controller;

import com.foodieapp.payment.model.RefundRequest;
import com.foodieapp.payment.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<?> requestRefund(@PathVariable Long paymentId,
                                           @RequestBody Map<String, String> request) {
        try {
            String reason = request.getOrDefault("reason", "Customer requested");
            RefundRequest refund = refundService.requestRefund(paymentId, reason);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Refund processed", "data", refund));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/refunds/{refundId}")
    public ResponseEntity<?> getRefund(@PathVariable Long refundId) {
        try {
            RefundRequest refund = refundService.getRefund(refundId);
            return ResponseEntity.ok(Map.of("success", true, "data", refund));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
