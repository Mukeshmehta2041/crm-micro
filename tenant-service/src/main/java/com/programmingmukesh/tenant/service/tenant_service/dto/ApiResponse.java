package com.programmingmukesh.tenant.service.tenant_service.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Generic API Response wrapper for consistent response format.
 * 
 * <p>
 * This class provides a standardized response format for all API endpoints
 * including success/error status, data payload, and metadata.
 * </p>
 * 
 * @param <T> the type of data being returned
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
public class ApiResponse<T> {

  /**
   * Indicates if the request was successful.
   */
  private boolean success;

  /**
   * Human-readable message describing the result.
   */
  private String message;

  /**
   * The actual data payload (null for error responses).
   */
  private T data;

  /**
   * Additional metadata about the response.
   */
  private ApiResponseMeta meta;

  /**
   * Error details (only present for error responses).
   */
  private ApiError error;

  /**
   * Timestamp when the response was generated.
   */
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();

  /**
   * Creates a successful response with data.
   * 
   * @param <T>  the type of data
   * @param data the response data
   * @return successful API response
   */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message("Request completed successfully")
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a successful response with data and custom message.
   * 
   * @param <T>     the type of data
   * @param data    the response data
   * @param message the success message
   * @return successful API response
   */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a successful response with data, message, and metadata.
   * 
   * @param <T>     the type of data
   * @param data    the response data
   * @param message the success message
   * @param meta    the response metadata
   * @return successful API response
   */
  public static <T> ApiResponse<T> success(T data, String message, ApiResponseMeta meta) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .meta(meta)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response with message.
   * 
   * @param <T>     the type of data
   * @param message the error message
   * @return error API response
   */
  public static <T> ApiResponse<T> error(String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response with message and error details.
   * 
   * @param <T>     the type of data
   * @param message the error message
   * @param error   the error details
   * @return error API response
   */
  public static <T> ApiResponse<T> error(String message, ApiError error) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .error(error)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a no-content success response.
   * 
   * @param <T>     the type of data
   * @param message the success message
   * @return successful API response with no data
   */
  public static <T> ApiResponse<T> noContent(String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
