package com.programmingmukesh.auth.service.auth_service.service;

import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.RegistrationResponse;

/**
 * Authentication Service interface for user authentication operations.
 * 
 * <p>
 * This service handles all authentication-related operations including:
 * </p>
 * <ul>
 * <li>User registration and credential creation</li>
 * <li>User login and authentication</li>
 * <li>Password management and security</li>
 * <li>Multi-factor authentication</li>
 * <li>Account security and lockout management</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public interface AuthService {

  /**
   * Registers a new user with authentication credentials.
   * 
   * <p>
   * This method performs the following operations:
   * </p>
   * <ul>
   * <li>Validates the registration request data</li>
   * <li>Creates a user profile in the users service</li>
   * <li>Creates authentication credentials</li>
   * <li>Hashes the password securely</li>
   * <li>Sets up initial security settings</li>
   * </ul>
   * 
   * @param request the registration request containing user data
   * @return RegistrationResponse containing the created user information
   * @throws IllegalArgumentException if the request data is invalid
   * @throws RuntimeException         if user creation fails
   */
  RegistrationResponse register(CreateUserRequest request);
}