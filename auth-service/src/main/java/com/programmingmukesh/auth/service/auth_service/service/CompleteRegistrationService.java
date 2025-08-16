package com.programmingmukesh.auth.service.auth_service.service;

import com.programmingmukesh.auth.service.auth_service.dto.request.CompleteRegistrationRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.CompleteRegistrationResponse;

/**
 * Service interface for complete registration flow.
 * 
 * <p>
 * This service handles the complete registration process that creates:
 * </p>
 * <ol>
 * <li>Tenant organization</li>
 * <li>User profile</li>
 * <li>Authentication credentials</li>
 * </ol>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public interface CompleteRegistrationService {

  /**
   * Performs complete registration flow.
   * 
   * <p>
   * This method orchestrates the complete registration process:
   * </p>
   * <ol>
   * <li>Creates a tenant organization</li>
   * <li>Creates a user profile in the tenant</li>
   * <li>Creates authentication credentials for the user</li>
   * <li>Sets up initial configurations</li>
   * </ol>
   * 
   * @param request the complete registration request
   * @return the complete registration response
   * @throws RuntimeException if any step in the registration process fails
   */
  CompleteRegistrationResponse performCompleteRegistration(CompleteRegistrationRequest request);

  /**
   * Validates if the registration request is valid and all services are
   * available.
   * 
   * @param request the registration request to validate
   * @return true if the request is valid and services are available
   */
  boolean validateRegistrationRequest(CompleteRegistrationRequest request);

  /**
   * Checks if a username is available across all services.
   * 
   * @param username the username to check
   * @return true if the username is available
   */
  boolean isUsernameAvailable(String username);

  /**
   * Checks if an email is available across all services.
   * 
   * @param email the email to check
   * @return true if the email is available
   */
  boolean isEmailAvailable(String email);

  /**
   * Checks if a company name/subdomain is available.
   * 
   * @param companyName the company name to check
   * @return true if the company name is available
   */
  boolean isCompanyNameAvailable(String companyName);

  /**
   * Gets the current number of ongoing registrations.
   * Useful for monitoring and debugging.
   * 
   * @return the number of ongoing registrations
   */
  int getOngoingRegistrationsCount();

  /**
   * Gets the current ongoing registrations for monitoring.
   * 
   * @return a copy of the ongoing registrations map
   */
  java.util.concurrent.ConcurrentMap<String, Boolean> getOngoingRegistrations();

  /**
   * Clears all ongoing registrations.
   * Use with caution - only for service restart or emergency cleanup.
   */
  void clearOngoingRegistrations();
}
