package com.programmingmukesh.tenant.service.tenant_service.entity;

/**
 * Enumeration representing different subscription plan types for tenants.
 * 
 * <p>
 * This enum defines the various subscription tiers available to tenants,
 * each with different feature sets and limitations.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public enum PlanType {
  /**
   * Basic plan with limited features and users.
   */
  BASIC,

  /**
   * Standard plan with additional features and users.
   */
  STANDARD,

  /**
   * Premium plan with advanced features and higher limits.
   */
  PREMIUM,

  /**
   * Enterprise plan with full features and custom limits.
   */
  ENTERPRISE,

  /**
   * Trial plan for evaluation purposes.
   */
  TRIAL
}
