package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> search(String query, String city) {
        return restaurantRepository.searchRestaurants(city, null, query);
    }
}
