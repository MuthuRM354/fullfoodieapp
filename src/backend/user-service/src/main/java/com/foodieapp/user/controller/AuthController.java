package com.foodieapp.user.controller;

import com.foodieapp.user.dto.ChangePasswordRequest;
import com.foodieapp.user.dto.JwtResponse;
import com.foodieapp.user.dto.LoginRequest;
import com.foodieapp.user.dto.RegisterRequest;
import com.foodieapp.user.service.AuthService;
import com.foodieapp.user.service.UserService;
import com.foodieapp.user.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            JwtResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "User registered successfully",
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }

    /**
     * POST /api/auth/change-password
     * Header: Authorization: Bearer <token>
     * Body: { currentPassword, newPassword, confirmPassword }
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            Long userId = jwtUtil.extractUserId(token);
            userService.changePassword(userId, request);
            return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
