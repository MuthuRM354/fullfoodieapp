package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    public MenuItem addMenuItem(Long restaurantId, MenuItem item) {
        item.setRestaurantId(restaurantId);
        return menuRepository.save(item);
    }

    public MenuItem updateMenuItem(Long restaurantId, Long itemId, MenuItem updated) {
        MenuItem existing = menuRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemId));
        if (!existing.getRestaurantId().equals(restaurantId)) {
            throw new RuntimeException("Menu item does not belong to restaurant");
        }
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getPrice() != null) existing.setPrice(updated.getPrice());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());
        existing.setVeg(updated.isVeg());
        existing.setAvailable(updated.isAvailable());
        return menuRepository.save(existing);
    }

    @Transactional
    public void deleteMenuItem(Long restaurantId, Long itemId) {
        menuRepository.deleteByRestaurantIdAndId(restaurantId, itemId);
    }

    public MenuItem getMenuItemById(Long itemId) {
        return menuRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemId));
    }
}
