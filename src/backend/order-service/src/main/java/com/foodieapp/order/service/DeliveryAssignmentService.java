package com.foodieapp.order.service;

import com.foodieapp.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * When an order becomes READY_FOR_PICKUP, auto-assigns the first available
 * delivery partner so it shows up on that partner's dashboard. Without this,
 * orders would sit "ready" with no mechanism to ever reach a partner.
 */
@Service
@Slf4j
public class DeliveryAssignmentService {

    private final RestTemplate restTemplate;
    private final String deliveryServiceUrl;
    private final String internalApiKey;

    public DeliveryAssignmentService(
            RestTemplate restTemplate,
            @Value("${services.delivery.url:http://localhost:8085}") String deliveryServiceUrl,
            @Value("${internal.api-key:foodieapp-internal-service-key-change-me}") String internalApiKey) {
        this.restTemplate = restTemplate;
        this.deliveryServiceUrl = deliveryServiceUrl;
        this.internalApiKey = internalApiKey;
    }

    @SuppressWarnings("unchecked")
    public void autoAssignPartner(Order order) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Api-Key", internalApiKey);
            HttpEntity<Void> getEntity = new HttpEntity<>(headers);

            var availableRes = restTemplate.exchange(
                    deliveryServiceUrl + "/api/delivery/partners/available",
                    HttpMethod.GET, getEntity, Map.class);

            Map<String, Object> availableBody = availableRes.getBody();
            List<Map<String, Object>> partners = availableBody != null
                    ? (List<Map<String, Object>>) availableBody.get("data") : null;

            if (partners == null || partners.isEmpty()) {
                log.warn("No available delivery partner to assign order {}", order.getId());
                return;
            }

            Object partnerId = partners.get(0).get("id");

            Map<String, Object> assignment = new HashMap<>();
            assignment.put("orderId", order.getId());
            assignment.put("deliveryPartnerId", partnerId);
            assignment.put("restaurantName", order.getRestaurantName());
            assignment.put("deliveryAddress", order.getDeliveryAddress());

            HttpHeaders postHeaders = new HttpHeaders();
            postHeaders.setContentType(MediaType.APPLICATION_JSON);
            postHeaders.set("X-Internal-Api-Key", internalApiKey);
            HttpEntity<Map<String, Object>> postEntity = new HttpEntity<>(assignment, postHeaders);

            restTemplate.postForEntity(
                    deliveryServiceUrl + "/api/delivery/assignments", postEntity, Map.class);

            log.debug("Assigned order {} to delivery partner {}", order.getId(), partnerId);
        } catch (Exception e) {
            // Non-critical: failure to auto-assign shouldn't break the status update itself
            log.warn("Failed to auto-assign delivery partner for order {}: {}", order.getId(), e.getMessage());
        }
    }
}
