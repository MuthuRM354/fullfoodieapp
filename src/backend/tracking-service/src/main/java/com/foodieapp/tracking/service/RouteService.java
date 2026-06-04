package com.foodieapp.tracking.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
@Service @Slf4j
public class RouteService {
    public Map<String, Object> calculateRoute(Double fromLat, Double fromLng, Double toLat, Double toLng) {
        // Stub: calculate approximate distance
        double distKm = Math.sqrt(Math.pow(toLat - fromLat, 2) + Math.pow(toLng - fromLng, 2)) * 111;
        int timeMinutes = (int) (distKm * 3); // ~20 km/h average
        return Map.of("distanceKm", Math.round(distKm * 10.0) / 10.0, "estimatedMinutes", timeMinutes);
    }
}
