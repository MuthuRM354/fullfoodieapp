package com.foodieapp.admin.controller;
import com.foodieapp.admin.model.AdminWorkflow;
import com.foodieapp.admin.service.AdminWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/admin/workflows") @CrossOrigin("*") @RequiredArgsConstructor
public class AdminWorkflowController {
    private final AdminWorkflowService service;
    @GetMapping public ResponseEntity<?> getAll() { return ResponseEntity.ok(Map.of("success", true, "data", service.getAll())); }
    @GetMapping("/pending") public ResponseEntity<?> getPending() { return ResponseEntity.ok(Map.of("success", true, "data", service.getPending())); }
    @GetMapping("/{id}") public ResponseEntity<?> get(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.getById(id))); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }
    @PostMapping public ResponseEntity<?> create(@RequestBody AdminWorkflow w) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", service.create(w))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @PutMapping("/{id}/approve") public ResponseEntity<?> approve(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.approve(id))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @PutMapping("/{id}/reject") public ResponseEntity<?> reject(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.reject(id))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
}
