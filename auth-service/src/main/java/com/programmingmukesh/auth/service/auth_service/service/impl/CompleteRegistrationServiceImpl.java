package com.programmingmukesh.auth.service.auth_service.service.impl;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmingmukesh.auth.service.auth_service.client.TenantServiceClient;
import com.programmingmukesh.auth.service.auth_service.client.UsersServiceClient;
import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.request.CompleteRegistrationRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.CompleteRegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.dto.response.RegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.dto.UserResponseDTO;
import com.programmingmukesh.auth.service.auth_service.service.AuthService;
import com.programmingmukesh.auth.service.auth_service.service.CompleteRegistrationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CompleteRegistrationService.
 * 
 * <p>
 * This service orchestrates the complete registration flow by coordinating
 * with Tenant Service, Users Service, and Auth Service.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Service
@Transactional
@Slf4j
public class CompleteRegistrationServiceImpl implements CompleteRegistrationService {

  private final TenantServiceClient tenantServiceClient;
  private final UsersServiceClient usersServiceClient;
  private final AuthService authService;

  // Track ongoing registrations to prevent duplicates
  private final ConcurrentMap<String, Boolean> ongoingRegistrations = new ConcurrentHashMap<>();

  @Autowired
  public CompleteRegistrationServiceImpl(
      TenantServiceClient tenantServiceClient,
      UsersServiceClient usersServiceClient,
      AuthService authService) {
    this.tenantServiceClient = tenantServiceClient;
    this.usersServiceClient = usersServiceClient;
    this.authService = authService;
  }

  @Override
  public CompleteRegistrationResponse performCompleteRegistration(CompleteRegistrationRequest request) {
    // Generate a unique request ID for tracking
    String requestId = "req-" + System.currentTimeMillis() + "-" + Thread.currentThread().threadId();

    // Create a unique key for deduplication
    String dedupKey = request.getEmail() + "|" + request.getCompanyName();

    log.info("[{}] Starting complete registration for email: {} and company: {}",
        requestId, request.getEmail(), request.getCompanyName());

    // Check if registration is already in progress for this email/company
    if (ongoingRegistrations.putIfAbsent(dedupKey, true) != null) {
      log.warn("[{}] Registration already in progress for email: {} and company: {}",
          requestId, request.getEmail(), request.getCompanyName());
      throw new RuntimeException("Registration is already in progress for this email and company. Please wait.");
    }

    try {
      // Step 1: Validate the request
      if (!validateRegistrationRequest(request)) {
        log.warn("[{}] Registration request validation failed", requestId);
        throw new RuntimeException("Registration request validation failed");
      }

      // Step 2: Create Tenant
      // log.info("[{}] Creating tenant for company: {}", requestId,
      // request.getCompanyName());
      // TenantResponse tenant = createTenant(request);
      // log.info("[{}] Tenant created successfully with ID: {} and subdomain: {}",
      // requestId, tenant.getId(), tenant.getSubdomain());

      UUID tenantId = UUID.randomUUID();

      // Step 3: Create Auth Credentials (this will also create the user)
      log.info("[{}] Creating auth credentials and user for email: {}", requestId, request.getEmail());
      RegistrationResponse auth = createAuthCredentials(request, tenantId);
      log.info("[{}] Auth credentials and user created successfully with ID: {}", requestId, auth.getId());

      // Step 4: Build complete response
      CompleteRegistrationResponse response = CompleteRegistrationResponse.success(null, null, auth);

      log.info("[{}] Complete registration successful for user: {} in tenant: {}",
          requestId, request.getEmail(), tenantId);

      return response;

    } catch (Exception e) {
      log.error("[{}] Complete registration failed for email: {} and company: {}",
          requestId, request.getEmail(), request.getCompanyName(), e);

      // TODO: Implement compensation/rollback logic here
      // For now, we'll let the exception propagate
      throw new RuntimeException("Registration failed: " + e.getMessage(), e);
    } finally {
      // Always remove from ongoing registrations
      ongoingRegistrations.remove(dedupKey);
      log.debug("[{}] Removed registration from ongoing list for key: {}", requestId, dedupKey);
    }
  }

