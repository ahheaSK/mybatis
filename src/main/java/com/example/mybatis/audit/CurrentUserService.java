package com.example.mybatis.audit;

/**
 * Provides the current authenticated user's username from the JWT token (SecurityContext).
 * Used for audit fields such as who created or last updated an entity.
 */
public interface CurrentUserService {

    /**
     * Returns the username of the currently authenticated user, or null if not authenticated.
     * The username comes from the JWT subject set by {@link com.example.mybatis.security.JwtAuthenticationFilter}.
     */
    String getCurrentUsername();
}
