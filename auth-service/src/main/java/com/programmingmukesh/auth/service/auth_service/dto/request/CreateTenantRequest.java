package com.programmingmukesh.auth.service.auth_service.dto.request;

import java.time.LocalDateTime;

import lombok.*;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Create Tenant Request DTO for tenant registration during user signup.
 * 
 * <p>
 * This DTO represents the data required to create a new tenant organization
 * during the user registration process.
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
  @NotBlank(message = "Company name cannot be blank")
  @Size(min = 1, max = 255, message = "Company name must be between 1 and 255 characters")
  private String name;

  /**
   * Unique subdomain for the tenant (generated from company name).
   */
  @NotBlank(message = "Subdomain cannot be blank")
  @Size(min = 3, max = 100, message = "Subdomain must be between 3 and 100 characters")
  @Pattern(regexp = "^[a-z0-9]([a-z0-9-]*[a-z0-9])?$", message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
  private String subdomain;

  /**
   * Contact email for the tenant (same as user email).
   */
  @Email(message = "Contact email must be in valid format")
  @Size(max = 255, message = "Contact email cannot exceed 255 characters")
  private String contactEmail;

  /**
   * Billing email for invoices and payment notifications.
   */
  @Email(message = "Billing email must be in valid format")
  @Size(max = 255, message = "Billing email cannot exceed 255 characters")
  private String billingEmail;

  /**
   * Timestamp when the trial period should end.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime trialEndsAt;

  /**
   * Flag indicating whether the tenant should start in trial mode.
   */
  @Builder.Default
  private Boolean isTrial = true;

  /**
   * Maximum number of users for trial.
   */
  @Builder.Default
  private Integer maxUsers = 5;

  /**
   * Maximum storage in GB for trial.
   */
  @Builder.Default
  private Integer maxStorageGb = 10;

  /**
   * Creates a tenant request from company name and email.
   * 
   * @param companyName the company name
   * @param email       the contact email
   * @return the create tenant request
   */
  public static CreateTenantRequest fromCompanyAndEmail(String companyName, String email) {
    String subdomain = generateSubdomainFromCompanyName(companyName);

    return CreateTenantRequest.builder()
        .name(companyName)
        .subdomain(subdomain)
        .contactEmail(email)
        .billingEmail(email)
        .isTrial(true)
        .maxUsers(5)
        .maxStorageGb(10)
        .trialEndsAt(LocalDateTime.now().plusDays(14))
        .build();
  }

  /**
   * Generates a subdomain from company name.
   * 
   * @param companyName the company name
   * @return the generated subdomain
   */
  private static String generateSubdomainFromCompanyName(String companyName) {
    if (companyName == null || companyName.trim().isEmpty()) {
      return null;
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
}
