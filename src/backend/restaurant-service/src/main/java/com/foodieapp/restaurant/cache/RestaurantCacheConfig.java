package com.foodieapp.restaurant.cache;

import com.foodieapp.restaurant.model.Restaurant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RestaurantCacheConfig {

    @Bean
    public ConcurrentHashMap<Long, Restaurant> restaurantCache() {
        return new ConcurrentHashMap<>();
    }
}
