package com.foodieapp.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Enumeration;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GatewayController {

    private final RestTemplate restTemplate;
    private final Map<String, String> serviceRoutes;

    @RequestMapping("/**")
    public ResponseEntity<?> proxy(HttpServletRequest request,
                                   @RequestBody(required = false) byte[] body) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String method = request.getMethod();

        // Find matching service
        String targetBase = resolveService(path);
        if (targetBase == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "No route found for: " + path));
        }

        // Build target URL
        String targetUrl = targetBase + path + (queryString != null ? "?" + queryString : "");
        log.info("Gateway: {} {} -> {}", method, path, targetUrl);

        // Copy headers
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            // Skip hop-by-hop headers
            if (!name.equalsIgnoreCase("host") && !name.equalsIgnoreCase("connection")
                    && !name.equalsIgnoreCase("transfer-encoding")) {
                headers.set(name, request.getHeader(name));
            }
        }

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    URI.create(targetUrl),
                    HttpMethod.valueOf(method),
                    entity,
                    byte[].class
            );
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsByteArray());
        } catch (Exception ex) {
            log.error("Gateway error for {} {}: {}", method, path, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("success", false, "message", "Service unavailable: " + ex.getMessage()));
        }
    }

    private String resolveService(String path) {
        for (Map.Entry<String, String> route : serviceRoutes.entrySet()) {
            if (path.startsWith(route.getKey())) {
                return route.getValue();
            }
        }
        return null;
    }
}
