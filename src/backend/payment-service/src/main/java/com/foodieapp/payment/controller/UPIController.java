package com.foodieapp.payment.controller;

import com.foodieapp.payment.service.UPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/upi")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UPIController {

    private final UPIService upiService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUPI(@RequestBody Map<String, String> request) {
        try {
            String upiId = request.get("upiId");
            String txnRef = request.get("transactionRef");
            Map<String, Object> result = upiService.verifyUPI(upiId, txnRef);
            return ResponseEntity.ok(Map.of("success", true, "data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
