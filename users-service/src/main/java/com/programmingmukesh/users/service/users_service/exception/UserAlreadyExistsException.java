package com.programmingmukesh.users.service.users_service.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }

  public UserAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}