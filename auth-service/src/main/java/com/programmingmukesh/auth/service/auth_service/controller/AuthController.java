package com.programmingmukesh.auth.service.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.RegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication Controller for user authentication operations.
 * 
 * <p>
 * This controller provides REST endpoints for:
 * </p>
 * <ul>
 * <li>User registration and account creation</li>
 * <li>User login and authentication</li>
 * <li>Password management</li>
 * <li>Multi-factor authentication</li>
 * <li>Account security operations</li>
 * </ul>
 * 
 * <p>
 * <strong>API Endpoints:</strong>
 * </p>
 * <ul>
 * <li>POST /api/v1/auth/register - Register a new user</li>
 * <li>POST /api/v1/auth/login - User login (planned)</li>
 * <li>POST /api/v1/auth/logout - User logout (planned)</li>
 * <li>POST /api/v1/auth/refresh - Refresh token (planned)</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Validated
public class AuthController {

  @Autowired
  private AuthService authService;

  /**
   * Registers a new user with authentication credentials.
   * 
   * <p>
   * This endpoint:
   * </p>
   * <ul>
   * <li>Validates the registration request</li>
   * <li>Creates a user profile in the users service</li>
   * <li>Creates authentication credentials</li>
   * <li>Returns the registration result</li>
   * </ul>
   * 
   * <p>
   * <strong>Request Body:</strong>
   * </p>
   * 
   * <pre>
   * {
   *   "username": "johndoe",
   *   "email": "john.doe@example.com",
   *   "password": "SecurePass123!",
   *   "firstName": "John",
   *   "lastName": "Doe",
   *   "displayName": "John Doe"
   * }
   * </pre>
   * 
   * <p>
   * <strong>Response:</strong>
   * </p>
   * 
   * <pre>
   * {
   *   "userId": "550e8400-e29b-41d4-a716-446655440000",
   *   "username": "johndoe",
   *   "email": "john.doe@example.com",
   *   "firstName": "John",
   *   "lastName": "Doe",
   *   "displayName": "John Doe",
   *   "emailVerified": false,
   *   "mfaEnabled": false,
   *   "createdAt": "2024-01-01T10:00:00",
   *   "updatedAt": "2024-01-01T10:00:00",
   *   "message": "User registered successfully",
   *   "status": "SUCCESS"
   * }
   * </pre>
   * 
   * @param request the registration request containing user data
   * @return ResponseEntity containing the registration response
   */
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegistrationResponse>> register(
      @Valid @RequestBody CreateUserRequest request) {

    log.info("Received registration request for username: {}", request.getUsername());

    try {
      // Validate request
      if (request == null) {
        log.error("Registration request is null");
        return ResponseEntity.badRequest()
            .body(ApiResponse.<RegistrationResponse>builder()
                .success(false)
                .message("Registration request cannot be null")
                .build());
      }

      // Process registration
      RegistrationResponse response = authService.register(request);

      log.info("Registration successful for username: {}", request.getUsername());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.<RegistrationResponse>builder()
              .success(true)
              .data(response)
              .message("User registered successfully")
              .build());

    } catch (IllegalArgumentException e) {
      log.error("Registration validation failed for username: {}", request.getUsername(), e);
      return ResponseEntity.badRequest()
          .body(ApiResponse.<RegistrationResponse>builder()
              .success(false)
              .message("Registration validation failed: " + e.getMessage())
              .build());

    } catch (Exception e) {
      log.error("Registration failed for username: {}", request.getUsername(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.<RegistrationResponse>builder()
              .success(false)
              .message("Registration failed: " + e.getMessage())
              .build());
    }
  }

}