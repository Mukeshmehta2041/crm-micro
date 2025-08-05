package com.programmingmukesh.users.service.users_service.exception;

/**
 * Exception thrown when user validation fails.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class UserValidationException extends RuntimeException {

  public UserValidationException(String message) {
    super(message);
  }

  public UserValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}