package com.programmingmukesh.tenant.service.tenant_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.programmingmukesh.tenant.service.tenant_service.dto.request.CreateTenantRequest;
import com.programmingmukesh.tenant.service.tenant_service.dto.response.TenantResponse;
import com.programmingmukesh.tenant.service.tenant_service.entity.PlanType;
import com.programmingmukesh.tenant.service.tenant_service.entity.TenantStatus;

/**
 * Tenant Service interface defining all tenant-related operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public interface TenantService {

  /**
   * Creates a new tenant.
   * 
   * @param request the create tenant request
   * @return the created tenant response
   */
  TenantResponse createTenant(CreateTenantRequest request);

  /**
   * Retrieves a tenant by ID.
   * 
   * @param tenantId the tenant ID
   * @return the tenant response
   */
  TenantResponse getTenantById(UUID tenantId);

  /**
   * Retrieves a tenant by subdomain.
   * 
   * @param subdomain the subdomain
   * @return the tenant response
   */
  TenantResponse getTenantBySubdomain(String subdomain);

  /**
   * Retrieves a tenant by custom domain.
   * 
   * @param customDomain the custom domain
   * @return the tenant response
   */
  TenantResponse getTenantByCustomDomain(String customDomain);

  /**
   * Retrieves a tenant by subdomain or custom domain.
   * 
   * @param domain the domain (subdomain or custom domain)
   * @return the tenant response
   */
  TenantResponse getTenantByDomain(String domain);

  /**
   * Retrieves all tenants with pagination.
   * 
   * @param pageable the pagination parameters
   * @return the page of tenant responses
   */
  Page<TenantResponse> getAllTenants(Pageable pageable);

  /**
   * Retrieves tenants by status.
   * 
   * @param status   the tenant status
   * @param pageable the pagination parameters
   * @return the page of tenant responses
   */
  Page<TenantResponse> getTenantsByStatus(TenantStatus status, Pageable pageable);

  /**
   * Retrieves tenants by plan type.
   * 
   * @param planType the plan type
   * @param pageable the pagination parameters
   * @return the page of tenant responses
   */
  Page<TenantResponse> getTenantsByPlanType(PlanType planType, Pageable pageable);

  /**
   * Searches tenants by name.
   * 
   * @param name     the name to search for
   * @param pageable the pagination parameters
   * @return the page of tenant responses
   */
  Page<TenantResponse> searchTenantsByName(String name, Pageable pageable);

  /**
   * Updates a tenant.
   * 
   * @param tenantId the tenant ID
   * @param request  the update request
   * @return the updated tenant response
   */
  TenantResponse updateTenant(UUID tenantId, CreateTenantRequest request);

  /**
   * Updates tenant status.
   * 
   * @param tenantId the tenant ID
   * @param status   the new status
   * @return the updated tenant response
   */
  TenantResponse updateTenantStatus(UUID tenantId, TenantStatus status);

  /**
   * Updates tenant plan.
   * 
   * @param tenantId     the tenant ID
   * @param planType     the new plan type
   * @param maxUsers     the maximum users
   * @param maxStorageGb the maximum storage in GB
   * @return the updated tenant response
   */
  TenantResponse updateTenantPlan(UUID tenantId, PlanType planType, Integer maxUsers, Integer maxStorageGb);

  /**
   * Converts tenant from trial to paid subscription.
   * 
   * @param tenantId the tenant ID
   * @param planType the new plan type
   * @return the updated tenant response
   */
  TenantResponse convertFromTrial(UUID tenantId, PlanType planType);

  /**
   * Suspends a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the updated tenant response
   */
  TenantResponse suspendTenant(UUID tenantId);

  /**
   * Activates a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the updated tenant response
   */
  TenantResponse activateTenant(UUID tenantId);

  /**
   * Deletes a tenant (soft delete).
   * 
   * @param tenantId the tenant ID
   */
  void deleteTenant(UUID tenantId);

  /**
   * Checks if a subdomain is available.
   * 
   * @param subdomain the subdomain to check
   * @return true if available, false otherwise
   */
  boolean isSubdomainAvailable(String subdomain);

  /**
   * Checks if a custom domain is available.
   * 
   * @param customDomain the custom domain to check
   * @return true if available, false otherwise
   */
  boolean isCustomDomainAvailable(String customDomain);

  /**
   * Gets tenants with expired trials.
   * 
   * @return list of tenants with expired trials
   */
  List<TenantResponse> getTenantsWithExpiredTrials();

  /**
   * Gets tenants with expiring trials (within next 7 days).
   * 
   * @return list of tenants with expiring trials
   */
  List<TenantResponse> getTenantsWithExpiringTrials();

  /**
   * Gets tenants with expired subscriptions.
   * 
   * @return list of tenants with expired subscriptions
   */
  List<TenantResponse> getTenantsWithExpiredSubscriptions();

  /**
   * Gets tenants with expiring subscriptions (within next 30 days).
   * 
   * @return list of tenants with expiring subscriptions
   */
  List<TenantResponse> getTenantsWithExpiringSubscriptions();

  /**
   * Gets tenant statistics.
   * 
   * @return tenant statistics
   */
  TenantStatistics getTenantStatistics();

  /**
   * Tenant statistics inner class.
   */
  class TenantStatistics {
    private long totalTenants;
    private long activeTenants;
    private long trialTenants;
    private long suspendedTenants;
    private long basicPlanTenants;
    private long standardPlanTenants;
    private long premiumPlanTenants;
    private long enterprisePlanTenants;

    // Constructors, getters, and setters
    public TenantStatistics() {
    }

    public TenantStatistics(long totalTenants, long activeTenants, long trialTenants, long suspendedTenants,
        long basicPlanTenants, long standardPlanTenants, long premiumPlanTenants, long enterprisePlanTenants) {
      this.totalTenants = totalTenants;
      this.activeTenants = activeTenants;
      this.trialTenants = trialTenants;
      this.suspendedTenants = suspendedTenants;
      this.basicPlanTenants = basicPlanTenants;
      this.standardPlanTenants = standardPlanTenants;
      this.premiumPlanTenants = premiumPlanTenants;
      this.enterprisePlanTenants = enterprisePlanTenants;
    }

    // Getters and setters
    public long getTotalTenants() {
      return totalTenants;
    }

    public void setTotalTenants(long totalTenants) {
      this.totalTenants = totalTenants;
    }

    public long getActiveTenants() {
      return activeTenants;
    }

    public void setActiveTenants(long activeTenants) {
      this.activeTenants = activeTenants;
    }

    public long getTrialTenants() {
      return trialTenants;
    }

    public void setTrialTenants(long trialTenants) {
      this.trialTenants = trialTenants;
    }

    public long getSuspendedTenants() {
      return suspendedTenants;
    }

    public void setSuspendedTenants(long suspendedTenants) {
      this.suspendedTenants = suspendedTenants;
    }

    public long getBasicPlanTenants() {
      return basicPlanTenants;
    }

    public void setBasicPlanTenants(long basicPlanTenants) {
      this.basicPlanTenants = basicPlanTenants;
    }

    public long getStandardPlanTenants() {
      return standardPlanTenants;
    }

    public void setStandardPlanTenants(long standardPlanTenants) {
      this.standardPlanTenants = standardPlanTenants;
    }

    public long getPremiumPlanTenants() {
      return premiumPlanTenants;
    }

    public void setPremiumPlanTenants(long premiumPlanTenants) {
      this.premiumPlanTenants = premiumPlanTenants;
    }

    public long getEnterprisePlanTenants() {
      return enterprisePlanTenants;
    }

    public void setEnterprisePlanTenants(long enterprisePlanTenants) {
      this.enterprisePlanTenants = enterprisePlanTenants;
    }
  }
}