  @Override
  public boolean validateRegistrationRequest(CompleteRegistrationRequest request) {
    if (request == null || !request.isValid()) {
      log.warn("Registration request is null or invalid");
      return false;
    }

    // Check if email is already taken
    if (!isEmailAvailable(request.getEmail())) {
      log.warn("Email is already taken: {}", request.getEmail());
      return false;
    }

    // Check if username is already taken
    if (!isUsernameAvailable(request.getUsername())) {
      log.warn("Username is already taken: {}", request.getUsername());
      return false;
    }

    // Check if company name/subdomain is available
    if (!isCompanyNameAvailable(request.getCompanyName())) {
      log.warn("Company name/subdomain is not available: {}", request.getCompanyName());
      return false;
    }

    return true;
  }

  @Override
  public boolean isUsernameAvailable(String username) {
    try {
      // Check with Users Service
      ResponseEntity<ApiResponse<UserResponseDTO>> userResponse = usersServiceClient.getUserByUsername(username);
      ApiResponse<UserResponseDTO> responseBody = userResponse.getBody();
      if (responseBody != null && responseBody.isSuccess()) {
        return false; // User exists, username not available
      }
    } catch (Exception e) {
      log.debug("Username not found in users service (this is good): {}", username);
    }

    return true; // Username is available
  }

  @Override
  public boolean isEmailAvailable(String email) {
    try {
      // Check with Users Service
      ResponseEntity<ApiResponse<UserResponseDTO>> userResponse = usersServiceClient.getUserByEmail(email);
      ApiResponse<UserResponseDTO> responseBody = userResponse.getBody();
      if (responseBody != null && responseBody.isSuccess()) {
        return false; // User exists, email not available
      }
    } catch (Exception e) {
      log.debug("Email not found in users service (this is good): {}", email);
    }

    return true; // Email is available
  }

  @Override
  public boolean isCompanyNameAvailable(String companyName) {
    try {
      String baseSubdomain = generateBaseSubdomain(companyName);
      log.info("Checking company name availability for '{}' with base subdomain '{}'", companyName, baseSubdomain);

      // First check if the base subdomain is available
      if (isSubdomainAvailableWithRetry(baseSubdomain)) {
        log.info("Company name '{}' is available with subdomain '{}'", companyName, baseSubdomain);
        return true;
      }

      // If base subdomain is taken, check if it's the same company
      log.info("Base subdomain '{}' is taken, checking if it's the same company", baseSubdomain);

      // For now, we'll assume that if the base subdomain is taken, the company name
      // is not available
      // In a real implementation, you might want to check if the existing tenant has
      // the same company name
      log.warn("Company name '{}' is not available - subdomain '{}' is already taken", companyName, baseSubdomain);
      return false;

    } catch (Exception e) {
      log.warn("Error checking company name availability: {}", companyName, e);
      // If we can't check availability due to service issues, allow the registration
      // to proceed
      // The tenant creation will handle subdomain conflicts at that level
      log.info(
          "Allowing registration to proceed despite availability check failure - will handle conflicts during tenant creation");
      return true;
    }
  }

