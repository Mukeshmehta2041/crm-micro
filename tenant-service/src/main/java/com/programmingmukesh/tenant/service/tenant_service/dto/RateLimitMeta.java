package com.programmingmukesh.tenant.service.tenant_service.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Rate limiting metadata for API responses.
 * 
 * <p>
 * This class provides rate limiting information including:
 * </p>
 * <ul>
 * <li>Request limits and remaining quota</li>
 * <li>Reset time information</li>
 * <li>Rate limiting policy details</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RateLimitMeta {

  /**
   * Maximum number of requests allowed in the time window.
   */
  private int limit;

  /**
   * Number of requests remaining in the current time window.
   */
  private int remaining;

  /**
   * Timestamp when the rate limit window resets.
   */
  private LocalDateTime resetTime;

  /**
   * Duration of the rate limit window in seconds.
   */
  private int windowSeconds;

  /**
   * The rate limiting policy applied (e.g., "per-tenant", "per-user").
   */
  private String policy;

  /**
   * Creates rate limit metadata.
   * 
   * @param limit         the request limit
   * @param remaining     the remaining requests
   * @param resetTime     the reset time
   * @param windowSeconds the window duration in seconds
   * @return rate limit metadata
   */
  public static RateLimitMeta of(int limit, int remaining, LocalDateTime resetTime, int windowSeconds) {
    return RateLimitMeta.builder()
        .limit(limit)
        .remaining(remaining)
        .resetTime(resetTime)
        .windowSeconds(windowSeconds)
        .build();
  }

  /**
   * Creates rate limit metadata with policy.
   * 
   * @param limit         the request limit
   * @param remaining     the remaining requests
   * @param resetTime     the reset time
   * @param windowSeconds the window duration in seconds
   * @param policy        the rate limiting policy
   * @return rate limit metadata
   */
  public static RateLimitMeta of(int limit, int remaining, LocalDateTime resetTime,
      int windowSeconds, String policy) {
    return RateLimitMeta.builder()
        .limit(limit)
        .remaining(remaining)
        .resetTime(resetTime)
        .windowSeconds(windowSeconds)
        .policy(policy)
        .build();
  }

  /**
   * Checks if the rate limit is exceeded.
   * 
   * @return true if rate limit is exceeded, false otherwise
   */
  public boolean isExceeded() {
    return remaining <= 0;
  }

  /**
   * Gets the percentage of quota used.
   * 
   * @return the percentage of quota used (0-100)
   */
  public double getQuotaUsedPercentage() {
    if (limit <= 0) {
      return 0.0;
    }
    return ((double) (limit - remaining) / limit) * 100.0;
  }
}
