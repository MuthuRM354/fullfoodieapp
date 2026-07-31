package com.foodieapp.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    // Allowed origins from env var (comma-separated). Defaults to wildcard for dev.
    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Parse comma-separated origins; "*" means all origins (no credentials)
        List<String> origins = List.of(allowedOrigins.split(","));
        boolean isWildcard = origins.contains("*");

        if (isWildcard) {
            config.addAllowedOriginPattern("*");
            config.setAllowCredentials(false); // credentials not allowed with wildcard
        } else {
            config.setAllowedOrigins(origins);
            config.setAllowCredentials(true); // safe with explicit origins
        }

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
