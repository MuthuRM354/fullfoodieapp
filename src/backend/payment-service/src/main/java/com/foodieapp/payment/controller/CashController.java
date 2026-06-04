package com.foodieapp.payment.controller;

import com.foodieapp.payment.service.CashService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/cash")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmCash(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.parseLong(request.get("orderId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String collectedBy = request.getOrDefault("collectedBy", "DELIVERY_PARTNER").toString();
            Map<String, Object> result = cashService.confirmCashPayment(orderId, amount, collectedBy);
            return ResponseEntity.ok(Map.of("success", true, "data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
