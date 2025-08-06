package com.programmingmukesh.users.service.users_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.response.UserResponse;
import com.programmingmukesh.users.service.users_service.entity.User;
import com.programmingmukesh.users.service.users_service.entity.UserStatus;
import com.programmingmukesh.users.service.users_service.event.UserCreatedEvent;
import com.programmingmukesh.users.service.users_service.event.UserDeletedEvent;
import com.programmingmukesh.users.service.users_service.event.UserUpdatedEvent;
import com.programmingmukesh.users.service.users_service.exception.UserAlreadyExistsException;
import com.programmingmukesh.users.service.users_service.exception.UserNotFoundException;
import com.programmingmukesh.users.service.users_service.exception.UserValidationException;
import com.programmingmukesh.users.service.users_service.mapper.UserMapper;
import com.programmingmukesh.users.service.users_service.repository.UserRepository;
import com.programmingmukesh.users.service.users_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User Service Implementation with comprehensive business logic and best
 * practices.
 * 
 * <p>
 * This service implements:
 * </p>
 * <ul>
 * <li>Comprehensive validation and business rules</li>
 * <li>Event-driven architecture for user lifecycle events</li>
 * <li>Proper error handling and custom exceptions</li>
 * <li>Transaction management</li>
 * <li>Audit trail and logging</li>
 * <li>Performance optimization with pagination</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public UserResponse createUser(CreateUserRequest request) {
    log.info("Creating user with username: {}", request.getUsername());

    try {
      // Validate request
      validateCreateUserRequest(request);

      // Check for existing user
      checkUserExists(request.getUsername(), request.getEmail());

      // Create user entity
      User user = userMapper.toEntity(request);

      // Set tenant ID if not provided (multi-tenancy support)
      if (user.getTenantId() == null) {
        user.setTenantId(UUID.randomUUID()); // TODO: Get from security context
      }

      // Save user
      User savedUser = userRepository.save(user);

      log.info("User created successfully with ID: {}", savedUser.getId());

      // Publish user created event
      publishUserCreatedEvent(savedUser);

      return userMapper.toResponse(savedUser);

    } catch (UserAlreadyExistsException | UserValidationException | UserNotFoundException e) {
      // Re-throw custom exceptions so they can be handled by GlobalExceptionHandler
      throw e;
    } catch (DataIntegrityViolationException e) {
      log.error("Data integrity violation while creating user: {}", e.getMessage());
      throw new UserAlreadyExistsException("User with provided username or email already exists");
    } catch (Exception e) {
      log.error("Unexpected error creating user: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to create user", e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserById(UUID userId) {
    log.debug("Fetching user by ID: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    // Check if user is deleted
    if (user.isDeleted()) {
      throw new UserNotFoundException("User has been deleted");
    }

    return userMapper.toResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserByUsername(String username) {
    log.debug("Fetching user by username: {}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

    if (user.isDeleted()) {
      throw new UserNotFoundException("User has been deleted");
    }

    return userMapper.toResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserByEmail(String email) {
    log.debug("Fetching user by email: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

    if (user.isDeleted()) {
      throw new UserNotFoundException("User has been deleted");
    }

    return userMapper.toResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResponse> getAllUsers(Pageable pageable) {
    log.debug("Fetching all users with pagination: {}", pageable);

    Page<User> users = userRepository.findByStatusNotAndDeletedAtIsNull(UserStatus.DELETED, pageable);

    return users.map(userMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> getUsersByDepartment(String department) {
    log.debug("Fetching users by department: {}", department);

    List<User> users = userRepository.findByDepartmentAndStatusNotAndDeletedAtIsNull(department, UserStatus.DELETED);

    return users.stream()
        .map(userMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> getUsersByCompany(String company) {
    log.debug("Fetching users by company: {}", company);

    List<User> users = userRepository.findByCompanyAndStatusNotAndDeletedAtIsNull(company, UserStatus.DELETED);

    return users.stream()
        .map(userMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public UserResponse updateUser(UUID userId, CreateUserRequest request) {
    log.info("Updating user with ID: {}", userId);

    // Validate request
    validateUpdateUserRequest(request);

    // Get existing user
    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    if (existingUser.isDeleted()) {
      throw new UserNotFoundException("Cannot update deleted user");
    }

    // Check for username/email conflicts if changed
    checkUserUpdateConflicts(existingUser, request);

    // Update user
    User updatedUser = userMapper.updateEntity(existingUser, request);
    User savedUser = userRepository.save(updatedUser);

    log.info("User updated successfully with ID: {}", savedUser.getId());

    // Publish user updated event
    publishUserUpdatedEvent(savedUser);

    return userMapper.toResponse(savedUser);
  }

  @Override
  @Transactional
  public void deleteUser(UUID userId) {
    log.info("Deleting user with ID: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    if (user.isDeleted()) {
      throw new UserNotFoundException("User is already deleted");
    }

    // Soft delete
    user.softDelete();
    userRepository.save(user);

    log.info("User deleted successfully with ID: {}", userId);

    // Publish user deleted event
    publishUserDeletedEvent(user);
  }

  @Override
  @Transactional
  public void activateUser(UUID userId) {
    log.info("Activating user with ID: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    if (user.isDeleted()) {
      throw new UserNotFoundException("Cannot activate deleted user");
    }

    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    log.info("User activated successfully with ID: {}", userId);
  }

  @Override
  @Transactional
  public void deactivateUser(UUID userId) {
    log.info("Deactivating user with ID: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    if (user.isDeleted()) {
      throw new UserNotFoundException("Cannot deactivate deleted user");
    }

    user.setStatus(UserStatus.INACTIVE);
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    log.info("User deactivated successfully with ID: {}", userId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean userExists(String username, String email) {
    return userRepository.existsByUsernameOrEmail(username, email);
  }

  @Override
  @Transactional(readOnly = true)
  public long getUserCount() {
    return userRepository.countByStatusNotAndDeletedAtIsNull(UserStatus.DELETED);
  }

  // Private helper methods

  /**
   * Validates the create user request.
   * 
   * @param request the create user request
   * @throws UserValidationException if validation fails
   */
  private void validateCreateUserRequest(CreateUserRequest request) {
    if (request == null) {
      throw new UserValidationException("Create user request cannot be null");
    }

    if (!request.hasMinimumRequiredInfo()) {
      throw new UserValidationException(
          "User must have username, first name, last name, and at least one contact method");
    }

    // Validate email format if provided
    if (request.getEmail() != null && !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new UserValidationException("Invalid email format");
    }

    // Validate phone number format if provided
    if (request.getPhoneNumber() != null && !request.getPhoneNumber().matches("^[+]?[0-9\\s()-]+$")) {
      throw new UserValidationException("Invalid phone number format");
    }

    // Validate birth date is not in the future
    if (request.getBirthDate() != null && request.getBirthDate().isAfter(java.time.LocalDate.now())) {
      throw new UserValidationException("Birth date cannot be in the future");
    }

    // Validate hire date is not in the future
    if (request.getHireDate() != null && request.getHireDate().isAfter(java.time.LocalDate.now())) {
      throw new UserValidationException("Hire date cannot be in the future");
    }

    // Validate working hours if both are provided
    if (request.getWorkingHoursStart() != null && request.getWorkingHoursEnd() != null) {
      if (request.getWorkingHoursStart().isAfter(request.getWorkingHoursEnd())) {
        throw new UserValidationException("Working hours start time cannot be after end time");
      }
    }
  }

  /**
   * Validates the update user request.
   * 
   * @param request the update user request
   * @throws UserValidationException if validation fails
   */
  private void validateUpdateUserRequest(CreateUserRequest request) {
    if (request == null) {
      throw new UserValidationException("Update user request cannot be null");
    }

    // Validate email format if provided
    if (request.getEmail() != null && !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new UserValidationException("Invalid email format");
    }

    // Validate phone number format if provided
    if (request.getPhoneNumber() != null && !request.getPhoneNumber().matches("^[+]?[0-9\\s()-]+$")) {
      throw new UserValidationException("Invalid phone number format");
    }

    // Validate birth date is not in the future
    if (request.getBirthDate() != null && request.getBirthDate().isAfter(java.time.LocalDate.now())) {
      throw new UserValidationException("Birth date cannot be in the future");
    }

    // Validate hire date is not in the future
    if (request.getHireDate() != null && request.getHireDate().isAfter(java.time.LocalDate.now())) {
      throw new UserValidationException("Hire date cannot be in the future");
    }

    // Validate working hours if both are provided
    if (request.getWorkingHoursStart() != null && request.getWorkingHoursEnd() != null) {
      if (request.getWorkingHoursStart().isAfter(request.getWorkingHoursEnd())) {
        throw new UserValidationException("Working hours start time cannot be after end time");
      }
    }
  }

  /**
   * Checks if a user with the given username or email already exists.
   * 
   * @param username the username to check
   * @param email    the email to check
   * @throws UserAlreadyExistsException if user already exists
   */
  private void checkUserExists(String username, String email) {
    if (userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistsException("Username already exists: " + username);
    }

    if (email != null && userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistsException("Email already exists: " + email);
    }
  }

  /**
   * Checks for conflicts when updating a user.
   * 
   * @param existingUser the existing user
   * @param request      the update request
   * @throws UserAlreadyExistsException if there are conflicts
   */
  private void checkUserUpdateConflicts(User existingUser, CreateUserRequest request) {
    // Check username conflict
    if (request.getUsername() != null && !request.getUsername().equals(existingUser.getUsername())) {
      if (userRepository.existsByUsername(request.getUsername())) {
        throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
      }
    }

    // Check email conflict
    if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
      }
    }
  }

  /**
   * Publishes user created event.
   * 
   * @param user the created user
   */
  private void publishUserCreatedEvent(User user) {
    try {
      UserCreatedEvent event = new UserCreatedEvent(user);
      eventPublisher.publishEvent(event);
      log.debug("Published user created event for user ID: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to publish user created event for user ID: {}", user.getId(), e);
      // Don't throw exception as event publishing should not fail the main operation
    }
  }

  /**
   * Publishes user updated event.
   * 
   * @param user the updated user
   */
  private void publishUserUpdatedEvent(User user) {
    try {
      UserUpdatedEvent event = new UserUpdatedEvent(user);
      eventPublisher.publishEvent(event);
      log.debug("Published user updated event for user ID: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to publish user updated event for user ID: {}", user.getId(), e);
      // Don't throw exception as event publishing should not fail the main operation
    }
  }

  /**
   * Publishes user deleted event.
   * 
   * @param user the deleted user
   */
  private void publishUserDeletedEvent(User user) {
    try {
      UserDeletedEvent event = new UserDeletedEvent(user);
      eventPublisher.publishEvent(event);
      log.debug("Published user deleted event for user ID: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to publish user deleted event for user ID: {}", user.getId(), e);
      // Don't throw exception as event publishing should not fail the main operation
    }
  }

  /**
   * Validates user ID format.
   * 
   * @param userId the user ID to validate
   * @throws UserValidationException if user ID is invalid
   */
  private void validateUserId(UUID userId) {
    if (userId == null) {
      throw new UserValidationException("User ID cannot be null");
    }
  }

  /**
   * Validates username format.
   * 
   * @param username the username to validate
   * @throws UserValidationException if username is invalid
   */
  private void validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new UserValidationException("Username cannot be null or empty");
    }

    if (username.length() < 3 || username.length() > 100) {
      throw new UserValidationException("Username must be between 3 and 100 characters");
    }

    if (!username.matches("^[a-zA-Z0-9_-]+$")) {
      throw new UserValidationException("Username can only contain letters, numbers, underscores, and hyphens");
    }
  }

  /**
   * Validates email format.
   * 
   * @param email the email to validate
   * @throws UserValidationException if email is invalid
   */
  private void validateEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new UserValidationException("Email cannot be null or empty");
    }

    if (email.length() > 255) {
      throw new UserValidationException("Email cannot exceed 255 characters");
    }

    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new UserValidationException("Invalid email format");
    }
  }

  /**
   * Validates department name.
   * 
   * @param department the department to validate
   * @throws UserValidationException if department is invalid
   */
  private void validateDepartment(String department) {
    if (department == null || department.trim().isEmpty()) {
      throw new UserValidationException("Department cannot be null or empty");
    }

    if (department.length() > 100) {
      throw new UserValidationException("Department cannot exceed 100 characters");
    }
  }

  /**
   * Validates company name.
   * 
   * @param company the company to validate
   * @throws UserValidationException if company is invalid
   */
  private void validateCompany(String company) {
    if (company == null || company.trim().isEmpty()) {
      throw new UserValidationException("Company cannot be null or empty");
    }

    if (company.length() > 100) {
      throw new UserValidationException("Company cannot exceed 100 characters");
    }
  }

  /**
   * Checks if user is active and not deleted.
   * 
   * @param user the user to check
   * @throws UserNotFoundException if user is not active or deleted
   */
  private void checkUserActive(User user) {
    if (user == null) {
      throw new UserNotFoundException("User not found");
    }

    if (user.isDeleted()) {
      throw new UserNotFoundException("User has been deleted");
    }

    if (!user.isActive()) {
      throw new UserNotFoundException("User is not active");
    }
  }

  /**
   * Logs user activity.
   * 
   * @param userId the user ID
   * @param action the action performed
   */
  private void logUserActivity(UUID userId, String action) {
    log.info("User activity - User ID: {}, Action: {}", userId, action);
    // TODO: Implement activity logging to database or external service
  }

  /**
   * Performs audit trail logging.
   * 
   * @param userId  the user ID
   * @param action  the action performed
   * @param details the action details
   */
  private void auditLog(UUID userId, String action, String details) {
    log.info("Audit log - User ID: {}, Action: {}, Details: {}", userId, action, details);
    // TODO: Implement audit logging to database or external service
  }
}