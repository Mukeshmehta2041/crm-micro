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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmingmukesh.users.service.users_service.dto.ApiResponse;
import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.request.UpdateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.response.UserResponse;
import com.programmingmukesh.users.service.users_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for User Management Operations.
 * 
 * <p>
 * Provides comprehensive CRUD operations for user management with:
 * </p>
 * <ul>
 * <li>Full user lifecycle management (create, read, update, delete)</li>
 * <li>Advanced filtering and pagination capabilities</li>
 * <li>Proper validation and error handling</li>
 * <li>RESTful API design principles</li>
 * <li>Comprehensive audit logging</li>
 * <li>Performance optimized queries</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 2.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "User Management", description = "APIs for managing user accounts and profiles")
public class UserController {

  private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
  private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
  private static final int MAX_PAGE_SIZE = 100;
  private static final int DEFAULT_PAGE_SIZE = 20;

  private final UserService userService;

  // =============== CREATE OPERATIONS ===============

  @Operation(summary = "Create a new user", description = "Creates a new user account with the provided information. Username and email must be unique.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or validation errors"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User with username or email already exists")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<UserResponse>> createUser(
      @Parameter(description = "User creation request payload", required = true) @Valid @RequestBody CreateUserRequest request) {

    log.info("Creating user with username: {}", request.getUsername());

    UserResponse user = userService.createUser(request);

    log.info("User created successfully with ID: {}", user.getId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(user, "User created successfully"));
  }

  // =============== READ OPERATIONS ===============

