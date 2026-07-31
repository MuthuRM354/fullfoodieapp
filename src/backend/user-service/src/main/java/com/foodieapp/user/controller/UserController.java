package com.foodieapp.user.controller;

import com.foodieapp.user.dto.UserUpdateRequest;
import com.foodieapp.user.model.User;
import com.foodieapp.user.service.UserService;
import com.foodieapp.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(Map.of("success", true, "data", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        try {
            User user = userService.updateUser(id, request);
            return ResponseEntity.ok(Map.of("success", true, "message", "User updated", "data", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "No token provided"));
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.extractSubject(token);
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(Map.of("success", true, "data", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * PUT /api/users/me — update the currently authenticated user's profile.
     * Frontend calls this from the Profile page (no need to know the user's ID).
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UserUpdateRequest request) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "No token provided"));
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.extractSubject(token);
            User currentUser = userService.getUserByEmail(email);
            User updated = userService.updateUser(currentUser.getId(), request);
            return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
