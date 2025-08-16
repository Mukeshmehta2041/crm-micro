package com.programmingmukesh.users.service.users_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.request.UpdateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.response.UserResponse;

/**
 * Enhanced User Service interface defining comprehensive user management
 * operations.
 * 
 * <p>
 * This service provides:
 * </p>
 * <ul>
 * <li>Complete CRUD operations with validation</li>
 * <li>Advanced search and filtering capabilities</li>
 * <li>User status management (activate/deactivate)</li>
 * <li>Bulk operations support</li>
 * <li>Performance optimized queries with caching</li>
 * <li>Event-driven architecture integration</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 2.0
 * @since 2024
 */
public interface UserService {

  // =============== CREATE OPERATIONS ===============

  /**
   * Creates a new user with comprehensive validation and business rules.
   * 
   * @param request the create user request containing user details
   * @return the created user response with generated ID and timestamps
   * @throws UserValidationException    if validation fails
   * @throws UserAlreadyExistsException if username or email already exists
   */
  UserResponse createUser(CreateUserRequest request);

  // =============== READ OPERATIONS ===============

  /**
   * Retrieves a user by their unique identifier.
   * 
   * @param userId the unique user identifier
   * @return the user response if found
   * @throws UserNotFoundException    if user not found or is deleted
   * @throws IllegalArgumentException if userId is null
   */
  UserResponse getUserById(UUID userId);

  /**
   * Retrieves a user by their unique username (case-insensitive).
   * 
   * @param username the username to search for
   * @return the user response if found
   * @throws UserNotFoundException   if user not found or is deleted
   * @throws UserValidationException if username is blank
   */
  UserResponse getUserByUsername(String username);

  /**
   * Retrieves a user by their email address (case-insensitive).
   * 
   * @param email the email address to search for
   * @return the user response if found
   * @throws UserNotFoundException   if user not found or is deleted
   * @throws UserValidationException if email is blank or invalid
   */
  UserResponse getUserByEmail(String email);

  /**
   * Retrieves all active users with pagination and sorting support.
   * 
   * @param pageable the pagination and sorting parameters
   * @return a page of user responses
   * @throws IllegalArgumentException if pageable is null
   */
  Page<UserResponse> getAllUsers(Pageable pageable);

  /**
   * Retrieves all active users belonging to a specific department.
   * 
   * @param department the department name (case-insensitive)
   * @return list of user responses in the department
   * @throws UserValidationException if department is blank
   */
  List<UserResponse> getUsersByDepartment(String department);

  /**
   * Retrieves all active users belonging to a specific company.
   * 
   * @param company the company name (case-insensitive)
   * @return list of user responses in the company
   * @throws UserValidationException if company is blank
   */
  List<UserResponse> getUsersByCompany(String company);

  /**
   * Advanced search functionality with multiple filter criteria.
   * 
   * @param query      general search query for name, username, or email
   * @param department filter by department (optional)
   * @param company    filter by company (optional)
   * @param status     filter by user status (optional)
   * @param pageable   pagination and sorting parameters
   * @return page of user responses matching the criteria
   */
  Page<UserResponse> searchUsers(String query, String department, String company,
      String status, Pageable pageable);

  // =============== UPDATE OPERATIONS ===============

  /**
   * Updates an existing user with new information (full update).
   * 
   * @param userId  the unique user identifier
   * @param request the update request containing new user details
   * @return the updated user response
   * @throws UserNotFoundException      if user not found or is deleted
   * @throws UserValidationException    if validation fails
   * @throws UserAlreadyExistsException if username or email conflicts exist
   */
  UserResponse updateUser(UUID userId, UpdateUserRequest request);

  /**
   * Partially updates an existing user (patch operation).
   * Only non-null fields in the request will be updated.
   * 
   * @param userId  the unique user identifier
   * @param request the partial update request
   * @return the updated user response
   * @throws UserNotFoundException      if user not found or is deleted
   * @throws UserValidationException    if validation fails
   * @throws UserAlreadyExistsException if username or email conflicts exist
   */
  UserResponse patchUser(UUID userId, UpdateUserRequest request);

  // =============== STATUS OPERATIONS ===============

  /**
   * Activates a user account, allowing them to access the system.
   * 
   * @param userId the unique user identifier
   * @throws UserNotFoundException if user not found or is deleted
   */
  void activateUser(UUID userId);

  /**
   * Deactivates a user account, preventing system access while preserving data.
   * 
   * @param userId the unique user identifier
   * @throws UserNotFoundException if user not found or is deleted
   */
  void deactivateUser(UUID userId);

  // =============== DELETE OPERATIONS ===============

  /**
   * Performs soft delete of a user account.
   * The user data is preserved but marked as deleted and hidden from queries.
   * 
   * @param userId the unique user identifier
   * @throws UserNotFoundException if user not found or already deleted
   */
  void deleteUser(UUID userId);

  // =============== UTILITY OPERATIONS ===============

  /**
   * Checks if a user exists by username or email address.
   * 
   * @param username the username to check (optional)
   * @param email    the email address to check (optional)
   * @return true if user exists, false otherwise
   * @throws UserValidationException if both username and email are null/blank
   */
  boolean userExists(String username, String email);

  /**
   * Gets the total count of active (non-deleted) users in the system.
   * 
   * @return the count of active users
   */
  long getUserCount();

  // =============== BULK OPERATIONS (Optional Extensions) ===============

  /**
   * Creates multiple users in a single transaction.
   * 
   * @param requests list of create user requests
   * @return list of created user responses
   * @throws UserValidationException    if any validation fails
   * @throws UserAlreadyExistsException if any username or email conflicts exist
   */
  default List<UserResponse> createUsers(List<CreateUserRequest> requests) {
    throw new UnsupportedOperationException("Bulk user creation not implemented");
  }

  /**
   * Updates multiple users in a single transaction.
   * 
   * @param userUpdates map of user IDs to update requests
   * @return list of updated user responses
   * @throws UserNotFoundException   if any user not found
   * @throws UserValidationException if any validation fails
   */
  default List<UserResponse> updateUsers(java.util.Map<UUID, UpdateUserRequest> userUpdates) {
    throw new UnsupportedOperationException("Bulk user updates not implemented");
  }

  /**
   * Deactivates multiple users in a single transaction.
   * 
   * @param userIds list of user IDs to deactivate
   * @throws UserNotFoundException if any user not found
   */
  default void deactivateUsers(List<UUID> userIds) {
    throw new UnsupportedOperationException("Bulk user deactivation not implemented");
  }

  // =============== REPORTING OPERATIONS (Optional Extensions) ===============

  /**
   * Gets user statistics grouped by department.
   * 
   * @return map of department names to user counts
   */
  default java.util.Map<String, Long> getUserCountByDepartment() {
    throw new UnsupportedOperationException("Department statistics not implemented");
  }

  /**
   * Gets user statistics grouped by company.
   * 
   * @return map of company names to user counts
   */
  default java.util.Map<String, Long> getUserCountByCompany() {
    throw new UnsupportedOperationException("Company statistics not implemented");
  }

  /**
   * Gets user registration statistics for a date range.
   * 
   * @param startDate start date for the range
   * @param endDate   end date for the range
   * @return map of dates to registration counts
   */
  default java.util.Map<java.time.LocalDate, Long> getUserRegistrationStats(
      java.time.LocalDate startDate, java.time.LocalDate endDate) {
    throw new UnsupportedOperationException("Registration statistics not implemented");
  }
}