  /**
   * Checks if a subdomain is available with retry mechanism.
   * 
   * @param subdomain the subdomain to check
   * @return true if available, false if taken
   */
  private boolean isSubdomainAvailableWithRetry(String subdomain) {
    int maxRetries = 3;
    int retryCount = 0;

    while (retryCount < maxRetries) {
      try {
        return isSubdomainAvailable(subdomain);
      } catch (Exception e) {
        retryCount++;
        if (retryCount >= maxRetries) {
          log.warn("Failed to check subdomain availability after {} retries for '{}': {}",
              maxRetries, subdomain, e.getMessage());
          // On final retry failure, assume available to allow registration to proceed
          return true;
        }
        log.debug("Retry {} for subdomain availability check '{}'", retryCount, subdomain);
        try {
          Thread.sleep(1000 * retryCount); // Exponential backoff
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }

    return true; // Default to available if all retries fail
  }

  /**
   * Gets the current number of ongoing registrations.
   * Useful for monitoring and debugging.
   * 
   * @return the number of ongoing registrations
   */
  public int getOngoingRegistrationsCount() {
    return ongoingRegistrations.size();
  }

  /**
   * Gets the current ongoing registrations for monitoring.
   * 
   * @return a copy of the ongoing registrations map
   */
  public ConcurrentMap<String, Boolean> getOngoingRegistrations() {
    return new ConcurrentHashMap<>(ongoingRegistrations);
  }

  /**
   * Clears all ongoing registrations.
   * Use with caution - only for service restart or emergency cleanup.
   */
  public void clearOngoingRegistrations() {
    int count = ongoingRegistrations.size();
    ongoingRegistrations.clear();
    log.warn("Cleared {} ongoing registrations", count);
  }

  /**
   * Generates the base subdomain from company name.
   * 
   * @param companyName the company name
   * @return the base subdomain
   */
  private String generateBaseSubdomain(String companyName) {
    if (companyName == null || companyName.trim().isEmpty()) {
      return "company";
    }

    // Convert to lowercase, remove special characters, replace spaces with hyphens
    String subdomain = companyName.toLowerCase()
        .replaceAll("[^a-z0-9\\s-]", "")
        .replaceAll("\\s+", "-")
        .replaceAll("-+", "-")
        .replaceAll("^-|-$", "");

    // Ensure it starts and ends with alphanumeric character
    if (subdomain.length() > 0 && !Character.isLetterOrDigit(subdomain.charAt(0))) {
      subdomain = "company-" + subdomain;
    }

    if (subdomain.length() > 0 && !Character.isLetterOrDigit(subdomain.charAt(subdomain.length() - 1))) {
      subdomain = subdomain + "-co";
    }

    // Limit length
    if (subdomain.length() > 50) {
      subdomain = subdomain.substring(0, 50);
    }

    // Ensure minimum length
    if (subdomain.length() < 3) {
      subdomain = subdomain + "-company";
    }

    return subdomain;
  }

  /**
   * Checks if a subdomain is available by calling the tenant service.
   * 
   * @param subdomain the subdomain to check
   * @return true if available, false if taken
   */
  private boolean isSubdomainAvailable(String subdomain) {
    try {
      log.debug("Checking subdomain availability: {}", subdomain);
      ApiResponse<Boolean> availabilityResponse = tenantServiceClient.checkSubdomainAvailability(subdomain);
      if (availabilityResponse.isSuccess() && availabilityResponse.getData() != null) {
        boolean available = availabilityResponse.getData();
        log.debug("Subdomain '{}' availability check result: {}", subdomain, available);
        return available;
      }
      // If we can't check availability, assume it's not available to be safe
      log.warn("Could not determine availability for subdomain '{}', assuming not available", subdomain);
      return false;
    } catch (Exception e) {
      log.warn("Error checking subdomain availability for '{}': {}", subdomain, e.getMessage());
      // If we can't check availability due to service issues, assume it's available
      // This allows the registration to proceed and handle conflicts during tenant
      // creation
      log.info("Service unavailable for subdomain check '{}', assuming available to allow registration", subdomain);
      return true;
    }
  }

  /**
   * Creates auth credentials for the registration.
   * This method will create both the user (via AuthService) and the auth
   * credentials.
   * 
   * @param request  the registration request
   * @param tenantId the tenant ID
   * @return the created auth response
   */
  private RegistrationResponse createAuthCredentials(CompleteRegistrationRequest request, UUID tenantId) {
    com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest authRequest = com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest
        .builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(request.getPassword())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .displayName(request.getFullName())
        .tenantId(tenantId)
        .phoneNumber(request.getPhoneNumber())
        .jobTitle(request.getJobTitle())
        .department(request.getDepartment())
        .company(request.getCompanyName())
        .timezone(request.getTimezone())
        .language(request.getLanguage())
        .gdprConsentGiven(true) // Required for registration
        .marketingConsentGiven(request.getMarketingConsent())
        .build();

    RegistrationResponse response = authService.register(authRequest);

    if (!response.isSuccessful()) {
      throw new RuntimeException("Failed to create auth credentials: " + response.getMessage());
    }

    // Set tenant ID in response
    response.setTenantId(tenantId);

    return response;
  }
}
