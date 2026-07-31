package com.foodieapp.order.controller;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;
import com.foodieapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            String deliveryAddress = request.get("deliveryAddress").toString();

            // Accept totalAmount from frontend (includes delivery fee + tax).
            // If not provided, backend will calculate from cart items.
            BigDecimal totalAmount = null;
            if (request.containsKey("totalAmount") && request.get("totalAmount") != null) {
                totalAmount = new BigDecimal(request.get("totalAmount").toString());
            }

            Order order = orderService.placeOrder(userId, deliveryAddress, totalAmount);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Order placed", "data", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrder(orderId);
            return ResponseEntity.ok(Map.of("success", true, "data", order));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId,
                                          @RequestBody Map<String, String> request) {
        try {
            OrderStatus status = OrderStatus.valueOf(request.get("status"));
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(Map.of("success", true, "data", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", orders));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<?> getOrdersByRestaurant(@PathVariable Long restaurantId) {
        List<Order> orders = orderService.getOrdersByRestaurant(restaurantId);
        return ResponseEntity.ok(Map.of("success", true, "data", orders));
    }
}
