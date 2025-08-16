package com.programmingmukesh.users.service.users_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmingmukesh.users.service.users_service.dto.ApiResponse;
import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.response.UserResponse;
import com.programmingmukesh.users.service.users_service.exception.UserAlreadyExistsException;
import com.programmingmukesh.users.service.users_service.exception.UserNotFoundException;
import com.programmingmukesh.users.service.users_service.exception.UserValidationException;
import com.programmingmukesh.users.service.users_service.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User Controller providing REST API endpoints for user management.
 * 
 * <p>
 * This controller implements:
 * </p>
 * <ul>
 * <li>CRUD operations for users</li>
 * <li>Pagination and filtering</li>
 * <li>Proper error handling and validation</li>
 * <li>RESTful API design</li>
 * <li>Comprehensive logging</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

  private final UserService userService;

  /**
   * Creates a new user.
   * 
   * @param request the create user request
   * @return the created user response
   */

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponse>> createUser(
      @Valid @RequestBody CreateUserRequest request) {
    log.info("Creating user with username: {}", request.getUsername());

    UserResponse user = userService.createUser(request);
    log.info("User created successfully with ID: {}", user.getId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(user, "User created successfully"));
  }

  /**
   * Retrieves a user by ID.
   * 
   * @param userId the user ID
   * @return the user response
   */
  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(
      @PathVariable @NotNull @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$") String userId) {
    log.debug("Fetching user by ID: {}", userId);

    UserResponse user = userService.getUserById(UUID.fromString(userId));
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * Retrieves a user by username.
   * 
   * @param username the username
   * @return the user response
   */
  @GetMapping("/username/{username}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(
      @PathVariable @NotBlank String username) {
    log.debug("Fetching user by username: {}", username);

    UserResponse user = userService.getUserByUsername(username);
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * Retrieves a user by email.
   * 
   * @param email the email
   * @return the user response
   */
  @GetMapping("/email/{email}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
      @PathVariable @NotBlank @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$") String email) {
    log.debug("Fetching user by email: {}", email);

    UserResponse user = userService.getUserByEmail(email);
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * Retrieves all users with pagination.
   * 
   * @param page      the page number (0-based)
   * @param size      the page size
   * @param sort      the sort field
   * @param direction the sort direction
   * @return the page of user responses
   */

  @GetMapping
  public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sort,
      @RequestParam(defaultValue = "DESC") String direction) {
    log.debug("Fetching all users with pagination - page: {}, size: {}, sort: {}, direction: {}",
        page, size, sort, direction);

    Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

    Page<UserResponse> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  /**
   * Retrieves users by department.
   * 
   * @param department the department
   * @return the list of user responses
   */
  @GetMapping("/department/{department}")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByDepartment(
      @PathVariable @NotBlank String department) {
    log.debug("Fetching users by department: {}", department);

    List<UserResponse> users = userService.getUsersByDepartment(department);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  /**
   * Retrieves users by company.
   * 
   * @param company the company
   * @return the list of user responses
   */
  @GetMapping("/company/{company}")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByCompany(
      @PathVariable @NotBlank String company) {
    log.debug("Fetching users by company: {}", company);

    List<UserResponse> users = userService.getUsersByCompany(company);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  /**
   * Updates a user.
   * 
   * @param userId  the user ID
   * @param request the update request
   * @return the updated user response
   */
  @PutMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
      @PathVariable @NotNull @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$") String userId,
      @Valid @RequestBody CreateUserRequest request) {
    log.info("Updating user with ID: {}", userId);

    UserResponse user = userService.updateUser(UUID.fromString(userId), request);
    log.info("User updated successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
  }

  /**
   * Deletes a user (soft delete).
   * 
   * @param userId the user ID
   * @return the response
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @PathVariable @NotNull @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$") String userId) {
    log.info("Deleting user with ID: {}", userId);

    userService.deleteUser(UUID.fromString(userId));
    log.info("User deleted successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
  }

  /**
   * Activates a user.
   * 
   * @param userId the user ID
   * @return the response
   */
  @PostMapping("/{userId}/activate")
  public ResponseEntity<ApiResponse<Void>> activateUser(
      @PathVariable @NotNull @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$") String userId) {
    log.info("Activating user with ID: {}", userId);

    userService.activateUser(UUID.fromString(userId));
    log.info("User activated successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(null, "User activated successfully"));
  }

  /**
   * Deactivates a user.
   * 
   * @param userId the user ID
   * @return the response
   */
  @PostMapping("/{userId}/deactivate")
  public ResponseEntity<ApiResponse<Void>> deactivateUser(
      @PathVariable @NotNull @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$") String userId) {
    log.info("Deactivating user with ID: {}", userId);

    userService.deactivateUser(UUID.fromString(userId));
    log.info("User deactivated successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
  }

  /**
   * Checks if a user exists.
   * 
   * @param username the username
   * @param email    the email
   * @return the response indicating if user exists
   */
  @GetMapping("/exists")
  public ResponseEntity<ApiResponse<Boolean>> userExists(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String email) {
    log.debug("Checking if user exists - username: {}, email: {}", username, email);

    boolean exists = userService.userExists(username, email);
    return ResponseEntity.ok(ApiResponse.success(exists));
  }

  /**
   * Gets the total count of active users.
   * 
   * @return the user count
   */
  @GetMapping("/count")
  public ResponseEntity<ApiResponse<Long>> getUserCount() {
    log.debug("Getting user count");

    long count = userService.getUserCount();
    return ResponseEntity.ok(ApiResponse.success(count));
  }

  /**
   * Health check endpoint.
   * 
   * @return the health status
   */
  @GetMapping("/health")
  public ResponseEntity<ApiResponse<String>> health() {
    return ResponseEntity.ok(ApiResponse.success("User service is healthy"));
  }
}
