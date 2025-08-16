package com.programmingmukesh.users.service.users_service.service;

import java.util.Set;
import java.util.UUID;

/**
 * Service interface for cache operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public interface CacheService {

    /**
     * Evict user from cache by ID.
     */
    void evictUser(UUID userId);

    /**
     * Evict user from cache by username.
     */
    void evictUserByUsername(String username);

    /**
     * Evict user from cache by email.
     */
    void evictUserByEmail(String email);

    /**
     * Clear all user caches.
     */
    void clearAllUserCaches();

    /**
     * Clear search caches.
     */
    void clearSearchCaches();

    /**
     * Get cache statistics.
     */
    CacheStats getCacheStats();

    /**
     * Warm up cache with frequently accessed users.
     */
    void warmUpCache();

    /**
     * Check if cache is available.
     */
    boolean isCacheAvailable();

    /**
     * Get all cache keys matching pattern.
     */
    Set<String> getCacheKeys(String pattern);
}