package com.foodieapp.admin.controller;
import com.foodieapp.admin.model.Admin;
import com.foodieapp.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/admin/admins") @CrossOrigin("*") @RequiredArgsConstructor
public class AdminManagementController {
    private final AdminService service;
    @GetMapping public ResponseEntity<?> getAll() { return ResponseEntity.ok(Map.of("success", true, "data", service.getAll())); }
    @GetMapping("/{id}") public ResponseEntity<?> get(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.getById(id))); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }
    @PostMapping public ResponseEntity<?> create(@RequestBody Admin admin) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", service.create(admin))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @PutMapping("/{id}") public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Admin admin) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.update(id, admin))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @DeleteMapping("/{id}") public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.deactivate(id))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
}
