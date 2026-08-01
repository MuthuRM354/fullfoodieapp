package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.security.AuthUtil;
import com.foodieapp.restaurant.service.MenuService;
import com.foodieapp.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<?> getMenu(@PathVariable Long restaurantId) {
        List<MenuItem> items = menuService.getMenuByRestaurant(restaurantId);
        return ResponseEntity.ok(Map.of("success", true, "data", items));
    }

    @PostMapping
    public ResponseEntity<?> addMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItem item) {
        try {
            if (!AuthUtil.canManage(restaurantService.getRestaurantById(restaurantId).getOwnerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "You don't own this restaurant"));
            }
            MenuItem created = menuService.addMenuItem(restaurantId, item);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Menu item added", "data", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long restaurantId, @PathVariable Long itemId,
                                             @RequestBody MenuItem item) {
        try {
            if (!AuthUtil.canManage(restaurantService.getRestaurantById(restaurantId).getOwnerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "You don't own this restaurant"));
            }
            MenuItem updated = menuService.updateMenuItem(restaurantId, itemId, item);
            return ResponseEntity.ok(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long restaurantId, @PathVariable Long itemId) {
        try {
            if (!AuthUtil.canManage(restaurantService.getRestaurantById(restaurantId).getOwnerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "You don't own this restaurant"));
            }
            menuService.deleteMenuItem(restaurantId, itemId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Menu item deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
