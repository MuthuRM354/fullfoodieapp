package com.foodieapp.delivery.controller;
import com.foodieapp.delivery.model.DeliveryPartner;
import com.foodieapp.delivery.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/delivery/partners") @CrossOrigin("*") @RequiredArgsConstructor
public class DeliveryPartnerController {
    private final DeliveryPartnerService service;
    @PostMapping public ResponseEntity<?> create(@RequestBody DeliveryPartner dp) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", service.create(dp))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @GetMapping("/{id}") public ResponseEntity<?> get(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.getById(id))); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }
    @PutMapping("/{id}") public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DeliveryPartner dp) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.update(id, dp))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @GetMapping("/available") public ResponseEntity<?> getAvailable() {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getAvailable()));
    }
    // Lookup by auth user ID (used by frontend which only knows the logged-in user's account ID)
    @GetMapping("/user/{userId}") public ResponseEntity<?> getByUserId(@PathVariable Long userId) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.getByUserId(userId))); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }
    @PutMapping("/{id}/availability") public ResponseEntity<?> setAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.setAvailability(id, body.get("available")))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
}
