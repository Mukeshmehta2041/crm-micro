package com.programmingmukesh.tenant.service.tenant_service.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.programmingmukesh.tenant.service.tenant_service.dto.ApiError;
import com.programmingmukesh.tenant.service.tenant_service.dto.ApiResponse;
import com.programmingmukesh.tenant.service.tenant_service.dto.ErrorDetail;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the Tenant Service.
 * 
 * <p>
 * This class handles all exceptions thrown by the tenant service
 * and provides consistent error responses to clients.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles TenantNotFoundException.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(TenantNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleTenantNotFoundException(
      TenantNotFoundException ex, WebRequest request) {
    log.error("Tenant not found: {}", ex.getMessage());

    ApiError error = ApiError.of("TENANT_NOT_FOUND", "Not Found",
        "The tenant you're looking for doesn't exist in our system. Please check the tenant ID or subdomain and try again.");
    ApiResponse<Object> response = ApiResponse.error("Tenant not found", error);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * Handles TenantAlreadyExistsException.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(TenantAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<Object>> handleTenantAlreadyExistsException(
      TenantAlreadyExistsException ex, WebRequest request) {
    log.error("Tenant already exists: {}", ex.getMessage());

    ApiError error = ApiError.of("TENANT_ALREADY_EXISTS", "Conflict",
        "A tenant with this information already exists. Please use a different subdomain or custom domain.");
    ApiResponse<Object> response = ApiResponse.error("Tenant already exists", error);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  /**
   * Handles TenantValidationException.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(TenantValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleTenantValidationException(
      TenantValidationException ex, WebRequest request) {
    log.error("Tenant validation error: {}", ex.getMessage());

    ApiError error = ApiError.of("TENANT_VALIDATION_ERROR", "Validation Error",
        "The information you provided doesn't meet our requirements. Please review the error details and correct any issues.");
    ApiResponse<Object> response = ApiResponse.error("Validation failed", error);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles validation errors from @Valid annotations.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.error("Validation error: {}", ex.getMessage());

    List<ErrorDetail> details = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      details.add(ErrorDetail.of(
          error.getField(),
          error.getRejectedValue(),
          error.getDefaultMessage(),
          error.getCode()));
    }

    ApiError error = ApiError.withDetails("VALIDATION_ERROR",
        "Some of the information you provided is invalid or missing. Please check the field errors below and correct any issues.",
        details);
    ApiResponse<Object> response = ApiResponse.error("Validation failed", error);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles constraint violation exceptions.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    log.error("Constraint violation: {}", ex.getMessage());

    List<ErrorDetail> details = new ArrayList<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      details.add(ErrorDetail.of(
          violation.getPropertyPath().toString(),
          violation.getInvalidValue(),
          violation.getMessage(),
          "CONSTRAINT_VIOLATION"));
    }

    ApiError error = ApiError.withDetails("CONSTRAINT_VIOLATION",
        "The data you provided violates our system constraints. Please review the error details and correct any issues.",
        details);
    ApiResponse<Object> response = ApiResponse.error("Validation failed", error);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles transaction system exceptions.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(TransactionSystemException.class)
  public ResponseEntity<ApiResponse<Object>> handleTransactionSystemException(
      TransactionSystemException ex, WebRequest request) {
    log.error("Transaction system error: {}", ex.getMessage(), ex);

    // Check if the root cause is a constraint violation
    Throwable rootCause = ex.getRootCause();
    if (rootCause instanceof ConstraintViolationException) {
      return handleConstraintViolationException((ConstraintViolationException) rootCause, request);
    }

    ApiError error = ApiError.of("TRANSACTION_ERROR", "Transaction Error",
        "A database transaction error occurred. Please try again later.");
    ApiResponse<Object> response = ApiResponse.error("Transaction failed", error);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * Handles data integrity violation exceptions.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    log.error("Data integrity violation: {}", ex.getMessage(), ex);

    String userMessage = "The information you provided conflicts with our system requirements.";
    String suggestion = "Please check your input and try again.";

    // Check for specific constraint violations
    if (ex.getMessage() != null) {
      if (ex.getMessage().contains("chk_plan_type_valid")) {
        userMessage = "The plan type you selected is not valid.";
        suggestion = "Please choose from: BASIC, STANDARD, PREMIUM, ENTERPRISE, or TRIAL.";
      } else if (ex.getMessage().contains("chk_subdomain_format")) {
        userMessage = "The subdomain format is not valid.";
        suggestion = "Subdomain must contain only lowercase letters, numbers, and hyphens.";
      } else if (ex.getMessage().contains("chk_status_valid")) {
        userMessage = "The status you selected is not valid.";
        suggestion = "Please choose from: ACTIVE, INACTIVE, SUSPENDED, PENDING, or DELETED.";
      } else if (ex.getMessage().contains("chk_primary_color_format")
          || ex.getMessage().contains("chk_secondary_color_format")) {
        userMessage = "The color format is not valid.";
        suggestion = "Colors must be in hex format (e.g., #FF0000 or #F00).";
      } else if (ex.getMessage().contains("chk_contact_email_format")
          || ex.getMessage().contains("chk_billing_email_format")) {
        userMessage = "The email format is not valid.";
        suggestion = "Please provide a valid email address.";
      } else if (ex.getMessage().contains("chk_phone_format")) {
        userMessage = "The phone number format is not valid.";
        suggestion = "Phone numbers can only contain digits, spaces, parentheses, hyphens, and plus sign.";
      }
    }

    ApiError error = ApiError.of("DATA_INTEGRITY_VIOLATION", "Data Validation Error",
        userMessage + " " + suggestion);
    ApiResponse<Object> response = ApiResponse.error("Data validation failed", error);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles IllegalArgumentException.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.error("Illegal argument: {}", ex.getMessage());

    ApiError error = ApiError.of("ILLEGAL_ARGUMENT", "Bad Request",
        "The request contains invalid parameters. Please check your input and try again.");
    ApiResponse<Object> response = ApiResponse.error("Invalid request parameters", error);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles generic exceptions.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(
      Exception ex, WebRequest request) {
    log.error("Unexpected error occurred", ex);

    ApiError error = ApiError.of("INTERNAL_SERVER_ERROR", "Internal Server Error",
        "Something unexpected happened on our end. Please try again in a few minutes. If the problem persists, contact our support team.");
    ApiResponse<Object> response = ApiResponse.error("Internal server error", error);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * Handles RuntimeException.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
      RuntimeException ex, WebRequest request) {
    log.error("Runtime error occurred: {}", ex.getMessage(), ex);

    ApiError error = ApiError.of("RUNTIME_ERROR", "Runtime Error",
        "We're experiencing technical difficulties. Please try again in a few minutes. If the problem continues, our technical team has been notified.");
    ApiResponse<Object> response = ApiResponse.error("Runtime error occurred", error);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * Handles HttpMessageNotReadableException for JSON parsing errors.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {
    log.error("JSON parsing error: {}", ex.getMessage(), ex);

    String userMessage = "The request body is not valid JSON or contains invalid values.";
    String suggestion = "Please ensure your JSON is well-formed and all values are valid.";

    // Check for specific JSON parsing errors
    if (ex.getMessage() != null) {
      if (ex.getMessage().contains("Invalid value")) {
        userMessage = "The JSON you provided contains invalid values for one or more fields.";
        suggestion = "Please review the error details and correct any invalid values.";
      } else if (ex.getMessage().contains("No suitable constructor found")) {
        userMessage = "The JSON structure does not match the expected object structure.";
        suggestion = "Please ensure your JSON is well-formed and matches the expected format.";
      } else if (ex.getMessage().contains("Could not read JSON")) {
        userMessage = "Could not read the JSON data from the request body.";
        suggestion = "Please ensure your request body is not empty and contains valid JSON.";
      }

      // Check for enum validation errors
      if (ex.getMessage().contains("not one of the values accepted for Enum class")) {
        userMessage = "One or more fields contain values that are not allowed.";
        suggestion = "Please check the field values and use only the allowed options.";

        // Extract enum information for better guidance
        if (ex.getMessage().contains("PlanType")) {
          userMessage = "The plan type you specified is not valid.";
          suggestion = "Please choose from: BASIC, STANDARD, PREMIUM, ENTERPRISE, or TRIAL.";
        } else if (ex.getMessage().contains("TenantStatus")) {
          userMessage = "The tenant status you specified is not valid.";
          suggestion = "Please choose from: ACTIVE, INACTIVE, SUSPENDED, PENDING, or DELETED.";
        } else if (ex.getMessage().contains("ConfigType")) {
          userMessage = "The configuration type you specified is not valid.";
          suggestion = "Please choose from: STRING, INTEGER, BOOLEAN, DECIMAL, JSON, URL, or EMAIL.";
        }
      }
    }

    ApiError error = ApiError.of("JSON_PARSING_ERROR", "JSON Parsing Error",
        userMessage + " " + suggestion);
    ApiResponse<Object> response = ApiResponse.error("JSON parsing failed", error);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles InvalidFormatException for specific format errors like enum
   * validation.
   * 
   * @param ex      the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvalidFormatException(
      InvalidFormatException ex, WebRequest request) {
    log.error("Invalid format error: {}", ex.getMessage(), ex);

    String userMessage = "The value you provided for a field is not in the correct format.";
    String suggestion = "Please check the field value and use the correct format.";

    // Check for enum validation errors
    if (ex.getMessage() != null && ex.getMessage().contains("not one of the values accepted for Enum class")) {
      String targetType = ex.getTargetType() != null ? ex.getTargetType().getSimpleName() : "unknown";
      String value = ex.getValue() != null ? ex.getValue().toString() : "unknown";

      userMessage = String.format("The value '%s' is not valid for the %s field.", value, targetType);

      // Provide specific guidance based on the enum type
      if ("PlanType".equals(targetType)) {
        suggestion = "Please choose from: BASIC, STANDARD, PREMIUM, ENTERPRISE, or TRIAL.";
      } else if ("TenantStatus".equals(targetType)) {
        suggestion = "Please choose from: ACTIVE, INACTIVE, SUSPENDED, PENDING, or DELETED.";
      } else if ("ConfigType".equals(targetType)) {
        suggestion = "Please choose from: STRING, INTEGER, BOOLEAN, DECIMAL, JSON, URL, or EMAIL.";
      } else {
        suggestion = "Please check the API documentation for the allowed values.";
      }
    }

    ApiError error = ApiError.of("INVALID_FORMAT_ERROR", "Invalid Format Error",
        userMessage + " " + suggestion);
    ApiResponse<Object> response = ApiResponse.error("Invalid format", error);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
