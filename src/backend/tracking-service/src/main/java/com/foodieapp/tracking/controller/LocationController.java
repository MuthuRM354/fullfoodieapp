package com.foodieapp.tracking.controller;
import com.foodieapp.tracking.service.LocationService;
import com.foodieapp.tracking.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/location") @CrossOrigin("*") @RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    private final RouteService routeService;
    @GetMapping("/history/{orderId}") public ResponseEntity<?> getHistory(@PathVariable Long orderId) {
        return ResponseEntity.ok(Map.of("success", true, "data", locationService.getHistory(orderId)));
    }
    @GetMapping("/route") public ResponseEntity<?> calculateRoute(
            @RequestParam Double fromLat, @RequestParam Double fromLng,
            @RequestParam Double toLat, @RequestParam Double toLng) {
        return ResponseEntity.ok(Map.of("success", true, "data", routeService.calculateRoute(fromLat, fromLng, toLat, toLng)));
    }
}
