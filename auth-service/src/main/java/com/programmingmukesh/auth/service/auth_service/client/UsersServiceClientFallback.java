package com.programmingmukesh.auth.service.auth_service.client;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.UserResponseDTO;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Fallback implementation for UsersServiceClient.
 * 
 * <p>
 * This class provides fallback behavior when the users service is unavailable:
 * </p>
 * <ul>
 * <li>Logs the failure for monitoring</li>
 * <li>Returns appropriate error responses</li>
 * <li>Prevents cascading failures</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Component
public class UsersServiceClientFallback implements UsersServiceClient {

  @Override
  public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(CreateUserRequest request) {
    log.error("Users service is unavailable. Cannot create user: {}", request.getUsername());
    return ResponseEntity.status(503)
        .body(ApiResponse.<UserResponseDTO>builder()
            .success(false)
            .message("User service is temporarily unavailable. Please try again later.")
            .build());
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(UUID userId) {
    log.error("Users service is unavailable. Cannot get user by ID: {}", userId);
    return ResponseEntity.status(503)
        .body(ApiResponse.<UserResponseDTO>builder()
            .success(false)
            .message("User service is temporarily unavailable. Please try again later.")
            .build());
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(String username) {
    log.error("Users service is unavailable. Cannot get user by username: {}", username);
    return ResponseEntity.status(503)
        .body(ApiResponse.<UserResponseDTO>builder()
            .success(false)
            .message("User service is temporarily unavailable. Please try again later.")
            .build());
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(String email) {
    log.error("Users service is unavailable. Cannot get user by email: {}", email);
    return ResponseEntity.status(503)
        .body(ApiResponse.<UserResponseDTO>builder()
            .success(false)
            .message("User service is temporarily unavailable. Please try again later.")
            .build());
  }
}