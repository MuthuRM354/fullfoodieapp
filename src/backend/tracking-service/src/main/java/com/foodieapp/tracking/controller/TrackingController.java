package com.foodieapp.tracking.controller;
import com.foodieapp.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/tracking") @CrossOrigin("*") @RequiredArgsConstructor
public class TrackingController {
    private final TrackingService service;
    @GetMapping("/{orderId}") public ResponseEntity<?> getTracking(@PathVariable Long orderId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getTracking(orderId)));
    }
    @PostMapping("/update") public ResponseEntity<?> updateLocation(@RequestBody Map<String, Object> req) {
        try {
            Long orderId = Long.parseLong(req.get("orderId").toString());
            Long partnerId = req.containsKey("deliveryPartnerId") ? Long.parseLong(req.get("deliveryPartnerId").toString()) : null;
            Double lat = Double.parseDouble(req.get("latitude").toString());
            Double lng = Double.parseDouble(req.get("longitude").toString());
            String status = req.containsKey("status") ? req.get("status").toString() : null;
            return ResponseEntity.ok(Map.of("success", true, "data", service.updateTracking(orderId, partnerId, lat, lng, status)));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @GetMapping("/{orderId}/history") public ResponseEntity<?> getHistory(@PathVariable Long orderId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getHistory(orderId)));
    }
}
