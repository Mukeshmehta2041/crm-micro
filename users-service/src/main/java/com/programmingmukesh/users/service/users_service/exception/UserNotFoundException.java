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

  /**
   * Creates a user-friendly UserNotFoundException for when a user is not found by
   * ID.
   */
  public static UserNotFoundException userNotFoundById(String userId) {
    return new UserNotFoundException("User not found with ID: " + userId);
  }

  /**
   * Creates a user-friendly UserNotFoundException for when a user is not found by
   * username.
   */
  public static UserNotFoundException userNotFoundByUsername(String username) {
    return new UserNotFoundException("User not found with username: " + username);
  }

  /**
   * Creates a user-friendly UserNotFoundException for when a user is not found by
   * email.
   */
  public static UserNotFoundException userNotFoundByEmail(String email) {
    return new UserNotFoundException("User not found with email: " + email);
  }
}