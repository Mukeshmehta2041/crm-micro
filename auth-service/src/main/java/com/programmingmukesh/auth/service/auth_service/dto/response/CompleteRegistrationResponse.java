package com.programmingmukesh.auth.service.auth_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmingmukesh.auth.service.auth_service.dto.UserResponseDTO;

/**
 * Complete Registration Response DTO for the integrated signup flow.
 * 
 * <p>
 * This DTO represents the response after successful completion of the
 * integrated registration process that creates Tenant, User, and Auth
 * credentials.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteRegistrationResponse {

  // User Information
  /**
   * The created user's unique identifier.
   */
  private UUID userId;

  /**
   * The user's username.
   */
  private String username;

  /**
   * The user's full name.
   */
  private String fullName;

  /**
   * The user's email address.
   */
  private String email;

  // Tenant Information
  /**
   * The created tenant's unique identifier.
   */
  private UUID tenantId;

  /**
   * The tenant's name/company name.
   */
  private String tenantName;

  /**
   * The tenant's subdomain.
   */
  private String subdomain;

  /**
   * Flag indicating if the tenant is in trial mode.
   */
  private Boolean isTrial;

  /**
   * Trial expiry date (if applicable).
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime trialExpiresAt;

  // Auth Information
  /**
   * The created auth credential's unique identifier.
   */
  private UUID authId;

  /**
   * Flag indicating if email verification is required.
   */
  private Boolean emailVerificationRequired;

  /**
   * Email verification token (if required).
   */
  private String emailVerificationToken;

  // System Information
  /**
   * Registration completion timestamp.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime registeredAt;

  /**
   * Next steps for the user.
   */
  private String nextSteps;

  /**
   * Login URL for the tenant.
   */
  private String loginUrl;

  /**
   * Dashboard URL for the tenant.
   */
  private String dashboardUrl;

  /**
   * Creates a successful registration response.
   * 
   * @param userResponse   the user response
   * @param tenantResponse the tenant response
   * @param authResponse   the auth response
   * @return the complete registration response
   */
  public static CompleteRegistrationResponse success(
      UserResponseDTO userResponse,
      TenantResponse tenantResponse,
      RegistrationResponse authResponse) {

    String loginUrl = "https://" + tenantResponse.getSubdomain() + ".mycrm.com/login";
    String dashboardUrl = "https://" + tenantResponse.getSubdomain() + ".mycrm.com/dashboard";

    String nextSteps = authResponse.getEmailVerified()
        ? "You can now log in to your account and start using the CRM system."
        : "Please check your email and verify your email address before logging in.";

    return CompleteRegistrationResponse.builder()
        .userId(userResponse.getId())
        .username(userResponse.getUsername())
        .fullName(userResponse.getFullName())
        .email(userResponse.getEmail())
        .tenantId(tenantResponse.getId())
        .tenantName(tenantResponse.getName())
        .subdomain(tenantResponse.getSubdomain())
        .isTrial(tenantResponse.getIsTrial())
        .trialExpiresAt(tenantResponse.getTrialEndsAt())
        .authId(authResponse.getId())
        .emailVerificationRequired(!authResponse.getEmailVerified())
        .emailVerificationToken(authResponse.getEmailVerificationToken())
        .registeredAt(LocalDateTime.now())
        .nextSteps(nextSteps)
        .loginUrl(loginUrl)
        .dashboardUrl(dashboardUrl)
        .build();
  }

  /**
   * Gets the full tenant domain.
   * 
   * @return the full tenant domain
   */
  public String getFullDomain() {
    return subdomain + ".mycrm.com";
  }

  /**
   * Checks if the registration is complete and user can log in.
   * 
   * @return true if user can log in immediately
   */
  public boolean canLoginImmediately() {
    return !Boolean.TRUE.equals(emailVerificationRequired);
  }

  /**
   * Gets the days remaining in trial.
   * 
   * @return days remaining in trial, null if not in trial
   */
  public Long getTrialDaysRemaining() {
    if (!Boolean.TRUE.equals(isTrial) || trialExpiresAt == null) {
      return null;
    }

    LocalDateTime now = LocalDateTime.now();
    if (trialExpiresAt.isBefore(now)) {
      return 0L;
    }

    return java.time.Duration.between(now, trialExpiresAt).toDays();
  }
}
