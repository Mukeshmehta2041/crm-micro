package com.programmingmukesh.users.service.users_service.exception;

/**
 * Exception thrown when a user is not found.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}