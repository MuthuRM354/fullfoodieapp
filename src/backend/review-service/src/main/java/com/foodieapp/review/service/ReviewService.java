package com.foodieapp.review.service;

import com.foodieapp.review.model.Review;
import com.foodieapp.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repo;
    private final RatingService ratingService;

    @Transactional
    public Review addReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5)
            throw new RuntimeException("Rating must be between 1 and 5");
        Review saved = repo.save(review);
        ratingService.recalculate(review.getRestaurantId());
        return saved;
    }

    public List<Review> getByRestaurant(Long restaurantId) {
        return repo.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    public List<Review> getByUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void delete(Long id) {
        Review r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found: " + id));
        repo.delete(r);
        ratingService.recalculate(r.getRestaurantId());
    }
}
