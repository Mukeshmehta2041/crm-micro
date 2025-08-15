package com.programmingmukesh.tenant.service.tenant_service.entity;

/**
 * Enumeration representing different configuration value types.
 * 
 * <p>
 * This enum defines the data types for configuration values,
 * enabling proper validation and type conversion.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public enum ConfigType {
  /**
   * String configuration value.
   */
  STRING,

  /**
   * Integer configuration value.
   */
  INTEGER,

  /**
   * Boolean configuration value.
   */
  BOOLEAN,

  /**
   * Decimal/Float configuration value.
   */
  DECIMAL,

  /**
   * JSON configuration value.
   */
  JSON,

  /**
   * URL configuration value.
   */
  URL,

  /**
   * Email configuration value.
   */
  EMAIL,

  /**
   * Date configuration value.
   */
  DATE,

  /**
   * DateTime configuration value.
   */
  DATETIME
}
