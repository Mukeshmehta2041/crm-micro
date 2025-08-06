package com.programmingmukesh.auth.service.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for the Auth Service.
 * 
 * <p>
 * This class provides centralized exception handling for all controllers.
 * It converts technical exceptions into user-friendly API responses.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles validation errors from @Valid annotations.
   * 
   * @param ex the validation exception
   * @return ResponseEntity with validation error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    // Create a user-friendly error message
    StringBuilder errorMessage = new StringBuilder("Validation failed: ");
    errors.forEach((field, message) -> {
      errorMessage.append(field).append(" - ").append(message).append("; ");
    });

    log.warn("Validation failed: {}", errorMessage.toString());

    return ResponseEntity.badRequest()
        .body(ApiResponse.builder()
            .success(false)
            .message(errorMessage.toString().replaceAll("; $", ""))
            .build());
  }

  /**
   * Handles constraint violation exceptions.
   * 
   * @param ex the constraint violation exception
   * @return ResponseEntity with validation error details
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getConstraintViolations().forEach(violation -> {
      String fieldName = violation.getPropertyPath().toString();
      String errorMessage = violation.getMessage();
      errors.put(fieldName, errorMessage);
    });

    StringBuilder errorMessage = new StringBuilder("Validation failed: ");
    errors.forEach((field, message) -> {
      errorMessage.append(field).append(" - ").append(message).append("; ");
    });

    log.warn("Constraint violation: {}", errorMessage.toString());

    return ResponseEntity.badRequest()
        .body(ApiResponse.builder()
            .success(false)
            .message(errorMessage.toString().replaceAll("; $", ""))
            .build());
  }

  /**
   * Handles IllegalArgumentException for business logic validation.
   * 
   * @param ex the illegal argument exception
   * @return ResponseEntity with validation error details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("Illegal argument exception: {}", ex.getMessage());

    return ResponseEntity.badRequest()
        .body(ApiResponse.builder()
            .success(false)
            .message(ex.getMessage())
            .build());
  }

  /**
   * Handles RuntimeException for service-level errors.
   * 
   * @param ex the runtime exception
   * @return ResponseEntity with service error details
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
    log.warn("Runtime exception: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ApiResponse.builder()
            .success(false)
            .message(ex.getMessage())
            .build());
  }

  /**
   * Generic exception handler for all other exceptions.
   * 
   * @param ex the exception
   * @return ResponseEntity with generic error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.builder()
            .success(false)
            .message("An unexpected error occurred. Please try again later.")
            .build());
  }
}