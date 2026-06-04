package com.foodieapp.restaurant.repository;

import com.foodieapp.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByCity(String city);
    List<Restaurant> findByCityAndIsActiveTrue(String city);
    List<Restaurant> findByCuisineTypeAndIsActiveTrue(String cuisineType);
    List<Restaurant> findByOwnerIdAndIsActiveTrue(Long ownerId);
    List<Restaurant> findByOwnerId(Long ownerId);

    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "(:city IS NULL OR r.city = :city) AND " +
           "(:cuisine IS NULL OR r.cuisineType = :cuisine) AND " +
           "(:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Restaurant> searchRestaurants(@Param("city") String city,
                                        @Param("cuisine") String cuisine,
                                        @Param("search") String search);
}
