package com.foodieapp.payment.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtil {

    private AuthUtil() {}

    /** The authenticated caller's userId, or null if unavailable. */
    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal principal)) return null;
        return principal.userId();
    }

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_" + role)) return true;
        }
        return false;
    }

    /** True if the caller IS ownerId, or is an admin acting on their behalf. */
    public static boolean canManage(Long ownerId) {
        if (hasRole("ADMIN")) return true;
        Long userId = currentUserId();
        return userId != null && userId.equals(ownerId);
    }
}
