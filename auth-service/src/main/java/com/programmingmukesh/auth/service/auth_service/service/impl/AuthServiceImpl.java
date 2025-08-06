package com.programmingmukesh.auth.service.auth_service.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;

import com.programmingmukesh.auth.service.auth_service.client.UsersServiceClient;
import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.UserResponseDTO;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.RegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.entity.UserCredential;
import com.programmingmukesh.auth.service.auth_service.repository.UserCredentialRepository;
import com.programmingmukesh.auth.service.auth_service.service.AuthService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthService providing authentication operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserCredentialRepository userCredentialRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UsersServiceClient usersServiceClient;

  @Override
  public RegistrationResponse register(CreateUserRequest request) {
    log.info("Processing registration for user: {}", request.getUsername());

    // Validate request
    validateRegistrationRequest(request);

    // Check if user already exists
    if (userCredentialRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException(
          "Username '" + request.getUsername() + "' is already taken. Please choose a different username.");
    }

    if (userCredentialRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException(
          "Email '" + request.getEmail() + "' is already registered. Please use a different email address.");
    }

    // Create user in users-service via Feign client
    ResponseEntity<ApiResponse<UserResponseDTO>> userResponse;
    UUID userId;

    try {
      userResponse = usersServiceClient.createUser(request);

      if (!userResponse.getStatusCode().is2xxSuccessful() ||
          userResponse.getBody() == null ||
          !userResponse.getBody().isSuccess()) {

        // Extract the actual error message from users-service response
        String errorMessage = "Failed to create user in users service";
        if (userResponse.getBody() != null && userResponse.getBody().getMessage() != null) {
          errorMessage = userResponse.getBody().getMessage();
        }

        // Handle different HTTP status codes appropriately
        if (userResponse.getStatusCode().value() == 409) {
          // Conflict - user already exists
          throw new IllegalArgumentException(errorMessage);
        } else if (userResponse.getStatusCode().value() == 400) {
          // Bad request - validation error
          throw new IllegalArgumentException(errorMessage);
        } else {
          // Other errors
          throw new RuntimeException(errorMessage);
        }
      }

      // Extract user ID from the response
      userId = extractUserIdFromResponse(userResponse.getBody());

    } catch (FeignException e) {
      // Handle Feign exceptions (HTTP errors from users-service)
      log.warn("Users service returned error for user '{}': HTTP {} - {}",
          request.getUsername(), e.status(), e.getMessage());

      // Parse the error response to extract the actual message
      String errorMessage = extractErrorMessageFromFeignException(e);

      if (e.status() == 409) {
        // Conflict - user already exists
        throw new IllegalArgumentException(errorMessage);
      } else if (e.status() == 400) {
        // Bad request - validation error
        throw new IllegalArgumentException(errorMessage);
      } else {
        // Other HTTP errors
        throw new RuntimeException(errorMessage);
      }
    } catch (IllegalArgumentException e) {
      // Re-throw validation/conflict errors as-is
      throw e;
    } catch (Exception e) {
      log.warn("Users service communication failed for user '{}': {}",
          request.getUsername(), e.getMessage());
      throw new RuntimeException("User service is temporarily unavailable. Please try again later.");
    }

    // Create user credential with the user ID from users service
    UserCredential userCredential = createUserCredential(request, userId);

    // Save to database
    UserCredential savedCredential = userCredentialRepository.save(userCredential);

    log.info("User registered successfully: {}", savedCredential.getUsername());

    // Build response
    return RegistrationResponse.builder()
        .userId(savedCredential.getUserId())
        .username(savedCredential.getUsername())
        .email(savedCredential.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .displayName(request.getDisplayName())
        .emailVerified(savedCredential.getEmailVerified())
        .mfaEnabled(savedCredential.getMfaEnabled())
        .createdAt(savedCredential.getCreatedAt())
        .updatedAt(savedCredential.getUpdatedAt())
        .build();
  }

  private void validateRegistrationRequest(CreateUserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Registration request cannot be null");
    }

    if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
      throw new IllegalArgumentException("Username is required");
    }

    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("Email address is required");
    }

    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new IllegalArgumentException("Password is required");
    }

    if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
      throw new IllegalArgumentException("First name is required");
    }

    if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
      throw new IllegalArgumentException("Last name is required");
    }
  }

  private UserCredential createUserCredential(CreateUserRequest request, UUID userId) {
    UserCredential userCredential = UserCredential.builder()
        .userId(userId)
        .username(request.getUsername().toLowerCase().trim())
        .email(request.getEmail().toLowerCase().trim())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .emailVerified(false)
        .mfaEnabled(false)
        .failedLoginAttempts(0)
        .build();

    userCredential.setTenantId(request.getTenantId());
    return userCredential;
  }

  private UUID extractUserIdFromResponse(ApiResponse<UserResponseDTO> response) {
    try {
      if (response != null && response.getData() != null) {
        UserResponseDTO userData = response.getData();

        // Extract user ID from the DTO
        if (userData.getId() != null) {
          return userData.getId();
        }

        log.warn("User ID is null in response, generating new UUID");
        return UUID.randomUUID();
      }

      log.warn("Response or response data is null, generating new UUID");
      return UUID.randomUUID();

    } catch (Exception e) {
      log.error("Error extracting user ID from response: {}", e.getMessage());
      return UUID.randomUUID();
    }
  }

  private String extractErrorMessageFromFeignException(FeignException e) {
    try {
      // Try to parse the error response as JSON
      String responseBody = e.contentUTF8();
      if (responseBody != null && !responseBody.trim().isEmpty()) {
        // Simple JSON parsing to extract message field
        if (responseBody.contains("\"message\":")) {
          int messageStart = responseBody.indexOf("\"message\":") + 11;
          int messageEnd = responseBody.indexOf("\"", messageStart);
          if (messageEnd > messageStart) {
            String message = responseBody.substring(messageStart, messageEnd);
            // Unescape JSON string
            return message.replace("\\\"", "\"").replace("\\\\", "\\");
          }
        }
      }
    } catch (Exception parseException) {
      log.debug("Could not parse error response: {}", parseException.getMessage());
    }

    // Fallback error messages based on status code
    switch (e.status()) {
      case 409:
        return "A user with this information already exists. Please use different username or email address.";
      case 400:
        return "Invalid request data. Please check your input and try again.";
      case 500:
        return "Internal server error. Please try again later.";
      default:
        return "An error occurred while processing your request. Please try again.";
    }
  }
}
