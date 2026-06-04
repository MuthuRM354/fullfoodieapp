package com.foodieapp.order.controller;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderService orderService;

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<?> getOrderHistory(@PathVariable Long userId) {
        List<Order> history = orderService.getOrderHistory(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", history));
    }
}
