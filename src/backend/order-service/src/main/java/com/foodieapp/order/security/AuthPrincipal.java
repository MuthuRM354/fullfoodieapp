package com.foodieapp.order.security;

/**
 * Set as the Authentication principal for normal user-JWT requests, so
 * controllers can compare the caller's own userId against a resource's
 * owner without re-parsing the JWT themselves.
 */
public record AuthPrincipal(Long userId, String email) {
}
