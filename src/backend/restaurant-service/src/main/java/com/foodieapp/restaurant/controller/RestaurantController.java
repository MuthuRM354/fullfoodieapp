package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.security.AuthUtil;
import com.foodieapp.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<?> getRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String search) {
        List<Restaurant> restaurants = restaurantService.searchRestaurants(city, cuisine, search);
        return ResponseEntity.ok(Map.of("success", true, "data", restaurants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurant(@PathVariable Long id) {
        try {
            Restaurant r = restaurantService.getRestaurantById(id);
            return ResponseEntity.ok(Map.of("success", true, "data", r));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody Restaurant restaurant) {
        try {
            // Owner is always the authenticated caller — never trust a
            // client-supplied ownerId, or anyone could create a restaurant
            // "owned" by someone else.
            Long callerId = AuthUtil.currentUserId();
            if (callerId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Not authenticated"));
            }
            restaurant.setOwnerId(callerId);
            Restaurant created = restaurantService.createRestaurant(restaurant);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Restaurant created", "data", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        try {
            Restaurant existing = restaurantService.getRestaurantById(id);
            if (!AuthUtil.canManage(existing.getOwnerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "You don't own this restaurant"));
            }
            Restaurant updated = restaurantService.updateRestaurant(id, restaurant);
            return ResponseEntity.ok(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id) {
        try {
            Restaurant existing = restaurantService.getRestaurantById(id);
            if (!AuthUtil.canManage(existing.getOwnerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "You don't own this restaurant"));
            }
            restaurantService.deleteRestaurant(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Restaurant deactivated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getRestaurantsByOwner(@PathVariable Long ownerId) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByOwner(ownerId);
        return ResponseEntity.ok(Map.of("success", true, "data", restaurants));
    }

    /**
     * Called internally by review-service after a review is added/deleted.
     * PUT /api/restaurants/{id}/rating
     * Body: { "averageRating": 4.3, "totalReviews": 17 }
     */
    @PutMapping("/{id}/rating")
    public ResponseEntity<?> updateRating(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            double averageRating = Double.parseDouble(body.get("averageRating").toString());
            int    totalReviews  = Integer.parseInt(body.get("totalReviews").toString());
            Restaurant updated = restaurantService.updateRating(id, averageRating, totalReviews);
            return ResponseEntity.ok(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
