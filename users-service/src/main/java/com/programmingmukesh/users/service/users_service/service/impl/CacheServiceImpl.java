package com.programmingmukesh.users.service.users_service.service.impl;

import java.util.Set;
import java.util.UUID;

import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.programmingmukesh.users.service.users_service.service.CacheService;
import com.programmingmukesh.users.service.users_service.service.CacheStats;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of cache service for advanced cache operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @CircuitBreaker(name = "cache", fallbackMethod = "evictUserFallback")
    public void evictUser(UUID userId) {
        try {
            var cache = cacheManager.getCache("users");
            if (cache != null) {
                cache.evict(userId);
                log.debug("Evicted user from cache: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict user from cache: {}", e.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "cache", fallbackMethod = "evictUserByUsernameFallback")
    public void evictUserByUsername(String username) {
        try {
            var cache = cacheManager.getCache("users");
            if (cache != null) {
                cache.evict("username:" + username);
                log.debug("Evicted user from cache by username: {}", username);
            }
        } catch (Exception e) {
            log.warn("Failed to evict user by username from cache: {}", e.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "cache", fallbackMethod = "evictUserByEmailFallback")
    public void evictUserByEmail(String email) {
        try {
            var cache = cacheManager.getCache("users");
            if (cache != null) {
                cache.evict("email:" + email);
                log.debug("Evicted user from cache by email: {}", email);
            }
        } catch (Exception e) {
            log.warn("Failed to evict user by email from cache: {}", e.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "cache", fallbackMethod = "clearAllUserCachesFallback")
    public void clearAllUserCaches() {
        try {
            var userCache = cacheManager.getCache("users");
            if (userCache != null) {
                userCache.clear();
                log.info("Cleared all user caches");
            }
        } catch (Exception e) {
            log.warn("Failed to clear all user caches: {}", e.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "cache", fallbackMethod = "clearSearchCachesFallback")
    public void clearSearchCaches() {
        try {
            var searchCache = cacheManager.getCache("user-search");
            if (searchCache != null) {
                searchCache.clear();
                log.info("Cleared search caches");
            }
        } catch (Exception e) {
            log.warn("Failed to clear search caches: {}", e.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "cache", fallbackMethod = "getCacheStatsFallback")
    public CacheStats getCacheStats() {
        try {
            // This is a simplified implementation
            // In a real scenario, you'd get actual metrics from Redis or cache provider
            return CacheStats.builder()
                    .cacheAvailable(isCacheAvailable())
                    .userCacheSize(getCacheSize("users"))
                    .searchCacheSize(getCacheSize("user-search"))
                    .statsCacheSize(getCacheSize("user-stats"))
                    .hitRatio(0.85) // This would come from actual metrics
                    .totalHits(1000) // This would come from actual metrics
                    .totalMisses(150) // This would come from actual metrics
                    .build();
        } catch (Exception e) {
            log.warn("Failed to get cache stats: {}", e.getMessage());
            return CacheStats.builder()
                    .cacheAvailable(false)
                    .build();
        }
    }

    @Override
    public void warmUpCache() {
        log.info("Cache warm-up initiated");
        // Implementation would depend on your specific requirements
        // For example, pre-load frequently accessed users
    }

    @Override
    public boolean isCacheAvailable() {
        try {
            redisTemplate.opsForValue().set("health-check", "ok");
            String result = (String) redisTemplate.opsForValue().get("health-check");
            redisTemplate.delete("health-check");
            return "ok".equals(result);
        } catch (Exception e) {
            log.warn("Cache availability check failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @CircuitBreaker(name = "cache", fallbackMethod = "getCacheKeysFallback")
    public Set<String> getCacheKeys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.warn("Failed to get cache keys: {}", e.getMessage());
            return Set.of();
        }
    }

    private long getCacheSize(String cacheName) {
        try {
            Set<String> keys = redisTemplate.keys(cacheName + "*");
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            log.warn("Failed to get cache size for {}: {}", cacheName, e.getMessage());
            return 0;
        }
    }

    // Fallback methods
    public void evictUserFallback(UUID userId, Exception ex) {
        log.warn("Cache eviction fallback for user {}: {}", userId, ex.getMessage());
    }

    public void evictUserByUsernameFallback(String username, Exception ex) {
        log.warn("Cache eviction fallback for username {}: {}", username, ex.getMessage());
    }

    public void evictUserByEmailFallback(String email, Exception ex) {
        log.warn("Cache eviction fallback for email {}: {}", email, ex.getMessage());
    }

    public void clearAllUserCachesFallback(Exception ex) {
        log.warn("Clear all user caches fallback: {}", ex.getMessage());
    }

    public void clearSearchCachesFallback(Exception ex) {
        log.warn("Clear search caches fallback: {}", ex.getMessage());
    }

    public CacheStats getCacheStatsFallback(Exception ex) {
        log.warn("Get cache stats fallback: {}", ex.getMessage());
        return CacheStats.builder()
                .cacheAvailable(false)
                .build();
    }

    public Set<String> getCacheKeysFallback(String pattern, Exception ex) {
        log.warn("Get cache keys fallback: {}", ex.getMessage());
        return Set.of();
    }
}