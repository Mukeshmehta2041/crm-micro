package com.programmingmukesh.auth.service.auth_service.dto.request;

import lombok.*;

import jakarta.validation.constraints.*;

/**
 * Complete Registration Request DTO for the integrated signup flow.
 * 
 * <p>
 * This DTO represents all the data required for the complete registration
 * process
 * that creates a Tenant, User, and Auth credentials in the proper sequence.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteRegistrationRequest {

  // User Information
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
   * User's email address (unique across the system).
   */
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be in valid format")
  @Size(max = 255, message = "Email cannot exceed 255 characters")
  private String email;

  /**
   * User's password for authentication.
   */
  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#\\-_+=])[A-Za-z\\d@$!%*?&#\\-_+=]+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
  private String password;

  /**
   * Username for authentication (generated from email if not provided).
   */
  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
  private String username;

  // Company/Tenant Information
  /**
   * Company or organization name.
   */
  @NotBlank(message = "Company name cannot be blank")
  @Size(min = 1, max = 255, message = "Company name must be between 1 and 255 characters")
  private String companyName;

  /**
   * User's phone number (optional).
   */
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Phone number can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Phone number cannot exceed 20 characters")
  private String phoneNumber;

  /**
   * User's job title or position (optional).
   */
  @Size(max = 150, message = "Job title cannot exceed 150 characters")
  private String jobTitle;

  /**
   * User's department (optional).
   */
  @Size(max = 100, message = "Department cannot exceed 100 characters")
  private String department;

  /**
   * User's timezone (optional, defaults to UTC).
   */
  @Size(max = 50, message = "Timezone cannot exceed 50 characters")
  private String timezone = "UTC";

  /**
   * User's preferred language (optional, defaults to en).
   */
  @Size(max = 10, message = "Language cannot exceed 10 characters")
  private String language = "en";

  /**
   * Marketing consent flag.
   */
  private Boolean marketingConsent = false;

  /**
   * Terms and conditions acceptance flag.
   */
  @NotNull(message = "Terms and conditions must be accepted")
  @AssertTrue(message = "You must accept the terms and conditions")
  private Boolean acceptTerms;

  /**
   * Privacy policy acceptance flag.
   */
  @NotNull(message = "Privacy policy must be accepted")
  @AssertTrue(message = "You must accept the privacy policy")
  private Boolean acceptPrivacy;

  /**
   * Generates username from email if not provided.
   * 
   * @return the username
   */
  public String getUsername() {
    if (username == null || username.trim().isEmpty()) {
      return generateUsernameFromEmail(email);
    }
    return username;
  }

  /**
   * Gets the full name of the user.
   * 
   * @return the full name
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * Validates that the request contains all required information.
   * 
   * @return true if the request is valid
   */
  public boolean isValid() {
    return firstName != null && !firstName.trim().isEmpty() &&
        lastName != null && !lastName.trim().isEmpty() &&
        email != null && !email.trim().isEmpty() &&
        password != null && !password.trim().isEmpty() &&
        companyName != null && !companyName.trim().isEmpty() &&
        Boolean.TRUE.equals(acceptTerms) &&
        Boolean.TRUE.equals(acceptPrivacy);
  }

  /**
   * Generates a username from email address.
   * 
   * @param email the email address
   * @return the generated username
   */
  private String generateUsernameFromEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return null;
    }

    String username = email.substring(0, email.indexOf('@'))
        .replaceAll("[^a-zA-Z0-9._-]", "")
        .toLowerCase();

    // Ensure it starts with a letter or number
    if (username.length() > 0 && !Character.isLetterOrDigit(username.charAt(0))) {
      username = "user" + username;
    }

    // Limit length
    if (username.length() > 50) {
      username = username.substring(0, 50);
    }

    // Ensure minimum length
    if (username.length() < 3) {
      username = username + "123";
    }

    return username;
  }

  /**
   * Creates a CreateUserRequest from this registration request.
   * 
   * @param tenantId the tenant ID to associate with the user
   * @return the create user request
   */
  public com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest toCreateUserRequest(
      java.util.UUID tenantId) {
    return com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest.builder()
        .username(getUsername())
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .phoneNumber(phoneNumber)
        .jobTitle(jobTitle)
        .department(department)
        .company(companyName)
        .timezone(timezone)
        .language(language)
        .tenantId(tenantId)
        .marketingConsentGiven(marketingConsent)
        .gdprConsentGiven(true) // Required for registration
        .build();
  }

  /**
   * Creates a CreateTenantRequest from this registration request.
   * 
   * @return the create tenant request
   */
  public CreateTenantRequest toCreateTenantRequest() {
    return CreateTenantRequest.fromCompanyAndEmail(companyName, email);
  }
}
