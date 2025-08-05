package com.programmingmukesh.auth.service.auth_service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized API response wrapper.
 * 
 * <p>
 * This class provides a consistent response format for all API endpoints.
 * It includes success/error status, data, message, and timestamp.
 * </p>
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
public class ApiResponse<T> {

  /**
   * Indicates if the request was successful.
   */
  private boolean success;

  /**
   * The response data.
   */
  private T data;

  /**
   * The response message.
   */
  private String message;

  /**
   * The error message (if any).
   */
  private String error;

  /**
   * The timestamp of the response.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;

  /**
   * Creates a successful response.
   * 
   * @param data the response data
   * @param <T>  the data type
   * @return the API response
   */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a successful response with message.
   * 
   * @param data    the response data
   * @param message the success message
   * @param <T>     the data type
   * @return the API response
   */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response.
   * 
   * @param error   the error message
   * @param details the error details
   * @param <T>     the data type
   * @return the API response
   */
  public static <T> ApiResponse<T> error(String error, String details) {
    return ApiResponse.<T>builder()
        .success(false)
        .error(error)
        .message(details)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response with data.
   * 
   * @param error   the error message
   * @param details the error details
   * @param data    the error data
   * @param <T>     the data type
   * @return the API response
   */
  public static <T> ApiResponse<T> error(String error, String details, T data) {
    return ApiResponse.<T>builder()
        .success(false)
        .error(error)
        .message(details)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }
}