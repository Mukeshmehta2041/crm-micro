package com.programmingmukesh.auth.service.auth_service.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.UserResponseDTO;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;

/**
 * Feign client for communicating with the Users Service.
 * 
 * <p>
 * This client provides:
 * </p>
 * <ul>
 * <li>User creation in the users service</li>
 * <li>User retrieval by various criteria</li>
 * <li>Error handling and fallback mechanisms</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@FeignClient(name = "users-service", url = "${users.service.url:http://localhost:8082}", fallback = UsersServiceClientFallback.class)
public interface UsersServiceClient {

  /**
   * Creates a new user in the users service.
   * 
   * @param request the user creation request
   * @return ResponseEntity containing the created user
   */
  @PostMapping("/api/v1/users")
  ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody CreateUserRequest request);

  /**
   * Retrieves a user by ID.
   * 
   * @param userId the user ID
   * @return ResponseEntity containing the user
   */
  @GetMapping("/api/v1/users/{userId}")
  ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable UUID userId);

  /**
   * Retrieves a user by username.
   * 
   * @param username the username
   * @return ResponseEntity containing the user
   */
  @GetMapping("/api/v1/users/username/{username}")
  ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(@PathVariable String username);

  /**
   * Retrieves a user by email.
   * 
   * @param email the email
   * @return ResponseEntity containing the user
   */
  @GetMapping("/api/v1/users/email/{email}")
  ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(@PathVariable String email);
}