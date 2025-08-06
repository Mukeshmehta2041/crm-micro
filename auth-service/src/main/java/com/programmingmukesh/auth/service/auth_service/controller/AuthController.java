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
   * @param request the registration request containing user data
   * @return ResponseEntity containing the registration response
   */
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegistrationResponse>> register(
      @Valid @RequestBody CreateUserRequest request) {

    log.info("Received registration request for username: {}", request.getUsername());

    try {
      RegistrationResponse response = authService.register(request);

      log.info("Registration successful for username: {}", request.getUsername());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.<RegistrationResponse>builder()
              .success(true)
              .data(response)
              .message("User registered successfully")
              .build());

    } catch (IllegalArgumentException e) {
      log.warn("Registration validation failed for username '{}': {}", request.getUsername(), e.getMessage());
      return ResponseEntity.badRequest()
          .body(ApiResponse.<RegistrationResponse>builder()
              .success(false)
              .message("Registration validation failed: " + e.getMessage())
              .build());

    } catch (RuntimeException e) {
      log.warn("Registration failed for username '{}': {}", request.getUsername(), e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(ApiResponse.<RegistrationResponse>builder()
              .success(false)
              .message(e.getMessage())
              .build());

    } catch (Exception e) {
      log.error("Registration failed for username: {}", request.getUsername(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.<RegistrationResponse>builder()
              .success(false)
              .message("Registration failed. Please try again later.")
              .build());
    }
  }

}