package com.foodieapp.restaurant.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtil {

    private AuthUtil() {}

    /** The authenticated caller's userId, or null for internal-service/anonymous calls. */
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

    /**
     * True if the caller is allowed to manage a resource owned by ownerId —
     * either they ARE that owner, or they're an admin/trusted internal call.
     */
    public static boolean canManage(Long ownerId) {
        if (hasRole("ADMIN") || hasRole("SERVICE")) return true;
        Long userId = currentUserId();
        return userId != null && userId.equals(ownerId);
    }
}
