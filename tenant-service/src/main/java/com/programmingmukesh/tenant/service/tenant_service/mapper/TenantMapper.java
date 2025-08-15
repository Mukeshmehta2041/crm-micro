package com.programmingmukesh.tenant.service.tenant_service.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.programmingmukesh.tenant.service.tenant_service.dto.request.CreateTenantRequest;
import com.programmingmukesh.tenant.service.tenant_service.dto.response.TenantResponse;
import com.programmingmukesh.tenant.service.tenant_service.entity.Tenant;
import com.programmingmukesh.tenant.service.tenant_service.entity.TenantStatus;

/**
 * Mapper class for converting between Tenant entities and DTOs.
 * 
 * <p>
 * This mapper provides methods for converting between:
 * </p>
 * <ul>
 * <li>CreateTenantRequest to Tenant entity</li>
 * <li>Tenant entity to TenantResponse DTO</li>
 * <li>Updating existing Tenant entity from request</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Component
public class TenantMapper {

  /**
   * Converts CreateTenantRequest to Tenant entity.
   * 
   * @param request the create tenant request
   * @return the tenant entity
   */
  public Tenant toEntity(CreateTenantRequest request) {
    if (request == null) {
      return null;
    }

    return Tenant.builder()
        .name(request.getName())
        .subdomain(request.getSubdomain() != null ? request.getSubdomain().toLowerCase() : null)
        .planType(request.getPlanType())
        .status(TenantStatus.ACTIVE)
        .maxUsers(request.getMaxUsers())
        .maxStorageGb(request.getMaxStorageGb())
        .customDomain(request.getCustomDomain())
        .logoUrl(request.getLogoUrl())
        .primaryColor(request.getPrimaryColor())
        .secondaryColor(request.getSecondaryColor())
        .contactEmail(request.getContactEmail())
        .contactPhone(request.getContactPhone())
        .billingEmail(request.getBillingEmail())
        .trialEndsAt(request.getTrialEndsAt())
        .isTrial(request.getIsTrial())
        .build();
  }

  /**
   * Converts Tenant entity to TenantResponse DTO.
   * 
   * @param tenant the tenant entity
   * @return the tenant response DTO
   */
  public TenantResponse toResponse(Tenant tenant) {
    if (tenant == null) {
      return null;
    }

    return TenantResponse.builder()
        .id(tenant.getId())
        .name(tenant.getName())
        .subdomain(tenant.getSubdomain())
        .planType(tenant.getPlanType())
        .status(tenant.getStatus())
        .maxUsers(tenant.getMaxUsers())
        .maxStorageGb(tenant.getMaxStorageGb())
        .customDomain(tenant.getCustomDomain())
        .logoUrl(tenant.getLogoUrl())
        .primaryColor(tenant.getPrimaryColor())
        .secondaryColor(tenant.getSecondaryColor())
        .contactEmail(tenant.getContactEmail())
        .contactPhone(tenant.getContactPhone())
        .billingEmail(tenant.getBillingEmail())
        .subscriptionExpiresAt(tenant.getSubscriptionExpiresAt())
        .trialEndsAt(tenant.getTrialEndsAt())
        .isTrial(tenant.getIsTrial())
        .createdAt(tenant.getCreatedAt())
        .updatedAt(tenant.getUpdatedAt())
        .isActive(tenant.isActive())
        .isInTrial(tenant.isInTrial())
        .isSubscriptionExpired(tenant.isSubscriptionExpired())
        .isTrialExpired(tenant.isTrialExpired())
        .build();
  }

  /**
   * Updates an existing Tenant entity from CreateTenantRequest.
   * 
   * @param tenant  the existing tenant entity
   * @param request the update request
   * @return the updated tenant entity
   */
  public Tenant updateEntity(Tenant tenant, CreateTenantRequest request) {
    if (tenant == null || request == null) {
      return tenant;
    }

    // Update basic information
    if (request.getName() != null) {
      tenant.setName(request.getName());
    }

    if (request.getSubdomain() != null) {
      tenant.setSubdomain(request.getSubdomain().toLowerCase());
    }

    if (request.getPlanType() != null) {
      tenant.setPlanType(request.getPlanType());
    }

    if (request.getMaxUsers() != null) {
      tenant.setMaxUsers(request.getMaxUsers());
    }

    if (request.getMaxStorageGb() != null) {
      tenant.setMaxStorageGb(request.getMaxStorageGb());
    }

    // Update optional fields
    tenant.setCustomDomain(request.getCustomDomain());
    tenant.setLogoUrl(request.getLogoUrl());
    tenant.setPrimaryColor(request.getPrimaryColor());
    tenant.setSecondaryColor(request.getSecondaryColor());
    tenant.setContactEmail(request.getContactEmail());
    tenant.setContactPhone(request.getContactPhone());
    tenant.setBillingEmail(request.getBillingEmail());

    // Update trial settings if provided
    if (request.getIsTrial() != null) {
      tenant.setIsTrial(request.getIsTrial());
    }

    if (request.getTrialEndsAt() != null) {
      tenant.setTrialEndsAt(request.getTrialEndsAt());
    }

    return tenant;
  }

  /**
   * Creates a new tenant entity with trial defaults.
   * 
   * @param request the create tenant request
   * @return the tenant entity with trial defaults
   */
  public Tenant toTrialEntity(CreateTenantRequest request) {
    Tenant tenant = toEntity(request);
    if (tenant != null) {
      // Set trial defaults
      tenant.setIsTrial(true);
      tenant.setTrialEndsAt(LocalDateTime.now().plusDays(14)); // 14-day trial
      tenant.setMaxUsers(Math.min(tenant.getMaxUsers() != null ? tenant.getMaxUsers() : 5, 5));
      tenant.setMaxStorageGb(Math.min(tenant.getMaxStorageGb() != null ? tenant.getMaxStorageGb() : 10, 10));
    }
    return tenant;
  }

  /**
   * Creates a tenant response with minimal information (for public APIs).
   * 
   * @param tenant the tenant entity
   * @return the minimal tenant response
   */
  public TenantResponse toMinimalResponse(Tenant tenant) {
    if (tenant == null) {
      return null;
    }

    return TenantResponse.builder()
        .id(tenant.getId())
        .name(tenant.getName())
        .subdomain(tenant.getSubdomain())
        .planType(tenant.getPlanType())
        .status(tenant.getStatus())
        .customDomain(tenant.getCustomDomain())
        .logoUrl(tenant.getLogoUrl())
        .primaryColor(tenant.getPrimaryColor())
        .secondaryColor(tenant.getSecondaryColor())
        .isActive(tenant.isActive())
        .isInTrial(tenant.isInTrial())
        .build();
  }
}
