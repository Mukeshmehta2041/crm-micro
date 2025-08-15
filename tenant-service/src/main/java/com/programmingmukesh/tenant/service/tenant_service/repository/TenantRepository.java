package com.programmingmukesh.tenant.service.tenant_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.programmingmukesh.tenant.service.tenant_service.entity.Tenant;
import com.programmingmukesh.tenant.service.tenant_service.entity.TenantStatus;
import com.programmingmukesh.tenant.service.tenant_service.entity.PlanType;

/**
 * Repository interface for Tenant entity operations.
 * 
 * <p>
 * This repository provides data access methods for tenant management including:
 * </p>
 * <ul>
 * <li>Basic CRUD operations</li>
 * <li>Tenant lookup by subdomain and custom domain</li>
 * <li>Status and plan-based queries</li>
 * <li>Trial and subscription management queries</li>
 * <li>Statistical and reporting queries</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

  /**
   * Finds a tenant by subdomain.
   * 
   * @param subdomain the subdomain to search for
   * @return Optional containing the tenant if found
   */
  Optional<Tenant> findBySubdomain(String subdomain);

  /**
   * Finds a tenant by custom domain.
   * 
   * @param customDomain the custom domain to search for
   * @return Optional containing the tenant if found
   */
  Optional<Tenant> findByCustomDomain(String customDomain);

  /**
   * Finds a tenant by subdomain or custom domain.
   * 
   * @param subdomain    the subdomain to search for
   * @param customDomain the custom domain to search for
   * @return Optional containing the tenant if found
   */
  @Query("SELECT t FROM Tenant t WHERE t.subdomain = :subdomain OR t.customDomain = :customDomain")
  Optional<Tenant> findBySubdomainOrCustomDomain(@Param("subdomain") String subdomain,
      @Param("customDomain") String customDomain);

  /**
   * Finds all tenants by status.
   * 
   * @param status the tenant status
   * @return List of tenants with the specified status
   */
  List<Tenant> findByStatus(TenantStatus status);

  /**
   * Finds all tenants by status with pagination.
   * 
   * @param status   the tenant status
   * @param pageable pagination parameters
   * @return Page of tenants with the specified status
   */
  Page<Tenant> findByStatus(TenantStatus status, Pageable pageable);

  /**
   * Finds all tenants by plan type.
   * 
   * @param planType the plan type
   * @return List of tenants with the specified plan type
   */
  List<Tenant> findByPlanType(PlanType planType);

  /**
   * Finds all tenants by plan type with pagination.
   * 
   * @param planType the plan type
   * @param pageable pagination parameters
   * @return Page of tenants with the specified plan type
   */
  Page<Tenant> findByPlanType(PlanType planType, Pageable pageable);

  /**
   * Finds all tenants currently in trial.
   * 
   * @return List of tenants in trial mode
   */
  List<Tenant> findByIsTrialTrue();

  /**
   * Finds all tenants with expired trials.
   * 
   * @param currentTime the current timestamp
   * @return List of tenants with expired trials
   */
  @Query("SELECT t FROM Tenant t WHERE t.isTrial = true AND t.trialEndsAt < :currentTime")
  List<Tenant> findTenantsWithExpiredTrials(@Param("currentTime") LocalDateTime currentTime);

  /**
   * Finds all tenants with expiring trials within the specified period.
   * 
   * @param currentTime     the current timestamp
   * @param expiryThreshold the expiry threshold timestamp
   * @return List of tenants with expiring trials
   */
  @Query("SELECT t FROM Tenant t WHERE t.isTrial = true AND t.trialEndsAt BETWEEN :currentTime AND :expiryThreshold")
  List<Tenant> findTenantsWithExpiringTrials(@Param("currentTime") LocalDateTime currentTime,
      @Param("expiryThreshold") LocalDateTime expiryThreshold);

  /**
   * Finds all tenants with expired subscriptions.
   * 
   * @param currentTime the current timestamp
   * @return List of tenants with expired subscriptions
   */
  @Query("SELECT t FROM Tenant t WHERE t.isTrial = false AND t.subscriptionExpiresAt < :currentTime")
  List<Tenant> findTenantsWithExpiredSubscriptions(@Param("currentTime") LocalDateTime currentTime);

  /**
   * Finds all tenants with expiring subscriptions within the specified period.
   * 
   * @param currentTime     the current timestamp
   * @param expiryThreshold the expiry threshold timestamp
   * @return List of tenants with expiring subscriptions
   */
  @Query("SELECT t FROM Tenant t WHERE t.isTrial = false AND t.subscriptionExpiresAt BETWEEN :currentTime AND :expiryThreshold")
  List<Tenant> findTenantsWithExpiringSubscriptions(@Param("currentTime") LocalDateTime currentTime,
      @Param("expiryThreshold") LocalDateTime expiryThreshold);

  /**
   * Finds tenants by contact email.
   * 
   * @param contactEmail the contact email
   * @return List of tenants with the specified contact email
   */
  List<Tenant> findByContactEmail(String contactEmail);

  /**
   * Finds tenants by billing email.
   * 
   * @param billingEmail the billing email
   * @return List of tenants with the specified billing email
   */
  List<Tenant> findByBillingEmail(String billingEmail);

  /**
   * Searches tenants by name containing the specified text (case-insensitive).
   * 
   * @param name     the name text to search for
   * @param pageable pagination parameters
   * @return Page of tenants matching the search criteria
   */
  @Query("SELECT t FROM Tenant t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
  Page<Tenant> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

  /**
   * Counts the total number of active tenants.
   * 
   * @return the number of active tenants
   */
  @Query("SELECT COUNT(t) FROM Tenant t WHERE t.status = 'ACTIVE'")
  long countActiveTenants();

  /**
   * Counts the total number of tenants in trial.
   * 
   * @return the number of tenants in trial
   */
  @Query("SELECT COUNT(t) FROM Tenant t WHERE t.isTrial = true")
  long countTrialTenants();

  /**
   * Counts tenants by plan type.
   * 
   * @param planType the plan type
   * @return the number of tenants with the specified plan type
   */
  long countByPlanType(PlanType planType);

  /**
   * Counts tenants by status.
   * 
   * @param status the tenant status
   * @return the number of tenants with the specified status
   */
  long countByStatus(TenantStatus status);

  /**
   * Checks if a subdomain is available (not taken).
   * 
   * @param subdomain the subdomain to check
   * @return true if the subdomain is available, false otherwise
   */
  @Query("SELECT CASE WHEN COUNT(t) = 0 THEN true ELSE false END FROM Tenant t WHERE t.subdomain = :subdomain")
  boolean isSubdomainAvailable(@Param("subdomain") String subdomain);

  /**
   * Checks if a custom domain is available (not taken).
   * 
   * @param customDomain the custom domain to check
   * @return true if the custom domain is available, false otherwise
   */
  @Query("SELECT CASE WHEN COUNT(t) = 0 THEN true ELSE false END FROM Tenant t WHERE t.customDomain = :customDomain")
  boolean isCustomDomainAvailable(@Param("customDomain") String customDomain);
}
