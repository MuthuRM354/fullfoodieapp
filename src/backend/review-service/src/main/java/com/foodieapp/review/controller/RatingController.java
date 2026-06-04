package com.foodieapp.review.controller;
import com.foodieapp.review.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/reviews") @CrossOrigin("*") @RequiredArgsConstructor
public class RatingController {
    private final RatingService service;
    @GetMapping("/restaurant/{restaurantId}/summary") public ResponseEntity<?> getSummary(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getSummary(restaurantId)));
    }
}
