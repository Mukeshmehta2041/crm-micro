package com.programmingmukesh.users.service.users_service.controller;

import com.programmingmukesh.users.service.users_service.dto.ApiError;
import com.programmingmukesh.users.service.users_service.dto.ApiResponse;
import com.programmingmukesh.users.service.users_service.dto.ErrorDetail;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.hibernate.LazyInitializationException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RestControllerAdvice
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public ResponseEntity<ApiResponse<ApiError>> handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
    Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    String errorMessage = "An unexpected error occurred";
    String errorPath = path != null ? path.toString() : "Unknown";

    if (status != null) {
      int statusCode = Integer.parseInt(status.toString());
      httpStatus = HttpStatus.valueOf(statusCode);

      switch (statusCode) {
        case 404:
          if (errorPath.contains("swagger-ui") || errorPath.contains("api-docs")) {
            errorMessage = "API Documentation not found. Please use /swagger-ui.html to access the Swagger UI.";
          } else {
            errorMessage = "The requested resource was not found";
          }
          break;
        case 403:
          errorMessage = "Access denied. You don't have permission to access this resource";
          break;
        case 400:
          errorMessage = "Bad request. Please check your request parameters";
          break;
        case 500:
          errorMessage = "Internal server error. Please try again later";
          break;
        default:
          errorMessage = message != null ? message.toString() : "An error occurred";
      }
    }

    ErrorDetail errorDetail = ErrorDetail.builder()
        .field("path")
        .value(errorPath)
        .message(errorMessage)
        .timestamp(LocalDateTime.now())
        .build();

    ApiError apiError = new ApiError(
        "HTTP_ERROR_" + httpStatus.value(),
        errorMessage,
        "path",
        errorPath,
        httpStatus.getReasonPhrase(),
        null);

    return ResponseEntity.status(httpStatus)
        .body(ApiResponse.<ApiError>builder()
            .success(false)
            .data(apiError)
            .message("Error occurred while processing your request")
            .timestamp(LocalDateTime.now())
            .build());
  }

  @ExceptionHandler(LazyInitializationException.class)
  public ResponseEntity<ApiResponse<ApiError>> handleLazyInitializationException(LazyInitializationException ex) {
    ErrorDetail errorDetail = ErrorDetail.builder()
        .field("entity")
        .value("lazy-loaded-collection")
        .message("Failed to load entity relationships. Please try again or contact support.")
        .timestamp(LocalDateTime.now())
        .build();

    ApiError apiError = new ApiError(
        "LAZY_INITIALIZATION_ERROR",
        "Entity relationship loading failed",
        "entity",
        "lazy-loaded-collection",
        "LazyInitializationException",
        null);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.<ApiError>builder()
            .success(false)
            .data(apiError)
            .message("Failed to load complete user data. Please try again.")
            .timestamp(LocalDateTime.now())
            .build());
  }
}