  @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found and retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found with the provided ID"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid UUID format")
  })
  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(
      @Parameter(description = "User unique identifier", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable @NotNull(message = "User ID cannot be null") @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format") String userId) {

    log.debug("Fetching user by ID: {}", userId);

    UserResponse user = userService.getUserById(UUID.fromString(userId));
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  @Operation(summary = "Get user by username", description = "Retrieves a user by their unique username")
  @GetMapping("/username/{username}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(
      @Parameter(description = "Username to search for", example = "john.doe") @PathVariable @NotBlank(message = "Username cannot be blank") String username) {

    log.debug("Fetching user by username: {}", username);

    UserResponse user = userService.getUserByUsername(username);
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
  @GetMapping("/email/{email}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
      @Parameter(description = "Email address to search for", example = "john.doe@example.com") @PathVariable @NotBlank(message = "Email cannot be blank") @Pattern(regexp = EMAIL_REGEX, message = "Invalid email format") String email) {

    log.debug("Fetching user by email: {}", email);

    UserResponse user = userService.getUserByEmail(email);
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  @Operation(summary = "Get all users with pagination", description = "Retrieves a paginated list of all active users with optional sorting")
  @GetMapping
  public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,

      @Parameter(description = "Page size (max 100)", example = "20") @RequestParam(defaultValue = "20") @Min(value = 1, message = "Page size must be at least 1") @Max(value = MAX_PAGE_SIZE, message = "Page size cannot exceed "
          + MAX_PAGE_SIZE) int size,

      @Parameter(description = "Sort field", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sort,

      @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") @Pattern(regexp = "^(ASC|DESC)$", message = "Direction must be ASC or DESC") String direction) {

    log.debug("Fetching users - page: {}, size: {}, sort: {}, direction: {}",
        page, size, sort, direction);

    Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
    Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE),
        Sort.by(sortDirection, sort));

    Page<UserResponse> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  @Operation(summary = "Get users by department", description = "Retrieves all active users belonging to a specific department")
  @GetMapping("/department/{department}")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByDepartment(
      @Parameter(description = "Department name", example = "Engineering") @PathVariable @NotBlank(message = "Department cannot be blank") String department) {

    log.debug("Fetching users by department: {}", department);

    List<UserResponse> users = userService.getUsersByDepartment(department);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  @Operation(summary = "Get users by company", description = "Retrieves all active users belonging to a specific company")
  @GetMapping("/company/{company}")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByCompany(
      @Parameter(description = "Company name", example = "Acme Corp") @PathVariable @NotBlank(message = "Company cannot be blank") String company) {

    log.debug("Fetching users by company: {}", company);

    List<UserResponse> users = userService.getUsersByCompany(company);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  // =============== UPDATE OPERATIONS ===============

  @Operation(summary = "Update user information", description = "Updates an existing user with new information. Only provided fields will be updated.")
  @PutMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
      @Parameter(description = "User unique identifier") @PathVariable @NotNull(message = "User ID cannot be null") @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format") String userId,

      @Parameter(description = "User update request payload", required = true) @Valid @RequestBody UpdateUserRequest request) {

    log.info("Updating user with ID: {}", userId);

    UserResponse user = userService.updateUser(UUID.fromString(userId), request);

    log.info("User updated successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
  }

  @Operation(summary = "Partially update user", description = "Performs partial update of user information using JSON Patch operations")
  @PatchMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> patchUser(
      @Parameter(description = "User unique identifier") @PathVariable @NotNull(message = "User ID cannot be null") @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format") String userId,

      @Parameter(description = "Partial update request payload", required = true) @Valid @RequestBody UpdateUserRequest request) {

    log.info("Partially updating user with ID: {}", userId);

    UserResponse user = userService.patchUser(UUID.fromString(userId), request);

    log.info("User partially updated successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
  }

  // =============== STATUS OPERATIONS ===============

  @Operation(summary = "Activate user account", description = "Activates a previously deactivated user account")
  @PatchMapping("/{userId}/activate")
  public ResponseEntity<ApiResponse<Void>> activateUser(
      @Parameter(description = "User unique identifier") @PathVariable @NotNull(message = "User ID cannot be null") @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format") String userId) {

    log.info("Activating user with ID: {}", userId);

    userService.activateUser(UUID.fromString(userId));

    log.info("User activated successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(null, "User activated successfully"));
  }

  @Operation(summary = "Deactivate user account", description = "Deactivates an active user account without deleting it")
  @PatchMapping("/{userId}/deactivate")
  public ResponseEntity<ApiResponse<Void>> deactivateUser(
      @Parameter(description = "User unique identifier") @PathVariable @NotNull(message = "User ID cannot be null") @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format") String userId) {

    log.info("Deactivating user with ID: {}", userId);

    userService.deactivateUser(UUID.fromString(userId));

    log.info("User deactivated successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
  }

  // =============== DELETE OPERATIONS ===============

  @Operation(summary = "Delete user account", description = "Performs soft delete of a user account. The account will be marked as deleted but data is preserved.")
  @DeleteMapping("/{userId}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @Parameter(description = "User unique identifier") @PathVariable @NotNull(message = "User ID cannot be null") @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format") String userId) {

    log.info("Deleting user with ID: {}", userId);

    userService.deleteUser(UUID.fromString(userId));

    log.info("User deleted successfully with ID: {}", userId);

    return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
  }

  // =============== UTILITY OPERATIONS ===============

  @Operation(summary = "Check if user exists", description = "Checks if a user exists by username or email address")
  @GetMapping("/exists")
  public ResponseEntity<ApiResponse<Boolean>> userExists(
      @Parameter(description = "Username to check", example = "john.doe") @RequestParam(required = false) String username,

      @Parameter(description = "Email to check", example = "john.doe@example.com") @RequestParam(required = false) String email) {

    log.debug("Checking if user exists - username: {}, email: {}", username, email);

    if (username == null && email == null) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("VALIDATION_ERROR", "At least one parameter (username or email) must be provided"));
    }

    boolean exists = userService.userExists(username, email);
    return ResponseEntity.ok(ApiResponse.success(exists));
  }

  @Operation(summary = "Get total user count", description = "Returns the total number of active users in the system")
  @GetMapping("/count")
  public ResponseEntity<ApiResponse<Long>> getUserCount() {
    log.debug("Getting user count");

    long count = userService.getUserCount();
    return ResponseEntity.ok(ApiResponse.success(count));
  }

  @Operation(summary = "Search users", description = "Advanced search functionality with multiple filter criteria")
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
      @Parameter(description = "Search query for name, username, or email") @RequestParam(required = false) String query,

      @Parameter(description = "Filter by department") @RequestParam(required = false) String department,

      @Parameter(description = "Filter by company") @RequestParam(required = false) String company,

      @Parameter(description = "Filter by status") @RequestParam(required = false) String status,

      @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,

      @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") @Min(1) @Max(MAX_PAGE_SIZE) int size,

      @Parameter(description = "Sort field", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sort,

      @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") String direction) {

    log.debug("Searching users with query: {}, department: {}, company: {}, status: {}",
        query, department, company, status);

    Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE),
        Sort.by(Sort.Direction.fromString(direction), sort));

    Page<UserResponse> users = userService.searchUsers(query, department, company, status, pageable);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  // =============== HEALTH CHECK ===============

  @Operation(summary = "Health check", description = "Simple health check endpoint to verify service availability")
  @GetMapping("/health")
  public ResponseEntity<ApiResponse<String>> health() {
    return ResponseEntity.ok(ApiResponse.success("User service is healthy"));
  }
}