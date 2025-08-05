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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "Operations for managing user profiles and accounts")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  /**
   * Creates a new user.
   * 
   * @param request the create user request
   * @return the created user response
   */
  @Operation(
      summary = "Create a new user",
      description = "Creates a new user account with the provided information. " +
                   "Username and email must be unique across the system.",
      tags = {"User Management"}
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "User created successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(
                  name = "Success Response",
                  value = """
                      {
                        "success": true,
                        "data": {
                          "id": "123e4567-e89b-12d3-a456-426614174000",
                          "username": "john.doe",
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "status": "ACTIVE",
                          "createdAt": "2024-01-15T10:30:00"
                        },
                        "message": "User created successfully",
                        "timestamp": "2024-01-15T10:30:00"
                      }
                      """
              )
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Invalid input data",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(
                  name = "Validation Error",
                  value = """
                      {
                        "success": false,
                        "error": "Validation failed",
                        "message": "Username must be between 3 and 100 characters",
                        "timestamp": "2024-01-15T10:30:00"
                      }
                      """
              )
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "409",
          description = "User already exists",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(
                  name = "Conflict Error",
                  value = """
                      {
                        "success": false,
                        "error": "User already exists",
                        "message": "User with email john.doe@example.com already exists",
                        "timestamp": "2024-01-15T10:30:00"
                      }
                      """
              )
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<UserResponse>> createUser(
      @Parameter(
          description = "User creation request with all required information",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CreateUserRequest.class),
              examples = @ExampleObject(ref = "#/components/examples/UserCreateExample")
          )
      )
      @Valid @RequestBody CreateUserRequest request) {
    log.info("Creating user with username: {}", request.getUsername());

    try {
      UserResponse user = userService.createUser(request);
      log.info("User created successfully with ID: {}", user.getId());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success(user, "User created successfully"));
    } catch (UserAlreadyExistsException e) {
      log.warn("User creation failed - already exists: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(ApiResponse.error("User already exists", e.getMessage()));
    } catch (UserValidationException e) {
      log.warn("User creation failed - validation error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Validation failed", e.getMessage()));
    } catch (Exception e) {
      log.error("User creation failed with unexpected error: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to create user"));
    }
  }

  /**
   * Retrieves a user by ID.
   * 
   * @param userId the user ID
   * @return the user response
   */
  @Operation(
      summary = "Get user by ID",
      description = "Retrieves a user's complete profile information using their unique identifier.",
      tags = {"User Management"}
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "User found successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(ref = "#/components/examples/UserResponseExample")
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(
      @Parameter(
          description = "Unique identifier of the user (UUID format)",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000",
          schema = @Schema(type = "string", format = "uuid")
      )
      @PathVariable @NotNull @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$") String userId) {
    log.debug("Fetching user by ID: {}", userId);

    try {
      UserResponse user = userService.getUserById(UUID.fromString(userId));
      return ResponseEntity.ok(ApiResponse.success(user));
    } catch (UserNotFoundException e) {
      log.warn("User not found with ID: {}", userId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("User not found", e.getMessage()));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid UUID format: {}", userId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Invalid user ID format", "User ID must be a valid UUID"));
    } catch (Exception e) {
      log.error("Error fetching user by ID: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to fetch user"));
    }
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

    try {
      UserResponse user = userService.getUserByUsername(username);
      return ResponseEntity.ok(ApiResponse.success(user));
    } catch (UserNotFoundException e) {
      log.warn("User not found with username: {}", username);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("User not found", e.getMessage()));
    } catch (Exception e) {
      log.error("Error fetching user by username: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to fetch user"));
    }
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

    try {
      UserResponse user = userService.getUserByEmail(email);
      return ResponseEntity.ok(ApiResponse.success(user));
    } catch (UserNotFoundException e) {
      log.warn("User not found with email: {}", email);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("User not found", e.getMessage()));
    } catch (Exception e) {
      log.error("Error fetching user by email: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to fetch user"));
    }
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
  @Operation(
      summary = "Get all users with pagination",
      description = "Retrieves a paginated list of all users with optional sorting. " +
                   "Supports sorting by various fields and pagination for efficient data retrieval.",
      tags = {"User Search"}
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Users retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(
                  name = "Paginated Users Response",
                  value = """
                      {
                        "success": true,
                        "data": {
                          "content": [
                            {
                              "id": "123e4567-e89b-12d3-a456-426614174000",
                              "username": "john.doe",
                              "firstName": "John",
                              "lastName": "Doe",
                              "email": "john.doe@example.com",
                              "status": "ACTIVE"
                            }
                          ],
                          "pageable": {
                            "pageNumber": 0,
                            "pageSize": 20,
                            "sort": {
                              "sorted": true,
                              "ascending": false
                            }
                          },
                          "totalElements": 1,
                          "totalPages": 1,
                          "first": true,
                          "last": true
                        },
                        "timestamp": "2024-01-15T10:30:00"
                      }
                      """
              )
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping
  public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
      @Parameter(description = "Page number (0-based)", example = "0")
      @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "20")
      @RequestParam(defaultValue = "20") int size,
      @Parameter(description = "Field to sort by", example = "createdAt", 
                 schema = @Schema(allowableValues = {"createdAt", "updatedAt", "firstName", "lastName", "email", "username"}))
      @RequestParam(defaultValue = "createdAt") String sort,
      @Parameter(description = "Sort direction", example = "DESC",
                 schema = @Schema(allowableValues = {"ASC", "DESC"}))
      @RequestParam(defaultValue = "DESC") String direction) {
    log.debug("Fetching all users with pagination - page: {}, size: {}, sort: {}, direction: {}",
        page, size, sort, direction);

    try {
      Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
      Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

      Page<UserResponse> users = userService.getAllUsers(pageable);
      return ResponseEntity.ok(ApiResponse.success(users));
    } catch (Exception e) {
      log.error("Error fetching all users: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to fetch users"));
    }
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

    try {
      List<UserResponse> users = userService.getUsersByDepartment(department);
      return ResponseEntity.ok(ApiResponse.success(users));
    } catch (Exception e) {
      log.error("Error fetching users by department: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to fetch users"));
    }
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

    try {
      List<UserResponse> users = userService.getUsersByCompany(company);
      return ResponseEntity.ok(ApiResponse.success(users));
    } catch (Exception e) {
      log.error("Error fetching users by company: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to fetch users"));
    }
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

    try {
      UserResponse user = userService.updateUser(UUID.fromString(userId), request);
      log.info("User updated successfully with ID: {}", userId);

      return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    } catch (UserNotFoundException e) {
      log.warn("User not found for update with ID: {}", userId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("User not found", e.getMessage()));
    } catch (UserAlreadyExistsException e) {
      log.warn("User update failed - conflict: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(ApiResponse.error("User conflict", e.getMessage()));
    } catch (UserValidationException e) {
      log.warn("User update failed - validation error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Validation failed", e.getMessage()));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid UUID format: {}", userId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Invalid user ID format", "User ID must be a valid UUID"));
    } catch (Exception e) {
      log.error("User update failed with unexpected error: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to update user"));
    }
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

    try {
      userService.deleteUser(UUID.fromString(userId));
      log.info("User deleted successfully with ID: {}", userId);

      return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    } catch (UserNotFoundException e) {
      log.warn("User not found for deletion with ID: {}", userId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("User not found", e.getMessage()));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid UUID format: {}", userId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Invalid user ID format", "User ID must be a valid UUID"));
    } catch (Exception e) {
      log.error("User deletion failed with unexpected error: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to delete user"));
    }
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

    try {
      userService.activateUser(UUID.fromString(userId));
      log.info("User activated successfully with ID: {}", userId);

      return ResponseEntity.ok(ApiResponse.success(null, "User activated successfully"));
    } catch (UserNotFoundException e) {
      log.warn("User not found for activation with ID: {}", userId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("User not found", e.getMessage()));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid UUID format: {}", userId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Invalid user ID format", "User ID must be a valid UUID"));
    } catch (Exception e) {
      log.error("User activation failed with unexpected error: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to activate user"));
    }
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

    try {
      userService.deactivateUser(UUID.fromString(userId));
      log.info("User deactivated successfully with ID: {}", userId);

      return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
    } catch (UserNotFoundException e) {
      log.warn("User not found for deactivation with ID: {}", userId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("User not found", e.getMessage()));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid UUID format: {}", userId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Invalid user ID format", "User ID must be a valid UUID"));
    } catch (Exception e) {
      log.error("User deactivation failed with unexpected error: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to deactivate user"));
    }
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

    try {
      boolean exists = userService.userExists(username, email);
      return ResponseEntity.ok(ApiResponse.success(exists));
    } catch (Exception e) {
      log.error("Error checking user existence: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to check user existence"));
    }
  }

  /**
   * Gets the total count of active users.
   * 
   * @return the user count
   */
  @GetMapping("/count")
  public ResponseEntity<ApiResponse<Long>> getUserCount() {
    log.debug("Getting user count");

    try {
      long count = userService.getUserCount();
      return ResponseEntity.ok(ApiResponse.success(count));
    } catch (Exception e) {
      log.error("Error getting user count: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Internal server error", "Failed to get user count"));
    }
  }

  /**
   * Health check endpoint.
   * 
   * @return the health status
   */
  @Operation(
      summary = "Health check",
      description = "Simple health check endpoint to verify the service is running and responsive.",
      tags = {"System"}
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Service is healthy",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(
                  name = "Health Check Response",
                  value = """
                      {
                        "success": true,
                        "data": "User service is healthy",
                        "timestamp": "2024-01-15T10:30:00"
                      }
                      """
              )
          )
      )
  })
  @GetMapping("/health")
  public ResponseEntity<ApiResponse<String>> health() {
    return ResponseEntity.ok(ApiResponse.success("User service is healthy"));
  }
}
