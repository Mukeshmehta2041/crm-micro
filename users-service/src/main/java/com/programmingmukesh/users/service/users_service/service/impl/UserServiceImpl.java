package com.programmingmukesh.users.service.users_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.programmingmukesh.users.service.users_service.exception.CacheException;
import com.programmingmukesh.users.service.users_service.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.request.UpdateUserRequest;
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
import com.programmingmukesh.users.service.users_service.specification.UserSpecification;

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

  private static final int MAX_PAGE_SIZE = 100;

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  @CircuitBreaker(name = "database", fallbackMethod = "createUserFallback")
  @Retry(name = "default")
  @CacheEvict(value = "users", allEntries = true)
  public UserResponse createUser(CreateUserRequest request) {
    log.info("Creating user with username: {}", request.getUsername());

    try {
      validateCreateUserRequest(request);
      checkUserExists(request.getUsername(), request.getEmail());

      User user = userMapper.toEntity(request);

      if (user.getTenantId() == null) {
        user.setTenantId(UUID.randomUUID());
      }

      User savedUser = userRepository.save(user);
      log.info("User created successfully with ID: {}", savedUser.getId());

      publishUserCreatedEvent(savedUser);
      logUserActivity(savedUser.getId(), "USER_CREATED");

      return userMapper.toResponse(savedUser);

    } catch (DataIntegrityViolationException e) {
      log.error("Data integrity violation while creating user: {}", e.getMessage());
      handleDataIntegrityViolation(e);
      throw new UserAlreadyExistsException("User with provided username or email already exists");
    }
  }

  /**
   * Fallback method for createUser when circuit breaker is open.
   * This should only be called for actual system failures, not business
   * exceptions.
   */
  public UserResponse createUserFallback(CreateUserRequest request, Exception ex) {
    log.error("Create user fallback triggered for username: {}, error: {}",
        request.getUsername(), ex.getMessage());
    throw new ServiceUnavailableException(
        "We're currently experiencing high demand. Your user creation request couldn't be processed at this time. Please try again in a few minutes.");
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "users", key = "#userId", unless = "#result == null")
  @CircuitBreaker(name = "database", fallbackMethod = "getUserByIdFallback")
  @Retry(name = "default")
  public UserResponse getUserById(UUID userId) {
    log.debug("Fetching user by ID: {}", userId);

    validateUserId(userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.userNotFoundById(userId.toString()));

    checkUserActive(user);
    return userMapper.toResponse(user);
  }

  /**
   * Fallback method for getUserById when circuit breaker is open.
   */
  public UserResponse getUserByIdFallback(UUID userId, Exception ex) {
    log.error("Get user by ID fallback triggered for ID: {}, error: {}", userId, ex.getMessage());
    throw new ServiceUnavailableException(
        "We're experiencing temporary service issues. Please try to retrieve the user information again in a few minutes.");
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "users", key = "'username:' + #username", unless = "#result == null")
  @CircuitBreaker(name = "database", fallbackMethod = "getUserByUsernameFallback")
  @Retry(name = "default")
  public UserResponse getUserByUsername(String username) {
    log.debug("Fetching user by username: {}", username);

    validateUsername(username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> UserNotFoundException.userNotFoundByUsername(username));

    checkUserActive(user);
    return userMapper.toResponse(user);
  }

  /**
   * Fallback method for getUserByUsername when circuit breaker is open.
   */
  public UserResponse getUserByUsernameFallback(String username, Exception ex) {
    log.error("Get user by username fallback triggered for username: {}, error: {}", username, ex.getMessage());
    throw new ServiceUnavailableException(
        "We're experiencing temporary service issues. Please try to retrieve the user information again in a few minutes.");
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "users", key = "'email:' + #email", unless = "#result == null")
  @CircuitBreaker(name = "database", fallbackMethod = "getUserByEmailFallback")
  @Retry(name = "default")
  public UserResponse getUserByEmail(String email) {
    log.debug("Fetching user by email: {}", email);

    validateEmail(email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> UserNotFoundException.userNotFoundByEmail(email));

    checkUserActive(user);
    return userMapper.toResponse(user);
  }

  /**
   * Fallback method for getUserByEmail when circuit breaker is open.
   */
  public UserResponse getUserByEmailFallback(String email, Exception ex) {
    log.error("Get user by email fallback triggered for email: {}, error: {}", email, ex.getMessage());
    throw new ServiceUnavailableException(
        "We're experiencing temporary service issues. Please try to retrieve the user information again in a few minutes.");
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
  @CacheEvict(value = "users", allEntries = true)
  public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
    log.info("Updating user with ID: {}", userId);

    validateUserId(userId);
    validateUpdateUserRequest(request);

    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    checkUserActive(existingUser);
    checkUserUpdateConflicts(existingUser, request);

    User updatedUser = userMapper.updateEntity(existingUser, request);
    User savedUser = userRepository.save(updatedUser);

    log.info("User updated successfully with ID: {}", savedUser.getId());
    logUserActivity(userId, "USER_UPDATED");
    publishUserUpdatedEvent(savedUser);

    return userMapper.toResponse(savedUser);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", allEntries = true)
  public void deleteUser(UUID userId) {
    log.info("Deleting user with ID: {}", userId);

    validateUserId(userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    if (user.isDeleted()) {
      throw new UserNotFoundException("User is already deleted");
    }

    user.softDelete();
    userRepository.save(user);

    log.info("User deleted successfully with ID: {}", userId);
    logUserActivity(userId, "USER_DELETED");
    publishUserDeletedEvent(user);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", allEntries = true)
  public void activateUser(UUID userId) {
    log.info("Activating user with ID: {}", userId);

    validateUserId(userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    if (user.isDeleted()) {
      throw new UserNotFoundException("Cannot activate deleted user");
    }

    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    log.info("User activated successfully with ID: {}", userId);
    logUserActivity(userId, "USER_ACTIVATED");
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", allEntries = true)
  public void deactivateUser(UUID userId) {
    log.info("Deactivating user with ID: {}", userId);

    validateUserId(userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    if (user.isDeleted()) {
      throw new UserNotFoundException("Cannot deactivate deleted user");
    }

    user.setStatus(UserStatus.INACTIVE);
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    log.info("User deactivated successfully with ID: {}", userId);
    logUserActivity(userId, "USER_DEACTIVATED");
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

  @Override
  @Transactional
  @CacheEvict(value = "users", allEntries = true)
  public UserResponse patchUser(UUID userId, UpdateUserRequest request) {
    log.info("Partially updating user with ID: {}", userId);

    validateUserId(userId);
    validateUpdateUserRequest(request);

    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    checkUserActive(existingUser);
    checkUserUpdateConflicts(existingUser, request);

    User updatedUser = userMapper.updateEntity(existingUser, request);
    User savedUser = userRepository.save(updatedUser);

    log.info("User partially updated successfully with ID: {}", savedUser.getId());
    logUserActivity(userId, "USER_PATCHED");
    publishUserUpdatedEvent(savedUser);

    return userMapper.toResponse(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "user-search", key = "#query + ':' + #department + ':' + #company + ':' + #status + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
  @CircuitBreaker(name = "database", fallbackMethod = "searchUsersFallback")
  @Retry(name = "default")
  public Page<UserResponse> searchUsers(String query, String department, String company, String status,
      Pageable pageable) {
    log.debug("Searching users with query: {}, department: {}, company: {}, status: {}",
        query, department, company, status);

    if (pageable.getPageSize() > MAX_PAGE_SIZE) {
      throw new UserValidationException("Page size cannot exceed " + MAX_PAGE_SIZE);
    }

    var specification = UserSpecification.createSearchSpecification(query, department, company, status);
    Page<User> users = userRepository.findAll(specification, pageable);

    return users.map(userMapper::toResponse);
  }

  /**
   * Fallback method for searchUsers when circuit breaker is open.
   */
  public Page<UserResponse> searchUsersFallback(String query, String department, String company, String status,
      Pageable pageable, Exception ex) {
    log.error("Search users fallback triggered, error: {}", ex.getMessage());
    throw new ServiceUnavailableException("User search service is temporarily unavailable. Please try again later.");
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
  private void validateUpdateUserRequest(UpdateUserRequest request) {
    if (request == null) {
      throw new UserValidationException("Update user request cannot be null");
    }

    if (!request.hasUpdates()) {
      throw new UserValidationException("At least one field must be provided for update");
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

    // Validate username format if provided
    if (request.getUsername() != null) {
      validateUsername(request.getUsername());
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
  private void checkUserUpdateConflicts(User existingUser, UpdateUserRequest request) {
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
   * Validates username.
   * 
   * @param username the username to validate
   * @throws UserValidationException if username is invalid
   */
  private void validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw UserValidationException.fieldRequired("Username");
    }

    if (username.length() > 50) {
      throw UserValidationException.fieldTooLong("Username", 50);
    }

    if (!username.matches("^[a-zA-Z0-9_]+$")) {
      throw UserValidationException.invalidUsername(username);
    }
  }

  /**
   * Validates email.
   * 
   * @param email the email to validate
   * @throws UserValidationException if email is invalid
   */
  private void validateEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw UserValidationException.fieldRequired("Email");
    }

    if (email.length() > 255) {
      throw UserValidationException.fieldTooLong("Email", 255);
    }

    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
      throw UserValidationException.invalidEmail(email);
    }
  }

  /**
   * Validates department.
   * 
   * @param department the department to validate
   * @throws UserValidationException if department is invalid
   */
  private void validateDepartment(String department) {
    if (department == null || department.trim().isEmpty()) {
      throw UserValidationException.fieldRequired("Department");
    }

    if (department.length() > 100) {
      throw UserValidationException.fieldTooLong("Department", 100);
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
      throw UserValidationException.fieldRequired("Company");
    }

    if (company.length() > 100) {
      throw UserValidationException.fieldTooLong("Company", 100);
    }
  }

  /**
   * Validates age.
   * 
   * @param age the age to validate
   * @throws UserValidationException if age is invalid
   */
  private void validateAge(Integer age) {
    if (age == null) {
      throw UserValidationException.fieldRequired("Age");
    }

    if (age < 13 || age > 120) {
      throw UserValidationException.invalidAge(age);
    }
  }

  /**
   * Validates first name.
   * 
   * @param firstName the first name to validate
   * @throws UserValidationException if first name is invalid
   */
  private void validateFirstName(String firstName) {
    if (firstName == null || firstName.trim().isEmpty()) {
      throw UserValidationException.fieldRequired("First name");
    }

    if (firstName.length() > 50) {
      throw UserValidationException.fieldTooLong("First name", 50);
    }
  }

  /**
   * Validates last name.
   * 
   * @param lastName the last name to validate
   * @throws UserValidationException if last name is invalid
   */
  private void validateLastName(String lastName) {
    if (lastName == null || lastName.trim().isEmpty()) {
      throw UserValidationException.fieldRequired("Last name");
    }

    if (lastName.length() > 50) {
      throw UserValidationException.fieldTooLong("Last name", 50);
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
      throw new UserNotFoundException("User account has been deleted and is no longer accessible");
    }

    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new UserNotFoundException("User account is currently inactive. Please contact support for assistance.");
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

  /**
   * Handles data integrity violations and provides meaningful error messages.
   */
  private void handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    String message = ex.getMessage();
    if (message != null) {
      if (message.contains("username") || message.contains("idx_users_username")) {
        log.warn("Username constraint violation: {}", message);
        throw UserAlreadyExistsException.usernameAlreadyExists("the requested username");
      } else if (message.contains("email") || message.contains("idx_users_email")) {
        log.warn("Email constraint violation: {}", message);
        throw UserAlreadyExistsException.emailAlreadyExists("the requested email");
      } else {
        log.warn("Data integrity constraint violation: {}", message);
        throw new UserAlreadyExistsException("User with provided information already exists");
      }
    }
    throw new UserAlreadyExistsException("User with provided information already exists");
  }

  /**
   * Safely handles cache operations with error recovery.
   */
  private void safeCacheOperation(Runnable cacheOperation, String operationName) {
    try {
      cacheOperation.run();
    } catch (Exception e) {
      log.warn("Cache operation '{}' failed: {}", operationName, e.getMessage());
      // Don't throw exception, let the main operation continue
    }
  }

}