package com.programmingmukesh.auth.service.auth_service.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmingmukesh.auth.service.auth_service.client.TenantServiceClient;
import com.programmingmukesh.auth.service.auth_service.client.UsersServiceClient;
import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.request.CompleteRegistrationRequest;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateTenantRequest;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.CompleteRegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.dto.response.RegistrationResponse;
import com.programmingmukesh.auth.service.auth_service.dto.response.TenantResponse;
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
    log.info("Starting complete registration for email: {} and company: {}",
        request.getEmail(), request.getCompanyName());

    try {
      // Step 1: Validate the request
      if (!validateRegistrationRequest(request)) {
        throw new RuntimeException("Registration request validation failed");
      }

      // Step 2: Create Tenant
      log.info("Creating tenant for company: {}", request.getCompanyName());
      TenantResponse tenant = createTenant(request);
      log.info("Tenant created successfully with ID: {} and subdomain: {}",
          tenant.getId(), tenant.getSubdomain());

      // Step 3: Create User
      log.info("Creating user for email: {}", request.getEmail());
      UserResponseDTO user = createUser(request, tenant.getId());
      log.info("User created successfully with ID: {}", user.getId());

      // Step 4: Create Auth Credentials
      log.info("Creating auth credentials for user: {}", user.getId());
      RegistrationResponse auth = createAuthCredentials(request, user.getId(), tenant.getId());
      log.info("Auth credentials created successfully with ID: {}", auth.getId());

      // Step 5: Build complete response
      CompleteRegistrationResponse response = CompleteRegistrationResponse.success(user, tenant, auth);

      log.info("Complete registration successful for user: {} in tenant: {}",
          user.getEmail(), tenant.getSubdomain());

      return response;

    } catch (Exception e) {
      log.error("Complete registration failed for email: {} and company: {}",
          request.getEmail(), request.getCompanyName(), e);

      // TODO: Implement compensation/rollback logic here
      // For now, we'll let the exception propagate
      throw new RuntimeException("Registration failed: " + e.getMessage(), e);
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
      if (userResponse.getBody() != null && userResponse.getBody().isSuccess()) {
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
      if (userResponse.getBody() != null && userResponse.getBody().isSuccess()) {
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
      CreateTenantRequest tenantRequest = CreateTenantRequest.fromCompanyAndEmail(companyName, "test@example.com");
      String subdomain = tenantRequest.getSubdomain();

      ApiResponse<Boolean> availabilityResponse = tenantServiceClient.checkSubdomainAvailability(subdomain);
      if (availabilityResponse.isSuccess() && availabilityResponse.getData() != null) {
        return availabilityResponse.getData(); // Return the availability status
      }
    } catch (Exception e) {
      log.warn("Error checking company name availability: {}", companyName, e);
    }

    return false; // Assume not available if we can't check
  }

  /**
   * Creates a tenant for the registration.
   * 
   * @param request the registration request
   * @return the created tenant response
   */
  private TenantResponse createTenant(CompleteRegistrationRequest request) {
    CreateTenantRequest tenantRequest = request.toCreateTenantRequest();

    ApiResponse<TenantResponse> response = tenantServiceClient.createTenant(tenantRequest);

    if (!response.isSuccess() || response.getData() == null) {
      throw new RuntimeException("Failed to create tenant: " + response.getMessage());
    }

    return response.getData();
  }

  /**
   * Creates a user for the registration.
   * 
   * @param request  the registration request
   * @param tenantId the tenant ID
   * @return the created user response
   */
  private UserResponseDTO createUser(CompleteRegistrationRequest request, UUID tenantId) {
    CreateUserRequest userRequest = request.toCreateUserRequest(tenantId);

    ResponseEntity<ApiResponse<UserResponseDTO>> response = usersServiceClient.createUser(userRequest);

    if (!response.getBody().isSuccess() || response.getBody().getData() == null) {
      throw new RuntimeException("Failed to create user: " + response.getBody().getMessage());
    }

    return response.getBody().getData();
  }

  /**
   * Creates auth credentials for the registration.
   * 
   * @param request  the registration request
   * @param userId   the user ID
   * @param tenantId the tenant ID
   * @return the created auth response
   */
  private RegistrationResponse createAuthCredentials(CompleteRegistrationRequest request,
      UUID userId, UUID tenantId) {
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

    // Set the user ID from the created user
    authRequest.setTenantId(tenantId);

    RegistrationResponse response = authService.register(authRequest);

    if (!response.isSuccessful()) {
      throw new RuntimeException("Failed to create auth credentials: " + response.getMessage());
    }

    // Set additional fields
    response.setUserId(userId);
    response.setTenantId(tenantId);

    return response;
  }
}
