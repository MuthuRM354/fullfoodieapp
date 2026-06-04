package com.foodieapp.review.controller;
import com.foodieapp.review.model.Review;
import com.foodieapp.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/reviews") @CrossOrigin("*") @RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;
    @PostMapping public ResponseEntity<?> add(@RequestBody Review review) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", service.addReview(review))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @GetMapping("/restaurant/{restaurantId}") public ResponseEntity<?> getByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getByRestaurant(restaurantId)));
    }
    @GetMapping("/user/{userId}") public ResponseEntity<?> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getByUser(userId)));
    }
    @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.delete(id); return ResponseEntity.ok(Map.of("success", true, "message", "Review deleted")); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
}
