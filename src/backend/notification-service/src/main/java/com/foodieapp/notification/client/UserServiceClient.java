package com.foodieapp.notification.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Looks up a user's email/phone from user-service when a notification
 * payload only includes a userId (the common case — order-service, for
 * example, doesn't carry the user's contact details on the Order entity).
 * Authenticates with the shared internal service key rather than a user JWT,
 * since this is a trusted server-to-server call.
 */
@Component
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.user.url:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${internal.api-key:foodieapp-internal-service-key-change-me}")
    private String internalApiKey;

    public record ContactInfo(String email, String phone, String name) {}

    public ContactInfo lookup(Long userId) {
        if (userId == null) return null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Api-Key", internalApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            var resp = restTemplate.exchange(
                    userServiceUrl + "/api/users/" + userId,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    Map.class);

            Object body = resp.getBody();
            if (body instanceof Map<?, ?> map) {
                Object data = map.get("data");
                if (data instanceof Map<?, ?> user) {
                    String email = (String) user.get("email");
                    String phone = (String) user.get("phone");
                    String name = (String) user.get("name");
                    return new ContactInfo(email, phone, name);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to resolve contact info for userId {}: {}", userId, e.getMessage());
        }
        return null;
    }
}
