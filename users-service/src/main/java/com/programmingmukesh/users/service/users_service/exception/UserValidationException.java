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

  /**
   * Creates a user-friendly UserValidationException for null or empty fields.
   */
  public static UserValidationException fieldRequired(String fieldName) {
    return new UserValidationException(fieldName + " is required and cannot be empty");
  }

  /**
   * Creates a user-friendly UserValidationException for field length validation.
   */
  public static UserValidationException fieldTooLong(String fieldName, int maxLength) {
    return new UserValidationException(fieldName + " cannot exceed " + maxLength + " characters");
  }

  /**
   * Creates a user-friendly UserValidationException for invalid email format.
   */
  public static UserValidationException invalidEmail(String email) {
    return new UserValidationException("Invalid email format: " + email + ". Please provide a valid email address.");
  }

  /**
   * Creates a user-friendly UserValidationException for invalid username format.
   */
  public static UserValidationException invalidUsername(String username) {
    return new UserValidationException("Username '" + username + "' contains invalid characters. Username should only contain letters, numbers, and underscores.");
  }

  /**
   * Creates a user-friendly UserValidationException for invalid age.
   */
  public static UserValidationException invalidAge(int age) {
    return new UserValidationException("Age must be between 13 and 120 years. Provided age: " + age);
  }
}