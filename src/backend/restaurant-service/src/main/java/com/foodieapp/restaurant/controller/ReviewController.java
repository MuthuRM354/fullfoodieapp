package com.foodieapp.restaurant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reviews")
@CrossOrigin("*")
public class ReviewController {

    @GetMapping
    public ResponseEntity<?> getReviews(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(Map.of("success", true, "message", "See review-service for reviews", "data", Map.of()));
    }
}
