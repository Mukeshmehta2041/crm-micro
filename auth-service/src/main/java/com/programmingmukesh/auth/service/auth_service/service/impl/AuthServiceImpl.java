package com.programmingmukesh.auth.service.auth_service.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.RegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.entity.UserCredential;
import com.programmingmukesh.auth.service.auth_service.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the Authentication Service.
 * 
 * <p>
 * This service handles user registration by:
 * </p>
 * <ul>
 * <li>Validating user input and checking uniqueness</li>
 * <li>Creating user profile in the users service</li>
 * <li>Creating authentication credentials</li>
 * <li>Hashing passwords securely</li>
 * <li>Managing security settings and audit logging</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private HttpServletRequest httpRequest;

  @Value("${users.service.url:http://localhost:8082}")
  private String usersServiceUrl;

  @Override
  public RegistrationResponse register(CreateUserRequest request) {
    // Step 1: Extract client information
    String clientIpAddress = getClientIpAddress(httpRequest);
    String userAgent = httpRequest.getHeader("User-Agent");

    // Step 2: Log registration request
    log.info("Registration attempt for email: {} from IP: {}", request.getEmail(), clientIpAddress);

    try {
      // Step 3: Validate if user/email already exists
      validateUserUniqueness(request);

      // Step 4: Validate password
      validatePassword(request.getPassword());

      // Step 5: Main registration logic
      return performRegistration(request, clientIpAddress, userAgent);

    } catch (Exception e) {
      // Step 6: Handle exceptions
      log.error("Registration failed for email: {} from IP: {}", request.getEmail(), clientIpAddress, e);
      logSecurityEvent("REGISTRATION_FAILED", request.getEmail(), clientIpAddress, e.getMessage());
      throw new RuntimeException("Registration failed: " + e.getMessage(), e);
    }
  }

  /**
   * Validates that the user's email and username are unique.
   * 
   * @param request the registration request
   * @throws IllegalArgumentException if email or username already exists
   */
  private void validateUserUniqueness(CreateUserRequest request) {
    // TODO: Implement actual database checks
    // For now, we'll use the validation methods in the request DTO
    if (!request.isEmailAvailable()) {
      throw new IllegalArgumentException("Email address is already registered");
    }

    if (!request.isUsernameAvailable()) {
      throw new IllegalArgumentException("Username is already taken");
    }
  }

  /**
   * Validates the password strength.
   * 
   * @param password the password to validate
   * @throws IllegalArgumentException if password doesn't meet requirements
   */
  private void validatePassword(String password) {
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters long");
    }

    boolean hasLower = password.matches(".*[a-z].*");
    boolean hasUpper = password.matches(".*[A-Z].*");
    boolean hasDigit = password.matches(".*\\d.*");
    boolean hasSpecial = password.matches(".*[@$!%*?&].*");

    if (!hasLower || !hasUpper || !hasDigit || !hasSpecial) {
      throw new IllegalArgumentException(
          "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
    }
  }

  /**
   * Performs the main registration logic.
   * 
   * @param request         the registration request
   * @param clientIpAddress the client's IP address
   * @param userAgent       the client's user agent
   * @return RegistrationResponse with the registration result
   */
  private RegistrationResponse performRegistration(CreateUserRequest request, String clientIpAddress,
      String userAgent) {
    // Step 5.1: Create user in external User Management Service
    UUID userId = createUserInExternalService(request);

    // Step 5.2: Save user credentials in the local database
    UserCredential userCredential = saveUserCredentials(request, userId);

    // Step 5.3: Generate email verification token (placeholder)
    String verificationTokenId = generateEmailVerificationToken(userId);

    // Step 5.4: Log successful registration event
    logSecurityEvent("REGISTRATION_SUCCESS", request.getEmail(), clientIpAddress, "User registered successfully");

    // Step 5.5: Return success response
    return buildRegistrationResponse(request, userId, userCredential, verificationTokenId);
  }

  /**
   * Creates a user in the external User Management Service.
   * 
   * @param request the registration request
   * @return the created user ID
   * @throws RuntimeException if user creation fails
   */
  private UUID createUserInExternalService(CreateUserRequest request) {
    try {
      log.info("Creating user profile in users service for username: {}", request.getUsername());

      // Prepare user creation request for users service
      UserCreationRequest userRequest = UserCreationRequest.builder()
          .username(request.getUsername())
          .firstName(request.getFirstName())
          .lastName(request.getLastName())
          .email(request.getEmail())
          .displayName(request.getDisplayName())
          .build();

      // Set up HTTP headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Create HTTP entity
      HttpEntity<UserCreationRequest> entity = new HttpEntity<>(userRequest, headers);

      // Call users service
      String usersServiceEndpoint = usersServiceUrl + "/api/v1/users";
      ApiResponseWrapper userResponse = restTemplate.postForObject(
          usersServiceEndpoint,
          entity,
          ApiResponseWrapper.class);

      if (userResponse == null || !userResponse.isSuccess() || userResponse.getData() == null || userResponse.getData().getId() == null) {
        throw new RuntimeException("Failed to create user profile in users service");
      }

      log.info("User profile created successfully with ID: {}", userResponse.getData().getId());
      return userResponse.getData().getId();

    } catch (Exception e) {
      log.error("Failed to create user profile in users service", e);
      throw new RuntimeException("Failed to create user profile: " + e.getMessage(), e);
    }
  }

  /**
   * Saves user credentials in the local database.
   * 
   * @param request the registration request
   * @param userId  the user ID
   * @return the created user credential
   */
  private UserCredential saveUserCredentials(CreateUserRequest request, UUID userId) {
    log.info("Creating authentication credentials for userId: {}", userId);

    // Hash the password securely
    String hashedPassword = passwordEncoder.encode(request.getPassword());

    UserCredential userCredential = new UserCredential();
    userCredential.setUserId(userId);
    userCredential.setUsername(request.getUsername());
    userCredential.setEmail(request.getEmail());
    userCredential.setPasswordHash(hashedPassword);
    userCredential.setEmailVerified(false);
    userCredential.setMfaEnabled(false);
    userCredential.setFailedLoginAttempts(0);

    // TODO: Save to database using repository
    // userCredentialRepository.save(userCredential);

    log.info("Authentication credentials created successfully for userId: {}", userId);
    return userCredential;
  }

  /**
   * Generates an email verification token for the user.
   * 
   * @param userId the user ID
   * @return the verification token ID
   */
  private String generateEmailVerificationToken(UUID userId) {
    // TODO: Implement actual email verification token generation
    log.info("Generating email verification token for userId: {}", userId);
    return UUID.randomUUID().toString();
  }

  /**
   * Builds the registration response.
   * 
   * @param request             the registration request
   * @param userId              the user ID
   * @param userCredential      the user credential
   * @param verificationTokenId the verification token ID
   * @return the registration response
   */
  private RegistrationResponse buildRegistrationResponse(CreateUserRequest request, UUID userId,
      UserCredential userCredential, String verificationTokenId) {
    return RegistrationResponse.builder()
        .userId(userId)
        .username(request.getUsername())
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .displayName(request.getDisplayName())
        .emailVerified(false)
        .mfaEnabled(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .message("User registered successfully")
        .status("SUCCESS")
        .metadata("Verification token: " + verificationTokenId)
        .build();
  }

  /**
   * Extracts the client IP address from the HTTP request.
   * 
   * @param request the HTTP request
   * @return the client IP address
   */
  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }

  /**
   * Logs security events for audit purposes.
   * 
   * @param eventType the type of security event
   * @param email     the user's email
   * @param ipAddress the client IP address
   * @param details   additional event details
   */
  private void logSecurityEvent(String eventType, String email, String ipAddress, String details) {
    log.info("SECURITY_EVENT: {} - Email: {} - IP: {} - Details: {}", eventType, email, ipAddress, details);
    // TODO: Implement actual audit logging service
  }

  /**
   * Internal DTO for user creation request to users service.
   */
  private static class UserCreationRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String displayName;

    // Getters and setters
    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private UserCreationRequest request = new UserCreationRequest();

      public Builder username(String username) {
        request.username = username;
        return this;
      }

      public Builder firstName(String firstName) {
        request.firstName = firstName;
        return this;
      }

      public Builder lastName(String lastName) {
        request.lastName = lastName;
        return this;
      }

      public Builder email(String email) {
        request.email = email;
        return this;
      }

      public Builder displayName(String displayName) {
        request.displayName = displayName;
        return this;
      }

      public UserCreationRequest build() {
        return request;
      }
    }
  }

  /**
   * Internal DTO for wrapped API response from users service.
   */
  private static class ApiResponseWrapper {
    private boolean success;
    private UserData data;
    private String message;
    private String error;

    // Getters and setters
    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    public UserData getData() {
      return data;
    }

    public void setData(UserData data) {
      this.data = data;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getError() {
      return error;
    }

    public void setError(String error) {
      this.error = error;
    }
  }

  /**
   * Internal DTO for user data from users service.
   */
  private static class UserData {
    private UUID id;
    private String username;
    private String email;

    // Getters and setters
    public UUID getId() {
      return id;
    }

    public void setId(UUID id) {
      this.id = id;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }
}
