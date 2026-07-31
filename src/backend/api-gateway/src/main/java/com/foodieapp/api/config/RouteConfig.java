package com.foodieapp.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class RouteConfig {

    @Value("${services.user}") private String userService;
    @Value("${services.restaurant}") private String restaurantService;
    @Value("${services.order}") private String orderService;
    @Value("${services.payment}") private String paymentService;
    @Value("${services.delivery}") private String deliveryService;
    @Value("${services.notification}") private String notificationService;
    @Value("${services.review}") private String reviewService;
    @Value("${services.tracking}") private String trackingService;
    @Value("${services.admin}") private String adminService;

    /**
     * Returns a prefix→base-URL map, checked in order (most specific first).
     */
    @Bean
    public Map<String, String> serviceRoutes() {
        Map<String, String> routes = new LinkedHashMap<>();
        // More-specific prefixes MUST come before less-specific ones (startsWith check, in order)
        routes.put("/api/auth",          userService);
        routes.put("/api/users",         userService);
        routes.put("/api/restaurants",   restaurantService);
        routes.put("/api/menu",          restaurantService);
        routes.put("/api/cart",          orderService);
        routes.put("/api/orders",        orderService);
        routes.put("/api/payments",      paymentService);
        routes.put("/api/delivery",      deliveryService);
        routes.put("/api/notifications", notificationService);
        routes.put("/api/reviews",       reviewService);
        routes.put("/api/tracking",      trackingService);
        routes.put("/api/location",      trackingService);   // LocationController prefix
        routes.put("/api/wallet",        paymentService);    // WalletController prefix
        // /api/admin/users → user-service (must be before /api/admin → admin-service)
        routes.put("/api/admin/users",   userService);
        routes.put("/api/admin",         adminService);
        return routes;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(25))
                .build();
    }
}
