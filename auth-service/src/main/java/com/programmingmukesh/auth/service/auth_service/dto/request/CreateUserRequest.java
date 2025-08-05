package com.programmingmukesh.auth.service.auth_service.dto.request;

import java.util.UUID;

import lombok.*;

import jakarta.validation.constraints.*;

/**
 * Create User Request DTO for user authentication credential creation.
 * 
 * <p>
 * This DTO represents the data required to create a new user's authentication
 * credentials.
 * It includes validation annotations to ensure data integrity and security.
 * </p>
 * 
 * <p>
 * <strong>Security Considerations:</strong>
 * </p>
 * <ul>
 * <li>Password validation ensures strong password requirements</li>
 * <li>Username validation prevents injection attacks</li>
 * <li>Email validation ensures proper format</li>
 * <li>All fields are validated for security and data integrity</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {
  /**
   * Unique username for login authentication.
   * Must be alphanumeric with optional underscores and hyphens.
   */
  @NotBlank(message = "Username cannot be blank")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
  private String username;

  /**
   * User's email address for login and notifications.
   * Must be a valid email format and unique across the system.
   */
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be in valid format")
  @Size(max = 255, message = "Email cannot exceed 255 characters")
  private String email;

  /**
   * User's password for authentication.
   * Will be hashed before storage.
   */
  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
  private String password;

  /**
   * User's first name.
   */
  @NotBlank(message = "First name cannot be blank")
  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
  private String firstName;

  /**
   * User's last name.
   */
  @NotBlank(message = "Last name cannot be blank")
  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
  private String lastName;

  /**
   * User's display name for public profiles.
   */
  @Size(max = 200, message = "Display name cannot exceed 200 characters")
  private String displayName;

  /**
   * Tenant ID for multi-tenancy support.
   * Optional field for tenant-based authentication.
   */
  private UUID tenantId;

  /**
   * Validates that the request contains minimum required information.
   * 
   * @return true if the request has minimum required information
   */
  public boolean hasMinimumRequiredInfo() {
    return username != null && !username.trim().isEmpty() &&
        email != null && !email.trim().isEmpty() &&
        password != null && !password.trim().isEmpty();
  }

  /**
   * Validates that the password meets security requirements.
   * 
   * @return true if the password meets security requirements
   */
  public boolean isPasswordSecure() {
    if (password == null || password.length() < 8) {
      return false;
    }

    boolean hasLower = password.matches(".*[a-z].*");
    boolean hasUpper = password.matches(".*[A-Z].*");
    boolean hasDigit = password.matches(".*\\d.*");
    boolean hasSpecial = password.matches(".*[@$!%*?&].*");

    return hasLower && hasUpper && hasDigit && hasSpecial;
  }

  /**
   * Validates that the username is available (not already taken).
   * This is a placeholder for actual availability checking.
   * 
   * @return true if the username appears to be available
   */
  public boolean isUsernameAvailable() {
    return username != null &&
        username.length() >= 3 &&
        username.length() <= 50 &&
        username.matches("^[a-zA-Z0-9_-]+$");
  }

  /**
   * Validates that the email is available (not already taken).
   * This is a placeholder for actual availability checking.
   * 
   * @return true if the email appears to be available
   */
  public boolean isEmailAvailable() {
    return email != null &&
        email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") &&
        email.length() <= 255;
  }
}