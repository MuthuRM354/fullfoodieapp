package com.foodieapp.user.controller;

import com.foodieapp.user.dto.SavedAddressRequest;
import com.foodieapp.user.model.SavedAddress;
import com.foodieapp.user.service.SavedAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SavedAddressController {

    private final SavedAddressService savedAddressService;

    @GetMapping
    public ResponseEntity<?> list(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("success", true, "data", savedAddressService.list(userId)));
    }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long userId, @Valid @RequestBody SavedAddressRequest request) {
        SavedAddress created = savedAddressService.create(userId, request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Address saved", "data", created));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<?> update(@PathVariable Long userId, @PathVariable Long addressId,
                                     @Valid @RequestBody SavedAddressRequest request) {
        try {
            SavedAddress updated = savedAddressService.update(userId, addressId, request);
            return ResponseEntity.ok(Map.of("success", true, "message", "Address updated", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
            savedAddressService.delete(userId, addressId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Address deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
