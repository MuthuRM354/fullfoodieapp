package com.foodieapp.payment.controller;

import com.foodieapp.payment.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin("*")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        Map<String, Object> balance = walletService.getWalletBalance(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", balance));
    }
}
