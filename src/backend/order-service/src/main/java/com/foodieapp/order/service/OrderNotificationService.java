package com.foodieapp.order.service;

import com.foodieapp.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderNotificationService {

    public void notifyOrderPlaced(Order order) {
        log.info("ORDER NOTIFICATION: Order #{} placed for user {} at restaurant {}",
                order.getId(), order.getUserId(), order.getRestaurantName());
    }

    public void notifyStatusChange(Order order) {
        log.info("ORDER NOTIFICATION: Order #{} status changed to {}", order.getId(), order.getStatus());
    }
}
