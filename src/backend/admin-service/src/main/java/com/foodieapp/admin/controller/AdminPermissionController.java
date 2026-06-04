package com.foodieapp.admin.controller;
import com.foodieapp.admin.service.AdminPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/admin/permissions") @CrossOrigin("*") @RequiredArgsConstructor
public class AdminPermissionController {
    private final AdminPermissionService service;
    @GetMapping("/{level}/{resource}") public ResponseEntity<?> getPermissions(@PathVariable String level, @PathVariable String resource) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getPermissions(resource, level)));
    }
    @GetMapping("/check") public ResponseEntity<?> checkPermission(
            @RequestParam String level, @RequestParam String resource, @RequestParam String action) {
        return ResponseEntity.ok(Map.of("success", true, "hasPermission", service.hasPermission(level, resource, action)));
    }
}
