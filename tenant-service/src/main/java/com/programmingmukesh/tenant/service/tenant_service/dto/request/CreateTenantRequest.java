package com.programmingmukesh.tenant.service.tenant_service.dto.request;

import java.time.LocalDateTime;

import lombok.*;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmingmukesh.tenant.service.tenant_service.entity.PlanType;

/**
 * Create Tenant Request DTO for tenant registration and creation.
 * 
 * <p>
 * This DTO represents the data required to create a new tenant organization.
 * It includes validation annotations to ensure data integrity.
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
public class CreateTenantRequest {

  /**
   * Tenant name or organization name.
   */
  @NotBlank(message = "Tenant name cannot be blank")
  @Size(min = 1, max = 255, message = "Tenant name must be between 1 and 255 characters")
  private String name;

  /**
   * Unique subdomain for the tenant (e.g., acme.mycrm.com).
   */
  @NotBlank(message = "Subdomain cannot be blank")
  @Size(min = 3, max = 100, message = "Subdomain must be between 3 and 100 characters")
  @Pattern(regexp = "^[a-z0-9]([a-z0-9-]*[a-z0-9])?$", message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
  private String subdomain;

  /**
   * Subscription plan type for the tenant.
   */
  private PlanType planType = PlanType.TRIAL;

  /**
   * Maximum number of users allowed for this tenant.
   */
  @Min(value = 1, message = "Max users must be at least 1")
  @Max(value = 10000, message = "Max users cannot exceed 10000")
  private Integer maxUsers = 10;

  /**
   * Maximum storage in GB allowed for this tenant.
   */
  @Min(value = 1, message = "Max storage must be at least 1 GB")
  @Max(value = 10000, message = "Max storage cannot exceed 10000 GB")
  private Integer maxStorageGb = 100;

  /**
   * Custom domain for the tenant (optional).
   */
  @Size(max = 255, message = "Custom domain cannot exceed 255 characters")
  @Pattern(regexp = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+(([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?))$", message = "Custom domain must be a valid domain name")
  private String customDomain;

  /**
   * URL to the tenant's logo image.
   */
  @Size(max = 500, message = "Logo URL cannot exceed 500 characters")
  private String logoUrl;

  /**
   * Primary brand color (hex code).
   */
  @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Primary color must be a valid hex color code")
  private String primaryColor;

  /**
   * Secondary brand color (hex code).
   */
  @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Secondary color must be a valid hex color code")
  private String secondaryColor;

  /**
   * Contact email for the tenant.
   */
  @Email(message = "Contact email must be in valid format")
  @Size(max = 255, message = "Contact email cannot exceed 255 characters")
  private String contactEmail;

  /**
   * Contact phone number for the tenant.
   */
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Contact phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Contact phone cannot exceed 20 characters")
  private String contactPhone;

  /**
   * Billing email for invoices and payment notifications.
   */
  @Email(message = "Billing email must be in valid format")
  @Size(max = 255, message = "Billing email cannot exceed 255 characters")
  private String billingEmail;

  /**
   * Timestamp when the trial period should end (optional).
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime trialEndsAt;

  /**
   * Flag indicating whether the tenant should start in trial mode.
   */
  private Boolean isTrial = true;

  /**
   * Validates that the request contains minimum required information.
   * 
   * @return true if the request has minimum required information
   */
  public boolean hasMinimumRequiredInfo() {
    return name != null && !name.trim().isEmpty() &&
        subdomain != null && !subdomain.trim().isEmpty();
  }

  /**
   * Validates that the subdomain format is correct.
   * 
   * @return true if the subdomain format is valid
   */
  public boolean isSubdomainValid() {
    return subdomain != null &&
        subdomain.length() >= 3 &&
        subdomain.length() <= 100 &&
        subdomain.matches("^[a-z0-9]([a-z0-9-]*[a-z0-9])?$");
  }

  /**
   * Validates that at least one contact method is provided.
   * 
   * @return true if at least one contact method is provided
   */
  public boolean hasContactMethod() {
    return (contactEmail != null && !contactEmail.trim().isEmpty()) ||
        (contactPhone != null && !contactPhone.trim().isEmpty());
  }

  /**
   * Sets default values for trial tenants.
   */
  public void setTrialDefaults() {
    if (isTrial == null || isTrial) {
      this.planType = PlanType.TRIAL;
      this.maxUsers = Math.min(maxUsers != null ? maxUsers : 5, 5); // Trial limited to 5 users
      this.maxStorageGb = Math.min(maxStorageGb != null ? maxStorageGb : 10, 10); // Trial limited to 10GB

      if (trialEndsAt == null) {
        this.trialEndsAt = LocalDateTime.now().plusDays(14); // Default 14-day trial
      }
    }
  }
}
