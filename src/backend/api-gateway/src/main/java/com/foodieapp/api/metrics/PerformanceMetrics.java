package com.foodieapp.api.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/gateway/metrics")
@RequiredArgsConstructor
public class PerformanceMetrics {

    private final GatewayMetrics metrics;

    @GetMapping
    public ResponseEntity<?> getMetrics() {
        return ResponseEntity.ok(Map.of(
                "totalRequests", metrics.getTotalRequests().get(),
                "successfulRequests", metrics.getSuccessfulRequests().get(),
                "failedRequests", metrics.getFailedRequests().get()
        ));
    }
}
