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

  /**
   * Creates a user-friendly UserAlreadyExistsException for when a username is already taken.
   */
  public static UserAlreadyExistsException usernameAlreadyExists(String username) {
    return new UserAlreadyExistsException("Username '" + username + "' is already taken. Please choose a different username.");
  }

  /**
   * Creates a user-friendly UserAlreadyExistsException for when an email is already registered.
   */
  public static UserAlreadyExistsException emailAlreadyExists(String email) {
    return new UserAlreadyExistsException("Email '" + email + "' is already registered. Please use a different email address or try to log in.");
  }

  /**
   * Creates a user-friendly UserAlreadyExistsException for when both username and email are already taken.
   */
  public static UserAlreadyExistsException userAlreadyExists(String username, String email) {
    return new UserAlreadyExistsException("A user with username '" + username + "' or email '" + email + "' already exists. Please use different credentials.");
  }
}