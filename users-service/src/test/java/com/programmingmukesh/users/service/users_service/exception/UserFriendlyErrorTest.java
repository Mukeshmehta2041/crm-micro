package com.programmingmukesh.users.service.users_service.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to demonstrate user-friendly error messages.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class UserFriendlyErrorTest {

  @Test
  public void testUserNotFoundExceptionMessages() {
    // Test user-friendly exception messages
    UserNotFoundException exception1 = UserNotFoundException.userNotFoundById("123");
    assertEquals("User not found with ID: 123", exception1.getMessage());

    UserNotFoundException exception2 = UserNotFoundException.userNotFoundByUsername("john_doe");
    assertEquals("User not found with username: john_doe", exception2.getMessage());

    UserNotFoundException exception3 = UserNotFoundException.userNotFoundByEmail("john@example.com");
    assertEquals("User not found with email: john@example.com", exception3.getMessage());
  }

  @Test
  public void testUserAlreadyExistsExceptionMessages() {
    // Test user-friendly exception messages
    UserAlreadyExistsException exception1 = UserAlreadyExistsException.usernameAlreadyExists("john_doe");
    assertEquals("Username 'john_doe' is already taken. Please choose a different username.", exception1.getMessage());

    UserAlreadyExistsException exception2 = UserAlreadyExistsException.emailAlreadyExists("john@example.com");
    assertEquals(
        "Email 'john@example.com' is already registered. Please use a different email address or try to log in.",
        exception2.getMessage());

    UserAlreadyExistsException exception3 = UserAlreadyExistsException.userAlreadyExists("john_doe",
        "john@example.com");
    assertEquals(
        "A user with username 'john_doe' or email 'john@example.com' already exists. Please use different credentials.",
        exception3.getMessage());
  }

  @Test
  public void testUserValidationExceptionMessages() {
    // Test user-friendly exception messages
    UserValidationException exception1 = UserValidationException.fieldRequired("Username");
    assertEquals("Username is required and cannot be empty", exception1.getMessage());

    UserValidationException exception2 = UserValidationException.fieldTooLong("Username", 50);
    assertEquals("Username cannot exceed 50 characters", exception2.getMessage());

    UserValidationException exception3 = UserValidationException.invalidEmail("invalid-email");
    assertEquals("Invalid email format: invalid-email. Please provide a valid email address.", exception3.getMessage());

    UserValidationException exception4 = UserValidationException.invalidUsername("user@name");
    assertEquals(
        "Username 'user@name' contains invalid characters. Username should only contain letters, numbers, and underscores.",
        exception4.getMessage());

    UserValidationException exception5 = UserValidationException.invalidAge(5);
    assertEquals("Age must be between 13 and 120 years. Provided age: 5", exception5.getMessage());
  }

  @Test
  public void testServiceUnavailableExceptionMessage() {
    // Test user-friendly service unavailable message
    ServiceUnavailableException exception = new ServiceUnavailableException(
        "We're currently experiencing high demand. Your user creation request couldn't be processed at this time. Please try again in a few minutes.");
    assertTrue(exception.getMessage().contains("high demand"));
    assertTrue(exception.getMessage().contains("try again"));
  }
}
