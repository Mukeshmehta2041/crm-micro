package com.programmingmukesh.tenant.service.tenant_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmingmukesh.tenant.service.tenant_service.entity.PlanType;
import com.programmingmukesh.tenant.service.tenant_service.entity.TenantStatus;

/**
 * Tenant Response DTO for tenant information retrieval.
 * 
 * <p>
 * This DTO represents tenant information returned by the API.
 * It includes all relevant tenant details for client consumption.
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
public class TenantResponse {

  /**
   * Unique identifier for the tenant.
   */
  private UUID id;

  /**
   * Tenant name or organization name.
   */
  private String name;

  /**
   * Unique subdomain for the tenant.
   */
  private String subdomain;

  /**
   * Subscription plan type for the tenant.
   */
  private PlanType planType;

  /**
   * Current status of the tenant.
   */
  private TenantStatus status;

  /**
   * Maximum number of users allowed for this tenant.
   */
  private Integer maxUsers;

  /**
   * Maximum storage in GB allowed for this tenant.
   */
  private Integer maxStorageGb;

  /**
   * Custom domain for the tenant (optional).
   */
  private String customDomain;

  /**
   * URL to the tenant's logo image.
   */
  private String logoUrl;

  /**
   * Primary brand color (hex code).
   */
  private String primaryColor;

  /**
   * Secondary brand color (hex code).
   */
  private String secondaryColor;

  /**
   * Contact email for the tenant.
   */
  private String contactEmail;

  /**
   * Contact phone number for the tenant.
   */
  private String contactPhone;

  /**
   * Billing email for invoices and payment notifications.
   */
  private String billingEmail;

  /**
   * Timestamp when the subscription expires.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime subscriptionExpiresAt;

  /**
   * Timestamp when the trial period ends.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime trialEndsAt;

  /**
   * Flag indicating whether the tenant is in trial mode.
   */
  private Boolean isTrial;

  /**
   * Timestamp when the tenant was created.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * Timestamp when the tenant was last updated.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  /**
   * Computed field indicating if the tenant is currently active.
   */
  private Boolean isActive;

  /**
   * Computed field indicating if the tenant is currently in trial.
   */
  private Boolean isInTrial;

  /**
   * Computed field indicating if the subscription is expired.
   */
  private Boolean isSubscriptionExpired;

  /**
   * Computed field indicating if the trial is expired.
   */
  private Boolean isTrialExpired;

  /**
   * Gets the full domain for the tenant (custom domain if available, otherwise
   * subdomain).
   * 
   * @return the full domain for the tenant
   */
  public String getFullDomain() {
    return customDomain != null ? customDomain : subdomain + ".mycrm.com";
  }

  /**
   * Gets the display name for the tenant (name if available, otherwise
   * subdomain).
   * 
   * @return the display name for the tenant
   */
  public String getDisplayName() {
    return name != null && !name.trim().isEmpty() ? name : subdomain;
  }

  /**
   * Gets the primary contact email (contact email if available, otherwise billing
   * email).
   * 
   * @return the primary contact email
   */
  public String getPrimaryContactEmail() {
    return contactEmail != null && !contactEmail.trim().isEmpty() ? contactEmail : billingEmail;
  }

  /**
   * Checks if the tenant has custom branding configured.
   * 
   * @return true if custom branding is configured, false otherwise
   */
  public boolean hasCustomBranding() {
    return (logoUrl != null && !logoUrl.trim().isEmpty()) ||
        (primaryColor != null && !primaryColor.trim().isEmpty()) ||
        (secondaryColor != null && !secondaryColor.trim().isEmpty());
  }

  /**
   * Checks if the tenant has contact information configured.
   * 
   * @return true if contact information is configured, false otherwise
   */
  public boolean hasContactInfo() {
    return (contactEmail != null && !contactEmail.trim().isEmpty()) ||
        (contactPhone != null && !contactPhone.trim().isEmpty());
  }

  /**
   * Gets the number of days remaining in trial (if applicable).
   * 
   * @return the number of days remaining in trial, null if not in trial
   */
  public Long getTrialDaysRemaining() {
    if (!Boolean.TRUE.equals(isTrial) || trialEndsAt == null) {
      return null;
    }

    LocalDateTime now = LocalDateTime.now();
    if (trialEndsAt.isBefore(now)) {
      return 0L;
    }

    return java.time.Duration.between(now, trialEndsAt).toDays();
  }

  /**
   * Gets the number of days remaining in subscription (if applicable).
   * 
   * @return the number of days remaining in subscription, null if in trial or no
   *         expiry
   */
  public Long getSubscriptionDaysRemaining() {
    if (Boolean.TRUE.equals(isTrial) || subscriptionExpiresAt == null) {
      return null;
    }

    LocalDateTime now = LocalDateTime.now();
    if (subscriptionExpiresAt.isBefore(now)) {
      return 0L;
    }

    return java.time.Duration.between(now, subscriptionExpiresAt).toDays();
  }
}
