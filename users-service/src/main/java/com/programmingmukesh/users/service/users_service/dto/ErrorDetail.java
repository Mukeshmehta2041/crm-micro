package com.programmingmukesh.users.service.users_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enhanced Error Detail DTO for providing comprehensive error information.
 * 
 * <p>
 * This DTO provides detailed error information including:
 * </p>
 * <ul>
 * <li>Error codes for programmatic handling</li>
 * <li>User-friendly error messages</li>
 * <li>Field-specific error details</li>
 * <li>Contextual information for debugging</li>
 * <li>Suggestions for error resolution</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

  /**
   * The error code for programmatic handling.
   */
  private String code;

  /**
   * The user-friendly error message.
   */
  private String message;

  /**
   * The field that caused the error (if applicable).
   */
  private String field;

  /**
   * The rejected value (if applicable).
   */
  private Object rejectedValue;

  /**
   * The expected value or format (if applicable).
   */
  private String expectedValue;

  /**
   * The error category (VALIDATION, BUSINESS, SYSTEM, etc.).
   */
  private String category;

  /**
   * The severity level (LOW, MEDIUM, HIGH, CRITICAL).
   */
  private String severity;

  /**
   * Suggestions for resolving the error.
   */
  private String suggestion;

  /**
   * Additional context information.
   */
  private Map<String, Object> context;

  /**
   * The timestamp when the error occurred.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;

  /**
   * The path or location where the error occurred.
   */
  private String path;

  /**
   * Reference documentation or help link.
   */
  private String helpUrl;

  /**
   * The constraint that was violated (for validation errors).
   */
  private String constraint;

  /**
   * The original value that was provided.
   */
  private String value;

  /**
   * Creates a validation error detail.
   * 
   * @param field the field name
   * @param message the error message
   * @param rejectedValue the rejected value
   * @return error detail
   */
  public static ErrorDetail validationError(String field, String message, Object rejectedValue) {
    return ErrorDetail.builder()
        .code("VALIDATION_ERROR")
        .category("VALIDATION")
        .severity("MEDIUM")
        .field(field)
        .message(message)
        .rejectedValue(rejectedValue)
        .timestamp(LocalDateTime.now())
        .suggestion("Please provide a valid value for this field")
        .build();
  }

  /**
   * Creates a business rule error detail.
   * 
   * @param message the error message
   * @param suggestion the suggestion for resolution
   * @return error detail
   */
  public static ErrorDetail businessError(String message, String suggestion) {
    return ErrorDetail.builder()
        .code("BUSINESS_RULE_VIOLATION")
        .category("BUSINESS")
        .severity("HIGH")
        .message(message)
        .suggestion(suggestion)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a system error detail.
   * 
   * @param message the error message
   * @return error detail
   */
  public static ErrorDetail systemError(String message) {
    return ErrorDetail.builder()
        .code("SYSTEM_ERROR")
        .category("SYSTEM")
        .severity("CRITICAL")
        .message(message)
        .suggestion("Please try again later or contact support if the problem persists")
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a not found error detail.
   * 
   * @param resource the resource that was not found
   * @param identifier the identifier used for search
   * @return error detail
   */
  public static ErrorDetail notFoundError(String resource, String identifier) {
    return ErrorDetail.builder()
        .code("RESOURCE_NOT_FOUND")
        .category("BUSINESS")
        .severity("MEDIUM")
        .message(String.format("%s with identifier '%s' was not found", resource, identifier))
        .suggestion("Please verify the identifier and try again")
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an access denied error detail.
   * 
   * @param action the action that was denied
   * @param resource the resource being accessed
   * @return error detail
   */
  public static ErrorDetail accessDeniedError(String action, String resource) {
    return ErrorDetail.builder()
        .code("ACCESS_DENIED")
        .category("SECURITY")
        .severity("HIGH")
        .message(String.format("Access denied for action '%s' on resource '%s'", action, resource))
        .suggestion("Please ensure you have the necessary permissions")
        .timestamp(LocalDateTime.now())
        .build();
  }
}