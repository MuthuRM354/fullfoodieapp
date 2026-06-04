package com.foodieapp.order.controller;

import com.foodieapp.order.model.Cart;
import com.foodieapp.order.model.CartItem;
import com.foodieapp.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", cart));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<?> addItem(@PathVariable Long userId,
                                     @RequestBody Map<String, Object> request) {
        try {
            CartItem item = CartItem.builder()
                    .menuItemId(Long.parseLong(request.get("menuItemId").toString()))
                    .name(request.get("name").toString())
                    .price(new java.math.BigDecimal(request.get("price").toString()))
                    .quantity(Integer.parseInt(request.getOrDefault("quantity", 1).toString()))
                    .build();
            Long restaurantId = Long.parseLong(request.get("restaurantId").toString());
            String restaurantName = request.getOrDefault("restaurantName", "").toString();
            Cart cart = cartService.addItem(userId, item, restaurantId, restaurantName);
            return ResponseEntity.ok(Map.of("success", true, "data", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long userId, @PathVariable Long itemId,
                                        @RequestBody Map<String, Object> request) {
        try {
            int quantity = Integer.parseInt(request.get("quantity").toString());
            Cart cart = cartService.updateItem(userId, itemId, quantity);
            return ResponseEntity.ok(Map.of("success", true, "data", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long userId, @PathVariable Long itemId) {
        try {
            Cart cart = cartService.removeItem(userId, itemId);
            return ResponseEntity.ok(Map.of("success", true, "data", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Cart cleared"));
    }
}
