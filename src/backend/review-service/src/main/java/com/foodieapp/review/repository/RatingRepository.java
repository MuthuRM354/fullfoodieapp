package com.foodieapp.review.repository;
import com.foodieapp.review.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByRestaurantId(Long restaurantId);
}
