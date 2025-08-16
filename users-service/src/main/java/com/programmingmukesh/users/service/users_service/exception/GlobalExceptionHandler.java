package com.programmingmukesh.users.service.users_service.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the User Service.
 * Handles all types of exceptions that can occur in the application.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handle user not found exceptions.
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(
      UserNotFoundException ex, WebRequest request) {
    log.warn("User not found: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("User Not Found")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage("The user you're looking for doesn't exist in our system.")
        .suggestion(
            "Please check the user ID or username and try again. If you believe this is an error, contact support.")
        .helpUrl("/api/docs/users#get-user")
        .errorCode("USER_001")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle user already exists exceptions.
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
      UserAlreadyExistsException ex, WebRequest request) {
    log.warn("User already exists: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT.value())
        .error("User Already Exists")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage("A user with this information already exists in our system.")
        .suggestion(
            "Please use a different username or email address. You can also try to log in if you already have an account.")
        .helpUrl("/api/docs/users#create-user")
        .errorCode("USER_002")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  /**
   * Handle user validation exceptions.
   */
  @ExceptionHandler(UserValidationException.class)
  public ResponseEntity<ErrorResponse> handleUserValidationException(
      UserValidationException ex, WebRequest request) {
    log.warn("User validation failed: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failed")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage("The information you provided doesn't meet our requirements.")
        .suggestion("Please review the error details below and correct any issues with your input.")
        .helpUrl("/api/docs/users#validation-rules")
        .errorCode("USER_003")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle method argument validation exceptions.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.warn("Method argument validation failed: {}", ex.getMessage());

    Map<String, String> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            FieldError::getDefaultMessage,
            (existing, replacement) -> existing));

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failed")
        .message("Invalid input parameters")
        .path(request.getDescription(false).replace("uri=", ""))
        .fieldErrors(fieldErrors)
        .userMessage("Some of the information you provided is invalid or missing.")
        .suggestion(
            "Please check the field errors below and correct any issues. Make sure all required fields are filled out properly.")
        .helpUrl("/api/docs/users#validation-rules")
        .errorCode("USER_004")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle constraint violation exceptions.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    log.warn("Constraint violation: {}", ex.getMessage());

    Map<String, String> fieldErrors = ex.getConstraintViolations()
        .stream()
        .collect(Collectors.toMap(
            violation -> violation.getPropertyPath().toString(),
            ConstraintViolation::getMessage,
            (existing, replacement) -> existing));

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Constraint Violation")
        .message("Validation constraints violated")
        .path(request.getDescription(false).replace("uri=", ""))
        .fieldErrors(fieldErrors)
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle data integrity violation exceptions.
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    log.error("Data integrity violation: {}", ex.getMessage());

    String message = "Data integrity constraint violated";
    String userMessage = "The information you provided conflicts with existing data.";
    String suggestion = "Please use different values for the conflicting fields.";

    if (ex.getMessage() != null) {
      if (ex.getMessage().contains("username")) {
        message = "Username already exists";
        userMessage = "This username is already taken by another user.";
        suggestion = "Please choose a different username. You can try adding numbers or special characters to make it unique.";
      } else if (ex.getMessage().contains("email")) {
        message = "Email already exists";
        userMessage = "This email address is already registered in our system.";
        suggestion = "Please use a different email address, or try to log in if you already have an account.";
      }
    }

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT.value())
        .error("Data Integrity Violation")
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage(userMessage)
        .suggestion(suggestion)
        .helpUrl("/api/docs/users#common-issues")
        .errorCode("USER_005")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  /**
   * Handle database access exceptions.
   */
  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ErrorResponse> handleDataAccessException(
      DataAccessException ex, WebRequest request) {
    log.error("Database access error: {}", ex.getMessage(), ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Database Error")
        .message("A database error occurred. Please try again later.")
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage("We're experiencing technical difficulties with our database.")
        .suggestion(
            "Please try again in a few minutes. If the problem continues, our technical team has been notified.")
        .helpUrl("/api/docs/status")
        .errorCode("SYS_002")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handle circuit breaker exceptions.
   */
  @ExceptionHandler(CallNotPermittedException.class)
  public ResponseEntity<ErrorResponse> handleCallNotPermittedException(
      CallNotPermittedException ex, WebRequest request) {
    log.error("Circuit breaker is open: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
        .error("Service Unavailable")
        .message("Service is temporarily unavailable. Please try again later.")
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage("We're experiencing high traffic or temporary issues with our service.")
        .suggestion("Please wait a few minutes and try again. If the problem persists, contact our support team.")
        .helpUrl("/api/docs/status")
        .errorCode("SYS_001")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * Handle service unavailable exceptions.
   */
  @ExceptionHandler(ServiceUnavailableException.class)
  public ResponseEntity<ErrorResponse> handleServiceUnavailableException(
      ServiceUnavailableException ex, WebRequest request) {
    log.error("Service unavailable: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
        .error("Service Temporarily Unavailable")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage("This service is temporarily unavailable due to high demand or maintenance.")
        .suggestion("Please wait a few minutes and try again. Our team is working to restore normal service.")
        .helpUrl("/api/docs/status")
        .errorCode("SYS_003")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * Handle JSON processing exceptions.
   */
  @ExceptionHandler(JsonProcessingException.class)
  public ResponseEntity<ErrorResponse> handleJsonProcessingException(
      JsonProcessingException ex, WebRequest request) {
    log.warn("JSON processing error: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Invalid JSON")
        .message("Invalid JSON format in request body")
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle HTTP message not readable exceptions.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {
    log.warn("HTTP message not readable: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Malformed Request")
        .message("Request body is malformed or missing")
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle method argument type mismatch exceptions.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    log.warn("Method argument type mismatch: {}", ex.getMessage());

    String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
        ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Invalid Parameter Type")
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle missing servlet request parameter exceptions.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex, WebRequest request) {
    log.warn("Missing request parameter: {}", ex.getMessage());

    String message = String.format("Required parameter '%s' is missing", ex.getParameterName());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Missing Parameter")
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle HTTP request method not supported exceptions.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex, WebRequest request) {
    log.warn("HTTP method not supported: {}", ex.getMessage());

    String message = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
        ex.getMethod(), ex.getSupportedHttpMethods());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
        .error("Method Not Allowed")
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * Handle HTTP media type not supported exceptions.
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException ex, WebRequest request) {
    log.warn("HTTP media type not supported: {}", ex.getMessage());

    String message = String.format("Media type '%s' is not supported. Supported media types: %s",
        ex.getContentType(), ex.getSupportedMediaTypes());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        .error("Unsupported Media Type")
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  /**
   * Handle no handler found exceptions.
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
      NoHandlerFoundException ex, WebRequest request) {
    log.warn("No handler found: {}", ex.getMessage());

    String message = String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Endpoint Not Found")
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle illegal argument exceptions.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.warn("Illegal argument: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Invalid Argument")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle all other exceptions.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, WebRequest request) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred. Please try again later.")
        .path(request.getDescription(false).replace("uri=", ""))
        .userMessage("Something unexpected happened on our end.")
        .suggestion(
            "Please try again in a few minutes. If the problem persists, contact our support team with the error details.")
        .helpUrl("/api/docs/support")
        .errorCode("SYS_999")
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}