package com.programmingmukesh.auth.service.auth_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Tenant Response DTO for tenant information from Tenant Service.
 * 
 * <p>
 * This DTO represents tenant information returned by the Tenant Service
 * for use in the Auth Service.
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
   * Current status of the tenant.
   */
  private String status;

  /**
   * Maximum number of users allowed for this tenant.
   */
  private Integer maxUsers;

  /**
   * Maximum storage in GB allowed for this tenant.
   */
  private Integer maxStorageGb;

  /**
   * Contact email for the tenant.
   */
  private String contactEmail;

  /**
   * Billing email for invoices and payment notifications.
   */
  private String billingEmail;

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
   * Computed field indicating if the tenant is currently active.
   */
  private Boolean isActive;

  /**
   * Computed field indicating if the tenant is currently in trial.
   */
  private Boolean isInTrial;
}
