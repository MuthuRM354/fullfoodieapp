package com.foodieapp.admin.controller;
import com.foodieapp.admin.service.AdminAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/admin/audit") @CrossOrigin("*") @RequiredArgsConstructor
public class AdminAuditController {
    private final AdminAuditService service;
    @GetMapping public ResponseEntity<?> getAll() { return ResponseEntity.ok(Map.of("success", true, "data", service.getAll())); }
    @GetMapping("/{adminId}") public ResponseEntity<?> getByAdmin(@PathVariable Long adminId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getByAdmin(adminId)));
    }
}
