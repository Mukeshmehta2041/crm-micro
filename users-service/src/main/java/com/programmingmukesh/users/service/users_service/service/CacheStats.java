package com.programmingmukesh.users.service.users_service.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cache statistics data transfer object.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheStats {
    
    private long userCacheSize;
    private long searchCacheSize;
    private long statsCacheSize;
    private double hitRatio;
    private long totalHits;
    private long totalMisses;
    private boolean cacheAvailable;
}