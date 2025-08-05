package com.programmingmukesh.users.service.users_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.response.UserResponse;

/**
 * User Service interface defining all user-related operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public interface UserService {

  /**
   * Creates a new user.
   * 
   * @param request the create user request
   * @return the created user response
   */
  UserResponse createUser(CreateUserRequest request);

  /**
   * Retrieves a user by ID.
   * 
   * @param userId the user ID
   * @return the user response
   */
  UserResponse getUserById(UUID userId);

  /**
   * Retrieves a user by username.
   * 
   * @param username the username
   * @return the user response
   */
  UserResponse getUserByUsername(String username);

  /**
   * Retrieves a user by email.
   * 
   * @param email the email
   * @return the user response
   */
  UserResponse getUserByEmail(String email);

  /**
   * Retrieves all users with pagination.
   * 
   * @param pageable the pagination parameters
   * @return the page of user responses
   */
  Page<UserResponse> getAllUsers(Pageable pageable);

  /**
   * Retrieves users by department.
   * 
   * @param department the department
   * @return the list of user responses
   */
  List<UserResponse> getUsersByDepartment(String department);

  /**
   * Retrieves users by company.
   * 
   * @param company the company
   * @return the list of user responses
   */
  List<UserResponse> getUsersByCompany(String company);

  /**
   * Updates a user.
   * 
   * @param userId  the user ID
   * @param request the update request
   * @return the updated user response
   */
  UserResponse updateUser(UUID userId, CreateUserRequest request);

  /**
   * Deletes a user (soft delete).
   * 
   * @param userId the user ID
   */
  void deleteUser(UUID userId);

  /**
   * Activates a user.
   * 
   * @param userId the user ID
   */
  void activateUser(UUID userId);

  /**
   * Deactivates a user.
   * 
   * @param userId the user ID
   */
  void deactivateUser(UUID userId);

  /**
   * Checks if a user exists by username or email.
   * 
   * @param username the username
   * @param email    the email
   * @return true if user exists, false otherwise
   */
  boolean userExists(String username, String email);

  /**
   * Gets the total count of active users.
   * 
   * @return the user count
   */
  long getUserCount();
}
