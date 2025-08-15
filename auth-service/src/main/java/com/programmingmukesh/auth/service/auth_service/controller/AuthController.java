package com.programmingmukesh.auth.service.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;
import com.programmingmukesh.auth.service.auth_service.dto.request.CompleteRegistrationRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.RegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.dto.response.CompleteRegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.service.AuthService;
import com.programmingmukesh.auth.service.auth_service.service.CompleteRegistrationService;

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

  @Autowired
  private CompleteRegistrationService completeRegistrationService;

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

  /**
   * Performs complete registration including Tenant, User, and Auth setup.
   * 
   * @param request the complete registration request
   * @return ResponseEntity containing the complete registration response
   */
  @PostMapping("/register/complete")
  public ResponseEntity<ApiResponse<CompleteRegistrationResponse>> completeRegistration(
      @Valid @RequestBody CompleteRegistrationRequest request) {

    log.info("Received complete registration request for email: {} and company: {}",
        request.getEmail(), request.getCompanyName());

    try {
      CompleteRegistrationResponse response = completeRegistrationService.performCompleteRegistration(request);

      log.info("Complete registration successful for email: {} in tenant: {}",
          request.getEmail(), response.getSubdomain());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.<CompleteRegistrationResponse>builder()
              .success(true)
              .message("Registration completed successfully")
              .data(response)
              .build());

    } catch (RuntimeException e) {
      log.warn("Complete registration failed for email '{}': {}", request.getEmail(), e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.<CompleteRegistrationResponse>builder()
              .success(false)
              .message(e.getMessage())
              .build());

    } catch (Exception e) {
      log.error("Complete registration failed for email: {}", request.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.<CompleteRegistrationResponse>builder()
              .success(false)
              .message("Registration failed. Please try again later.")
              .build());
    }
  }

  /**
   * Checks if a username is available.
   * 
   * @param username the username to check
   * @return ResponseEntity containing availability status
   */
  @GetMapping("/check-username/{username}")
  public ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailability(@PathVariable String username) {
    log.info("Checking username availability: {}", username);

    boolean available = completeRegistrationService.isUsernameAvailable(username);
    String message = available ? "Username is available" : "Username is not available";

    return ResponseEntity.ok(ApiResponse.<Boolean>builder()
        .success(true)
        .message(message)
        .data(available)
        .build());
  }

  /**
   * Checks if an email is available.
   * 
   * @param email the email to check
   * @return ResponseEntity containing availability status
   */
  @GetMapping("/check-email")
  public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@RequestParam String email) {
    log.info("Checking email availability: {}", email);

    boolean available = completeRegistrationService.isEmailAvailable(email);
    String message = available ? "Email is available" : "Email is not available";

    return ResponseEntity.ok(ApiResponse.<Boolean>builder()
        .success(true)
        .message(message)
        .data(available)
        .build());
  }

  /**
   * Checks if a company name is available.
   * 
   * @param companyName the company name to check
   * @return ResponseEntity containing availability status
   */
  @GetMapping("/check-company")
  public ResponseEntity<ApiResponse<Boolean>> checkCompanyNameAvailability(@RequestParam String companyName) {
    log.info("Checking company name availability: {}", companyName);

    boolean available = completeRegistrationService.isCompanyNameAvailable(companyName);
    String message = available ? "Company name is available" : "Company name is not available";

    return ResponseEntity.ok(ApiResponse.<Boolean>builder()
        .success(true)
        .message(message)
        .data(available)
        .build());
  }

}