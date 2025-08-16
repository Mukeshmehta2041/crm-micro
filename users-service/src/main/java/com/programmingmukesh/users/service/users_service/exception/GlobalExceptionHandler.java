package com.programmingmukesh.users.service.users_service.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.programmingmukesh.users.service.users_service.dto.ApiResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Comprehensive Global Exception Handler for the Users Service.
 * 
 * <p>
 * This handler provides centralized exception handling across the whole
 * application.
 * It converts exceptions into standardized, user-friendly API responses with
 * appropriate HTTP status codes.
 * </p>
 * 
 * <p>
 * Features:
 * </p>
 * <ul>
 * <li>Detailed validation error messages with field-level information</li>
 * <li>Business logic error handling with contextual messages</li>
 * <li>System error handling with appropriate logging</li>
 * <li>Security-conscious error responses (no sensitive data exposure)</li>
 * <li>Consistent error response format</li>
 * <li>Internationalization support ready</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles validation errors from @Valid annotations with detailed field-level
   * errors.
   * 
   * @param ex      the validation exception
   * @param request the web request
   * @return standardized error response with field details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    log.warn("Validation error occurred for request: {}", request.getDescription(false));

    Map<String, String> fieldErrors = new HashMap<>();
    List<String> globalErrors = new ArrayList<>();

    // Process field errors
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      String fieldName = error.getField();
      String errorMessage = error.getDefaultMessage();
      String rejectedValue = error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null";

      fieldErrors.put(fieldName, errorMessage);
      log.debug("Field validation error - Field: {}, Value: {}, Message: {}",
          fieldName, rejectedValue, errorMessage);
    });

    // Process global errors
    ex.getBindingResult().getGlobalErrors().forEach(error -> {
      globalErrors.add(error.getDefaultMessage());
      log.debug("Global validation error: {}", error.getDefaultMessage());
    });

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("fieldErrors", fieldErrors);
    if (!globalErrors.isEmpty()) {
      errorData.put("globalErrors", globalErrors);
    }
    errorData.put("errorCount", fieldErrors.size() + globalErrors.size());

    String userFriendlyMessage = createUserFriendlyValidationMessage(fieldErrors, globalErrors);

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("VALIDATION_FAILED")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles constraint violation exceptions from method-level validation.
   * 
   * @param ex      the constraint violation exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    log.warn("Constraint violation occurred for request: {}", request.getDescription(false));

    Map<String, String> violations = new HashMap<>();

    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String propertyPath = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      violations.put(propertyPath, message);

      log.debug("Constraint violation - Property: {}, Value: {}, Message: {}",
          propertyPath, violation.getInvalidValue(), message);
    }

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("violations", violations);
    errorData.put("violationCount", violations.size());

    String userFriendlyMessage = "Please correct the following issues: " +
        violations.values().stream().collect(Collectors.joining(", "));

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("CONSTRAINT_VIOLATION")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles JSON parsing and format errors.
   * 
   * @param ex      the message not readable exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {

    log.warn("Invalid JSON format in request: {}", request.getDescription(false));

    Map<String, Object> errorData = new HashMap<>();
    String userFriendlyMessage = "Invalid request format. Please check your JSON syntax.";

    // Handle specific JSON format errors
    Throwable cause = ex.getCause();
    if (cause instanceof InvalidFormatException) {
      InvalidFormatException formatEx = (InvalidFormatException) cause;
      String fieldName = formatEx.getPath().stream()
          .map(JsonMappingException.Reference::getFieldName)
          .collect(Collectors.joining("."));

      errorData.put("field", fieldName);
      errorData.put("invalidValue", formatEx.getValue());
      errorData.put("expectedType", formatEx.getTargetType().getSimpleName());

      userFriendlyMessage = String.format("Invalid value '%s' for field '%s'. Expected type: %s",
          formatEx.getValue(), fieldName, formatEx.getTargetType().getSimpleName());
    }

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("INVALID_REQUEST_FORMAT")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles missing request parameters.
   * 
   * @param ex      the missing parameter exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex, WebRequest request) {

    log.warn("Missing required parameter: {} for request: {}", ex.getParameterName(), request.getDescription(false));

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("missingParameter", ex.getParameterName());
    errorData.put("parameterType", ex.getParameterType());

    String userFriendlyMessage = String.format("Required parameter '%s' is missing", ex.getParameterName());

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("MISSING_PARAMETER")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles method argument type mismatch errors.
   * 
   * @param ex      the type mismatch exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {

    log.warn("Type mismatch for parameter: {} with value: {}", ex.getName(), ex.getValue());

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("parameter", ex.getName());
    errorData.put("providedValue", ex.getValue());
    errorData.put("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

    String userFriendlyMessage = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
        ex.getValue(), ex.getName(),
        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("INVALID_PARAMETER_TYPE")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles unsupported HTTP methods.
   * 
   * @param ex      the method not supported exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex, WebRequest request) {

    log.warn("Unsupported HTTP method: {} for request: {}", ex.getMethod(), request.getDescription(false));

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("method", ex.getMethod());
    errorData.put("supportedMethods", ex.getSupportedMethods());

    String userFriendlyMessage = String.format(
        "HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
        ex.getMethod(), String.join(", ", ex.getSupportedMethods()));

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("METHOD_NOT_SUPPORTED")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * Handles unsupported media types.
   * 
   * @param ex      the media type not supported exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException ex, WebRequest request) {

    log.warn("Unsupported media type: {} for request: {}", ex.getContentType(), request.getDescription(false));

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("providedMediaType", ex.getContentType() != null ? ex.getContentType().toString() : "unknown");
    errorData.put("supportedMediaTypes", ex.getSupportedMediaTypes().stream()
        .map(Object::toString).collect(Collectors.toList()));

    String userFriendlyMessage = String.format("Media type '%s' is not supported. Supported types: %s",
        ex.getContentType(),
        ex.getSupportedMediaTypes().stream()
            .map(Object::toString).collect(Collectors.joining(", ")));

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("UNSUPPORTED_MEDIA_TYPE")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  /**
   * Handles endpoint not found errors.
   * 
   * @param ex      the no handler found exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleNoHandlerFoundException(
      NoHandlerFoundException ex, WebRequest request) {

    log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("method", ex.getHttpMethod());
    errorData.put("url", ex.getRequestURL());

    String userFriendlyMessage = String.format("Endpoint '%s %s' not found. Please check the URL and HTTP method.",
        ex.getHttpMethod(), ex.getRequestURL());

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("ENDPOINT_NOT_FOUND")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles database constraint violations and data integrity issues.
   * 
   * @param ex      the data integrity violation exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {

    log.warn("Data integrity violation: {}", ex.getMessage());

    Map<String, Object> errorData = new HashMap<>();
    String userFriendlyMessage = "Data integrity violation occurred.";

    // Parse common constraint violations
    String message = ex.getMessage().toLowerCase();
    if (message.contains("unique") || message.contains("duplicate")) {
      if (message.contains("email")) {
        userFriendlyMessage = "An account with this email address already exists.";
        errorData.put("conflictField", "email");
      } else if (message.contains("username")) {
        userFriendlyMessage = "This username is already taken. Please choose a different one.";
        errorData.put("conflictField", "username");
      } else {
        userFriendlyMessage = "This information already exists in the system. Please use different values.";
      }
      errorData.put("violationType", "UNIQUE_CONSTRAINT");
    } else if (message.contains("foreign key") || message.contains("reference")) {
      userFriendlyMessage = "Referenced data does not exist. Please check your input values.";
      errorData.put("violationType", "FOREIGN_KEY_CONSTRAINT");
    } else if (message.contains("not null") || message.contains("null")) {
      userFriendlyMessage = "Required information is missing. Please provide all mandatory fields.";
      errorData.put("violationType", "NOT_NULL_CONSTRAINT");
    }

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("DATA_INTEGRITY_VIOLATION")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  /**
   * Handles user not found exceptions.
   * 
   * @param ex      the user not found exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleUserNotFoundException(
      UserNotFoundException ex, WebRequest request) {

    log.warn("User not found: {}", ex.getMessage());

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("resource", "User");
    errorData.put("action", "FIND");

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("USER_NOT_FOUND")
        .message("The requested user could not be found. Please verify the user ID or search criteria.")
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles user already exists exceptions.
   * 
   * @param ex      the user already exists exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleUserAlreadyExistsException(
      UserAlreadyExistsException ex, WebRequest request) {

    log.warn("User already exists: {}", ex.getMessage());

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("resource", "User");
    errorData.put("action", "CREATE");
    errorData.put("conflictReason", "DUPLICATE_ENTRY");

    String userFriendlyMessage = "A user with this information already exists. " +
        "Please use different username or email address.";

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("USER_ALREADY_EXISTS")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  /**
   * Handles user validation exceptions.
   * 
   * @param ex      the user validation exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(UserValidationException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleUserValidationException(
      UserValidationException ex, WebRequest request) {

    log.warn("User validation error: {}", ex.getMessage());

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("resource", "User");
    errorData.put("validationType", "BUSINESS_RULE");

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("USER_VALIDATION_FAILED")
        .message("User data validation failed: " + ex.getMessage())
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles illegal argument exceptions.
   * 
   * @param ex      the illegal argument exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {

    log.warn("Illegal argument: {}", ex.getMessage());

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("argumentType", "INVALID");

    String userFriendlyMessage = "Invalid input provided. Please check your request parameters and try again.";

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("INVALID_ARGUMENT")
        .message(userFriendlyMessage)
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles security-related exceptions.
   * 
   * @param ex      the security exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleSecurityException(
      SecurityException ex, WebRequest request) {

    log.warn("Security exception: {}", ex.getMessage());

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("securityIssue", true);

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("ACCESS_DENIED")
        .message("Access denied. You don't have permission to perform this action.")
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  /**
   * Handles all other runtime exceptions.
   * 
   * @param ex      the runtime exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleRuntimeException(
      RuntimeException ex, WebRequest request) {

    log.error("Runtime exception occurred: {}", ex.getMessage(), ex);

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("errorType", "RUNTIME_ERROR");
    errorData.put("requestId", generateRequestId());

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("INTERNAL_SERVER_ERROR")
        .message("An unexpected error occurred. Our team has been notified. Please try again later.")
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles all other exceptions (fallback handler).
   * 
   * @param ex      the exception
   * @param request the web request
   * @return standardized error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Map<String, Object>>> handleGenericException(
      Exception ex, WebRequest request) {

    log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("errorType", "UNEXPECTED_ERROR");
    errorData.put("requestId", generateRequestId());

    ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
        .success(false)
        .error("INTERNAL_SERVER_ERROR")
        .message("An unexpected system error occurred. Our team has been notified. Please try again later.")
        .data(errorData)
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Creates a user-friendly validation message from field and global errors.
   * 
   * @param fieldErrors  map of field errors
   * @param globalErrors list of global errors
   * @return user-friendly message
   */
  private String createUserFriendlyValidationMessage(Map<String, String> fieldErrors, List<String> globalErrors) {
    StringBuilder message = new StringBuilder("Please correct the following issues:\n");

    fieldErrors.forEach((field, error) -> {
      String friendlyFieldName = convertFieldNameToFriendly(field);
      message.append("• ").append(friendlyFieldName).append(": ").append(error).append("\n");
    });

    globalErrors.forEach(error -> {
      message.append("• ").append(error).append("\n");
    });

    return message.toString().trim();
  }

  /**
   * Converts technical field names to user-friendly names.
   * 
   * @param fieldName the technical field name
   * @return user-friendly field name
   */
  private String convertFieldNameToFriendly(String fieldName) {
    return switch (fieldName) {
      case "firstName" -> "First Name";
      case "lastName" -> "Last Name";
      case "middleName" -> "Middle Name";
      case "displayName" -> "Display Name";
      case "phoneNumber" -> "Phone Number";
      case "workPhone" -> "Work Phone";
      case "mobilePhone" -> "Mobile Phone";
      case "birthDate" -> "Date of Birth";
      case "hireDate" -> "Hire Date";
      case "jobTitle" -> "Job Title";
      case "employeeId" -> "Employee ID";
      case "costCenter" -> "Cost Center";
      case "officeLocation" -> "Office Location";
      case "profileImageUrl" -> "Profile Image URL";
      case "websiteUrl" -> "Website URL";
      case "linkedinUrl" -> "LinkedIn URL";
      case "twitterHandle" -> "Twitter Handle";
      case "addressLine1" -> "Address Line 1";
      case "addressLine2" -> "Address Line 2";
      case "stateProvince" -> "State/Province";
      case "postalCode" -> "Postal Code";
      case "dateFormat" -> "Date Format";
      case "timeFormat" -> "Time Format";
      case "themePreference" -> "Theme Preference";
      case "currencyPreference" -> "Currency Preference";
      case "managerId" -> "Manager ID";
      case "teamId" -> "Team ID";
      case "workingHoursStart" -> "Working Hours Start";
      case "workingHoursEnd" -> "Working Hours End";
      case "workingDays" -> "Working Days";
      case "spokenLanguages" -> "Spoken Languages";
      case "emergencyContactName" -> "Emergency Contact Name";
      case "emergencyContactPhone" -> "Emergency Contact Phone";
      case "emergencyContactRelationship" -> "Emergency Contact Relationship";
      case "customFields" -> "Custom Fields";
      case "gdprConsentGiven" -> "GDPR Consent";
      case "marketingConsentGiven" -> "Marketing Consent";
      case "profileVisibility" -> "Profile Visibility";
      case "activityVisibility" -> "Activity Visibility";
      case "emailVisibility" -> "Email Visibility";
      case "phoneVisibility" -> "Phone Visibility";
      default -> fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");
    };
  }

  /**
   * Generates a unique request ID for error tracking.
   * 
   * @return unique request ID
   */
  private String generateRequestId() {
    return "REQ-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
  }
}