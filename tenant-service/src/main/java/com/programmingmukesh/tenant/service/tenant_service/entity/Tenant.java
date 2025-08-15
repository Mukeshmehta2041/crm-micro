package com.programmingmukesh.tenant.service.tenant_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Tenant entity representing tenant organizations in the multi-tenant CRM
 * system.
 * 
 * <p>
 * This entity stores all tenant-related information including:
 * </p>
 * <ul>
 * <li>Basic tenant information (name, subdomain)</li>
 * <li>Subscription and plan details</li>
 * <li>Limits and quotas (users, storage)</li>
 * <li>Branding and customization settings</li>
 * <li>Contact and billing information</li>
 * <li>Trial and subscription management</li>
 * </ul>
 * 
 * <p>
 * <strong>Database Indexes:</strong>
 * </p>
 * <ul>
 * <li>Primary key on id (UUID)</li>
 * <li>Unique index on subdomain for fast lookups</li>
 * <li>Index on status for active tenant queries</li>
 * <li>Index on planType for subscription queries</li>
 * <li>Index on customDomain for domain-based routing</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenants_subdomain", columnList = "subdomain", unique = true),
    @Index(name = "idx_tenants_status", columnList = "status"),
    @Index(name = "idx_tenants_plan_type", columnList = "plan_type"),
    @Index(name = "idx_tenants_custom_domain", columnList = "custom_domain"),
    @Index(name = "idx_tenants_contact_email", columnList = "contact_email")
})
@Entity
public class Tenant extends BaseEntity {

  /**
   * Tenant name or organization name.
   */
  @Column(name = "name", nullable = false, length = 255)
  @NotBlank(message = "Tenant name cannot be blank")
  @Size(min = 1, max = 255, message = "Tenant name must be between 1 and 255 characters")
  private String name;

  /**
   * Unique subdomain for the tenant (e.g., acme.mycrm.com).
   */
  @Column(name = "subdomain", nullable = false, unique = true, length = 100)
  @NotBlank(message = "Subdomain cannot be blank")
  @Size(min = 3, max = 100, message = "Subdomain must be between 3 and 100 characters")
  @Pattern(regexp = "^[a-z0-9]([a-z0-9-]*[a-z0-9])?$", message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
  private String subdomain;

  /**
   * Subscription plan type for the tenant.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "plan_type", nullable = false, length = 50)
  @NotNull(message = "Plan type cannot be null")
  private PlanType planType = PlanType.BASIC;

  /**
   * Current status of the tenant.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @NotNull(message = "Status cannot be null")
  private TenantStatus status = TenantStatus.ACTIVE;

  /**
   * Maximum number of users allowed for this tenant.
   */
  @Column(name = "max_users")
  @Min(value = 1, message = "Max users must be at least 1")
  @Max(value = 10000, message = "Max users cannot exceed 10000")
  private Integer maxUsers = 10;

  /**
   * Maximum storage in GB allowed for this tenant.
   */
  @Column(name = "max_storage_gb")
  @Min(value = 1, message = "Max storage must be at least 1 GB")
  @Max(value = 10000, message = "Max storage cannot exceed 10000 GB")
  private Integer maxStorageGb = 100;

  /**
   * Custom domain for the tenant (optional).
   */
  @Column(name = "custom_domain", length = 255)
  @Size(max = 255, message = "Custom domain cannot exceed 255 characters")
  @Pattern(regexp = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+(([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?))$", message = "Custom domain must be a valid domain name")
  private String customDomain;

  /**
   * URL to the tenant's logo image.
   */
  @Column(name = "logo_url", length = 500)
  @Size(max = 500, message = "Logo URL cannot exceed 500 characters")
  private String logoUrl;

  /**
   * Primary brand color (hex code).
   */
  @Column(name = "primary_color", length = 7)
  @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Primary color must be a valid hex color code")
  private String primaryColor;

  /**
   * Secondary brand color (hex code).
   */
  @Column(name = "secondary_color", length = 7)
  @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Secondary color must be a valid hex color code")
  private String secondaryColor;

  /**
   * Contact email for the tenant.
   */
  @Column(name = "contact_email", length = 255)
  @Email(message = "Contact email must be in valid format")
  @Size(max = 255, message = "Contact email cannot exceed 255 characters")
  private String contactEmail;

  /**
   * Contact phone number for the tenant.
   */
  @Column(name = "contact_phone", length = 20)
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Contact phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Contact phone cannot exceed 20 characters")
  private String contactPhone;

  /**
   * Billing email for invoices and payment notifications.
   */
  @Column(name = "billing_email", length = 255)
  @Email(message = "Billing email must be in valid format")
  @Size(max = 255, message = "Billing email cannot exceed 255 characters")
  private String billingEmail;

  /**
   * Timestamp when the subscription expires.
   */
  @Column(name = "subscription_expires_at")
  private LocalDateTime subscriptionExpiresAt;

  /**
   * Timestamp when the trial period ends.
   */
  @Column(name = "trial_ends_at")
  private LocalDateTime trialEndsAt;

  /**
   * Flag indicating whether the tenant is in trial mode.
   */
  @Column(name = "is_trial", nullable = false)
  @NotNull(message = "Trial status cannot be null")
  private Boolean isTrial = true;

  /**
   * Checks if the tenant is currently active.
   * 
   * @return true if the tenant is active, false otherwise
   */
  public boolean isActive() {
    return status == TenantStatus.ACTIVE;
  }

  /**
   * Checks if the tenant is currently in trial mode.
   * 
   * @return true if the tenant is in trial, false otherwise
   */
  public boolean isInTrial() {
    return isTrial && (trialEndsAt == null || trialEndsAt.isAfter(LocalDateTime.now()));
  }

  /**
   * Checks if the tenant's subscription is expired.
   * 
   * @return true if the subscription is expired, false otherwise
   */
  public boolean isSubscriptionExpired() {
    return subscriptionExpiresAt != null && subscriptionExpiresAt.isBefore(LocalDateTime.now());
  }

  /**
   * Checks if the tenant's trial is expired.
   * 
   * @return true if the trial is expired, false otherwise
   */
  public boolean isTrialExpired() {
    return isTrial && trialEndsAt != null && trialEndsAt.isBefore(LocalDateTime.now());
  }

  /**
   * Updates the tenant to convert from trial to paid subscription.
   * 
   * @param newPlanType        the new subscription plan type
   * @param subscriptionExpiry the subscription expiry date
   */
  public void convertFromTrial(PlanType newPlanType, LocalDateTime subscriptionExpiry) {
    this.isTrial = false;
    this.planType = newPlanType;
    this.subscriptionExpiresAt = subscriptionExpiry;
    this.trialEndsAt = null;
  }

  /**
   * Updates the subscription plan and limits.
   * 
   * @param newPlanType  the new plan type
   * @param maxUsers     the maximum number of users
   * @param maxStorageGb the maximum storage in GB
   */
  public void updatePlan(PlanType newPlanType, Integer maxUsers, Integer maxStorageGb) {
    this.planType = newPlanType;
    this.maxUsers = maxUsers;
    this.maxStorageGb = maxStorageGb;
  }

  /**
   * Suspends the tenant account.
   */
  public void suspend() {
    this.status = TenantStatus.SUSPENDED;
  }

  /**
   * Activates the tenant account.
   */
  public void activate() {
    this.status = TenantStatus.ACTIVE;
  }

  /**
   * Deactivates the tenant account.
   */
  public void deactivate() {
    this.status = TenantStatus.INACTIVE;
  }
}
