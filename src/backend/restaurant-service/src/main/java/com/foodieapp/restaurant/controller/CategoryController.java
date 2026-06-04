package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.model.Category;
import com.foodieapp.restaurant.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/categories")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> getCategories(@PathVariable Long restaurantId) {
        List<Category> categories = categoryRepository.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(Map.of("success", true, "data", categories));
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@PathVariable Long restaurantId, @RequestBody Category category) {
        category.setRestaurantId(restaurantId);
        Category saved = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", saved));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long restaurantId, @PathVariable Long categoryId) {
        categoryRepository.deleteById(categoryId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Category deleted"));
    }
}
