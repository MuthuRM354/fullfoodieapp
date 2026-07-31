package com.foodieapp.notification.controller;
import com.foodieapp.notification.model.Notification;
import com.foodieapp.notification.model.NotificationType;
import com.foodieapp.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/notifications") @CrossOrigin("*") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;
    @GetMapping("/{userId}") public ResponseEntity<?> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getByUser(userId)));
    }
    @PostMapping public ResponseEntity<?> create(@RequestBody Map<String, Object> req) {
        try {
            Long userId = Long.parseLong(req.get("userId").toString());
            String title = req.get("title").toString();
            String message = req.get("message").toString();
            NotificationType type;
            try {
                type = NotificationType.valueOf(req.getOrDefault("type", "ACCOUNT").toString());
            } catch (IllegalArgumentException ex) {
                type = NotificationType.ACCOUNT;
            }
            String email = req.get("email") != null ? req.get("email").toString() : null;
            String phone = req.get("phone") != null ? req.get("phone").toString() : null;
            Notification n = service.save(userId, title, message, type, email, phone);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", n));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @PutMapping("/{id}/read") public ResponseEntity<?> markRead(@PathVariable Long id) {
        try { return ResponseEntity.ok(Map.of("success", true, "data", service.markRead(id))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @PutMapping("/{userId}/read-all") public ResponseEntity<?> markAllRead(@PathVariable Long userId) {
        service.markAllRead(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "All notifications marked as read"));
    }
    @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Notification deleted"));
    }
}
