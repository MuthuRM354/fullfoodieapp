package com.foodieapp.order.service;

import com.foodieapp.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrderNotificationService {

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    private final String internalApiKey;

    public OrderNotificationService(
            RestTemplate restTemplate,
            @Value("${services.notification.url:http://localhost:8086}") String notificationServiceUrl,
            @Value("${internal.api-key:foodieapp-internal-service-key-change-me}") String internalApiKey) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
        this.internalApiKey = internalApiKey;
    }

    public void notifyOrderPlaced(Order order) {
        String title   = "Order Placed! 🎉";
        String message = String.format("Your order #%d from %s has been placed successfully.",
                order.getId(), order.getRestaurantName());
        sendNotification(order.getUserId(), title, message, "ORDER");
    }

    public void notifyStatusChange(Order order) {
        String title   = "Order Update";
        String message = buildStatusMessage(order);
        sendNotification(order.getUserId(), title, message, "ORDER");
    }

    private String buildStatusMessage(Order order) {
        return switch (order.getStatus()) {
            case CONFIRMED       -> String.format("Order #%d confirmed by %s. Preparing your food!", order.getId(), order.getRestaurantName());
            case PREPARING       -> String.format("Order #%d is being prepared by %s.", order.getId(), order.getRestaurantName());
            case READY_FOR_PICKUP-> String.format("Order #%d is ready and waiting for a delivery partner.", order.getId());
            case OUT_FOR_DELIVERY-> String.format("Order #%d is on the way! Your delivery partner is heading to you.", order.getId());
            case DELIVERED       -> String.format("Order #%d delivered successfully. Enjoy your meal! 🍽️", order.getId());
            case CANCELLED       -> String.format("Order #%d has been cancelled.", order.getId());
            default              -> String.format("Order #%d status updated to %s.", order.getId(), order.getStatus());
        };
    }

    private void sendNotification(Long userId, String title, String message, String type) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId",  userId);
            payload.put("title",   title);
            payload.put("message", message);
            payload.put("type",    type);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // notification-service requires authentication on all non-actuator
            // routes; this is a trusted server-to-server call, so we use the
            // shared internal service key rather than forwarding a user JWT.
            headers.set("X-Internal-Api-Key", internalApiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications",
                    entity,
                    Map.class);

            log.debug("Notification sent to user {} — {}", userId, title);
        } catch (Exception e) {
            // Non-critical: log the error but don't fail the order flow
            log.warn("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }
}
