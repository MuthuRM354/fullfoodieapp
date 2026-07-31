package com.foodieapp.review.service;

import com.foodieapp.review.model.Rating;
import com.foodieapp.review.repository.RatingRepository;
import com.foodieapp.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class RatingService {

    private final RatingRepository ratingRepo;
    private final ReviewRepository reviewRepo;
    private final RestTemplate restTemplate;
    private final String restaurantServiceUrl;
    private final String internalApiKey;

    public RatingService(
            RatingRepository ratingRepo,
            ReviewRepository reviewRepo,
            RestTemplate restTemplate,
            @Value("${services.restaurant.url:http://localhost:8082}") String restaurantServiceUrl,
            @Value("${internal.api-key:foodieapp-internal-service-key-change-me}") String internalApiKey) {
        this.ratingRepo           = ratingRepo;
        this.reviewRepo           = reviewRepo;
        this.restTemplate         = restTemplate;
        this.restaurantServiceUrl = restaurantServiceUrl;
        this.internalApiKey       = internalApiKey;
    }

    public Rating getSummary(Long restaurantId) {
        return ratingRepo.findByRestaurantId(restaurantId)
                .orElse(Rating.builder().restaurantId(restaurantId).averageRating(0.0).totalReviews(0).build());
    }

    @Transactional
    public void recalculate(Long restaurantId) {
        Double avg   = reviewRepo.avgRatingByRestaurant(restaurantId);
        long   total = reviewRepo.countByRestaurantId(restaurantId);

        double roundedAvg = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
        int    totalReviews = (int) total;

        // Update rating in this service's own table
        Rating rating = ratingRepo.findByRestaurantId(restaurantId)
                .orElse(Rating.builder().restaurantId(restaurantId).build());
        rating.setAverageRating(roundedAvg);
        rating.setTotalReviews(totalReviews);
        ratingRepo.save(rating);

        // Sync back to restaurant-service so the restaurant card shows live ratings
        syncToRestaurantService(restaurantId, roundedAvg, totalReviews);
    }

    private void syncToRestaurantService(Long restaurantId, double averageRating, int totalReviews) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // restaurant-service requires authentication on all non-GET
            // routes; this is a trusted server-to-server call, so we use the
            // shared internal service key rather than forwarding a user JWT.
            headers.set("X-Internal-Api-Key", internalApiKey);
            Map<String, Object> body = Map.of(
                    "averageRating", averageRating,
                    "totalReviews",  totalReviews);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.put(
                    restaurantServiceUrl + "/api/restaurants/" + restaurantId + "/rating",
                    entity);
            log.debug("Synced rating for restaurant {} → avg={}, total={}", restaurantId, averageRating, totalReviews);
        } catch (Exception e) {
            // Non-critical: rating sync failure should not break review submission
            log.warn("Failed to sync rating to restaurant-service for restaurant {}: {}", restaurantId, e.getMessage());
        }
    }
}
