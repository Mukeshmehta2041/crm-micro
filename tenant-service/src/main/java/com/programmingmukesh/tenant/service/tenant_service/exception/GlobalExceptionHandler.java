package com.programmingmukesh.tenant.service.tenant_service.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.programmingmukesh.tenant.service.tenant_service.dto.ApiError;
import com.programmingmukesh.tenant.service.tenant_service.dto.ApiResponse;
import com.programmingmukesh.tenant.service.tenant_service.dto.ErrorDetail;

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

    ApiError error = ApiError.of("TENANT_NOT_FOUND", "Not Found", ex.getMessage());
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

    ApiError error = ApiError.of("TENANT_ALREADY_EXISTS", "Conflict", ex.getMessage());
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

    ApiError error = ApiError.of("TENANT_VALIDATION_ERROR", "Validation Error", ex.getMessage());
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

    ApiError error = ApiError.withDetails("VALIDATION_ERROR", "Input validation failed", details);
    ApiResponse<Object> response = ApiResponse.error("Validation failed", error);

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

    ApiError error = ApiError.of("ILLEGAL_ARGUMENT", "Bad Request", ex.getMessage());
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
        "An unexpected error occurred. Please try again later.");
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

    ApiError error = ApiError.of("RUNTIME_ERROR", "Runtime Error", ex.getMessage());
    ApiResponse<Object> response = ApiResponse.error("Runtime error occurred", error);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
