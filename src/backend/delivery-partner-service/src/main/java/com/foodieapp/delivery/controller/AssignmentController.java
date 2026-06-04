package com.foodieapp.delivery.controller;
import com.foodieapp.delivery.model.Assignment;
import com.foodieapp.delivery.model.AssignmentStatus;
import com.foodieapp.delivery.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/delivery/assignments") @CrossOrigin("*") @RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService service;
    @PostMapping public ResponseEntity<?> create(@RequestBody Assignment a) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", service.create(a))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @GetMapping("/{id}") public ResponseEntity<?> get(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.getById(id))); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }
    @PutMapping("/{id}/status") public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            AssignmentStatus status = AssignmentStatus.valueOf(body.get("status"));
            return ResponseEntity.ok(Map.of("success", true, "data", service.updateStatus(id, status)));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @GetMapping("/partner/{partnerId}") public ResponseEntity<?> getByPartner(@PathVariable Long partnerId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getByPartner(partnerId)));
    }
}
