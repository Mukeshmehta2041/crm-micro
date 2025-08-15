package com.programmingmukesh.tenant.service.tenant_service.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API Error details for error responses.
 * 
 * <p>
 * This class provides detailed error information including:
 * </p>
 * <ul>
 * <li>Error code and type</li>
 * <li>Detailed error messages</li>
 * <li>Field-specific validation errors</li>
 * <li>Stack trace information (for debugging)</li>
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
public class ApiError {

  /**
   * Error code for programmatic handling.
   */
  private String code;

  /**
   * Error type/category.
   */
  private String type;

  /**
   * Detailed error message.
   */
  private String message;

  /**
   * List of field-specific error details.
   */
  private List<ErrorDetail> details;

  /**
   * Stack trace information (only in development).
   */
  private String stackTrace;

  /**
   * Timestamp when the error occurred.
   */
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();

  /**
   * Creates a simple API error.
   * 
   * @param code    the error code
   * @param message the error message
   * @return API error
   */
  public static ApiError of(String code, String message) {
    return ApiError.builder()
        .code(code)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an API error with type.
   * 
   * @param code    the error code
   * @param type    the error type
   * @param message the error message
   * @return API error
   */
  public static ApiError of(String code, String type, String message) {
    return ApiError.builder()
        .code(code)
        .type(type)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an API error with validation details.
   * 
   * @param code    the error code
   * @param message the error message
   * @param details the validation error details
   * @return API error
   */
  public static ApiError withDetails(String code, String message, List<ErrorDetail> details) {
    return ApiError.builder()
        .code(code)
        .message(message)
        .details(details)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an API error with stack trace (for debugging).
   * 
   * @param code       the error code
   * @param message    the error message
   * @param stackTrace the stack trace
   * @return API error
   */
  public static ApiError withStackTrace(String code, String message, String stackTrace) {
    return ApiError.builder()
        .code(code)
        .message(message)
        .stackTrace(stackTrace)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
