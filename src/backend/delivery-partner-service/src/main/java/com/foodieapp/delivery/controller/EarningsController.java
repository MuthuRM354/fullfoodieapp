package com.foodieapp.delivery.controller;
import com.foodieapp.delivery.service.EarningsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/delivery/partners") @CrossOrigin("*") @RequiredArgsConstructor
public class EarningsController {
    private final EarningsService service;
    @GetMapping("/{id}/earnings") public ResponseEntity<?> getEarnings(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getByPartner(id)));
    }
    @GetMapping("/{id}/earnings/summary") public ResponseEntity<?> getSummary(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getSummary(id)));
    }
}
