package com.foodieapp.restaurant.security;

/**
 * Set as the Authentication principal for normal user-JWT requests, so
 * controllers can compare the caller's own userId against a resource's
 * owner without re-parsing the JWT themselves. Internal service-to-service
 * calls (X-Internal-Api-Key) keep the plain "internal-service" String
 * principal instead — those are inherently trusted, not tied to a user.
 */
public record AuthPrincipal(Long userId, String email) {
}
