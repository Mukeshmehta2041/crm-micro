package com.programmingmukesh.users.service.users_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response structure for the User Service.
 * Provides user-friendly error messages and helpful suggestions.
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
public class ErrorResponse {

  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private Map<String, String> fieldErrors;
  private String traceId;

  // User-friendly fields
  private String userMessage;
  private String suggestion;
  private String helpUrl;
  private String errorCode;
}