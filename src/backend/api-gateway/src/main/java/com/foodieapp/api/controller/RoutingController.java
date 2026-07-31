package com.foodieapp.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RoutingController {

    private final Map<String, String> serviceRoutes;

    @GetMapping("/gateway/routes")
    public ResponseEntity<?> getRoutes() {
        return ResponseEntity.ok(Map.of("success", true, "routes", serviceRoutes));
    }

    @GetMapping("/gateway/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "api-gateway"));
    }
}
