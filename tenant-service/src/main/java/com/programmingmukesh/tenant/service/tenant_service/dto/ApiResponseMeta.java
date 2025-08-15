package com.programmingmukesh.tenant.service.tenant_service.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * API Response metadata for additional information about the response.
 * 
 * <p>
 * This class provides metadata about the API response including:
 * </p>
 * <ul>
 * <li>Pagination information</li>
 * <li>Rate limiting information</li>
 * <li>Request tracking information</li>
 * <li>Performance metrics</li>
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
public class ApiResponseMeta {

  /**
   * Pagination metadata (if applicable).
   */
  private PaginationMeta pagination;

  /**
   * Rate limiting metadata (if applicable).
   */
  private RateLimitMeta rateLimit;

  /**
   * Unique request ID for tracking.
   */
  private String requestId;

  /**
   * API version used for this request.
   */
  private String version;

  /**
   * Response time in milliseconds.
   */
  private Long responseTimeMs;

  /**
   * Server instance that processed the request.
   */
  private String serverInstance;

  /**
   * Creates metadata with pagination information.
   * 
   * @param pagination the pagination metadata
   * @return API response metadata
   */
  public static ApiResponseMeta withPagination(PaginationMeta pagination) {
    return ApiResponseMeta.builder()
        .pagination(pagination)
        .build();
  }

  /**
   * Creates metadata with rate limit information.
   * 
   * @param rateLimit the rate limit metadata
   * @return API response metadata
   */
  public static ApiResponseMeta withRateLimit(RateLimitMeta rateLimit) {
    return ApiResponseMeta.builder()
        .rateLimit(rateLimit)
        .build();
  }

  /**
   * Creates metadata with request tracking information.
   * 
   * @param requestId the request ID
   * @param version   the API version
   * @return API response metadata
   */
  public static ApiResponseMeta withTracking(String requestId, String version) {
    return ApiResponseMeta.builder()
        .requestId(requestId)
        .version(version)
        .build();
  }
}
