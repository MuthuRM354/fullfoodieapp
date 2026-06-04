package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> searchRestaurants(String city, String cuisine, String search) {
        return restaurantRepository.searchRestaurants(city, cuisine, search);
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long id, Restaurant updated) {
        Restaurant existing = getRestaurantById(id);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getCuisineType() != null) existing.setCuisineType(updated.getCuisineType());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getCity() != null) existing.setCity(updated.getCity());
        if (updated.getPincode() != null) existing.setPincode(updated.getPincode());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());
        existing.setOpen(updated.isOpen());
        return restaurantRepository.save(existing);
    }

    public void deleteRestaurant(Long id) {
        Restaurant r = getRestaurantById(id);
        r.setActive(false);
        restaurantRepository.save(r);
    }

    public List<Restaurant> getRestaurantsByOwner(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    public Restaurant updateRating(Long restaurantId, double newRating, int totalReviews) {
        Restaurant r = getRestaurantById(restaurantId);
        r.setRating(newRating);
        r.setTotalReviews(totalReviews);
        return restaurantRepository.save(r);
    }
}
