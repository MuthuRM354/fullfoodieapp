package com.foodieapp.order.repository;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
    List<Order> findByDeliveryPartnerId(Long deliveryPartnerId);
    List<Order> findByStatus(OrderStatus status);
}
