package com.programmingmukesh.tenant.service.tenant_service.entity;

/**
 * Enumeration representing different tenant status values.
 * 
 * <p>
 * This enum defines the various states a tenant can be in throughout
 * its lifecycle in the system.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public enum TenantStatus {
  /**
   * Tenant is active and can use the system.
   */
  ACTIVE,

  /**
   * Tenant is inactive and cannot use the system.
   */
  INACTIVE,

  /**
   * Tenant account is suspended due to policy violations or payment issues.
   */
  SUSPENDED,

  /**
   * Tenant account is pending activation or approval.
   */
  PENDING,

  /**
   * Tenant account has been deleted (soft delete).
   */
  DELETED
}
