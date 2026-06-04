package com.foodieapp.review.service;
import com.foodieapp.review.model.Rating;
import com.foodieapp.review.repository.RatingRepository;
import com.foodieapp.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
@Service @RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepo;
    private final ReviewRepository reviewRepo;
    public Rating getSummary(Long restaurantId) {
        return ratingRepo.findByRestaurantId(restaurantId)
            .orElse(Rating.builder().restaurantId(restaurantId).averageRating(0.0).totalReviews(0).build());
    }
    public void recalculate(Long restaurantId) {
        Double avg = reviewRepo.avgRatingByRestaurant(restaurantId);
        long total = reviewRepo.countByRestaurantId(restaurantId);
        Rating rating = ratingRepo.findByRestaurantId(restaurantId)
            .orElse(Rating.builder().restaurantId(restaurantId).build());
        rating.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        rating.setTotalReviews((int) total);
        ratingRepo.save(rating);
    }
}
