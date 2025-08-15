package com.programmingmukesh.tenant.service.tenant_service.entity;

import java.util.UUID;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * TenantConfiguration entity representing configuration settings for tenants.
 * 
 * <p>
 * This entity stores tenant-specific configuration settings including:
 * </p>
 * <ul>
 * <li>Configuration key-value pairs</li>
 * <li>Configuration metadata (type, category, description)</li>
 * <li>Security settings (encryption, system flags)</li>
 * <li>Editability and access control</li>
 * </ul>
 * 
 * <p>
 * <strong>Database Indexes:</strong>
 * </p>
 * <ul>
 * <li>Primary key on id (UUID)</li>
 * <li>Index on tenantId for tenant-specific queries</li>
 * <li>Index on configKey for fast key lookups</li>
 * <li>Index on category for grouped configuration queries</li>
 * <li>Composite index on tenantId and configKey for unique constraints</li>
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
@Table(name = "tenant_configurations", indexes = {
    @Index(name = "idx_tenant_configs_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_tenant_configs_config_key", columnList = "config_key"),
    @Index(name = "idx_tenant_configs_category", columnList = "category"),
    @Index(name = "idx_tenant_configs_is_system", columnList = "is_system"),
    @Index(name = "idx_tenant_configs_tenant_key", columnList = "tenant_id,config_key", unique = true)
})
@Entity
public class TenantConfiguration extends BaseEntity {

  /**
   * Reference to the tenant this configuration belongs to.
   */
  @Column(name = "tenant_id", nullable = false, length = 36)
  @NotNull(message = "Tenant ID cannot be null")
  private UUID tenantId;

  /**
   * Configuration key identifier.
   */
  @Column(name = "config_key", nullable = false, length = 255)
  @NotBlank(message = "Configuration key cannot be blank")
  @Size(min = 1, max = 255, message = "Configuration key must be between 1 and 255 characters")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Configuration key can only contain letters, numbers, dots, underscores, and hyphens")
  private String configKey;

  /**
   * Configuration value (stored as text).
   */
  @Column(name = "config_value", columnDefinition = "TEXT")
  private String configValue;

  /**
   * Data type of the configuration value.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "config_type", nullable = false, length = 50)
  @NotNull(message = "Configuration type cannot be null")
  private ConfigType configType = ConfigType.STRING;

  /**
   * Category or group for organizing configurations.
   */
  @Column(name = "category", length = 100)
  @Size(max = 100, message = "Category cannot exceed 100 characters")
  private String category;

  /**
   * Human-readable description of the configuration.
   */
  @Column(name = "description", length = 500)
  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  /**
   * Flag indicating whether the value is encrypted.
   */
  @Column(name = "is_encrypted", nullable = false)
  @NotNull(message = "Encrypted flag cannot be null")
  private Boolean isEncrypted = false;

  /**
   * Flag indicating whether this is a system configuration.
   */
  @Column(name = "is_system", nullable = false)
  @NotNull(message = "System flag cannot be null")
  private Boolean isSystem = false;

  /**
   * Flag indicating whether the configuration can be edited by users.
   */
  @Column(name = "is_editable", nullable = false)
  @NotNull(message = "Editable flag cannot be null")
  private Boolean isEditable = true;

  /**
   * Checks if the configuration is a system configuration.
   * 
   * @return true if this is a system configuration, false otherwise
   */
  public boolean isSystemConfig() {
    return isSystem;
  }

  /**
   * Checks if the configuration value is encrypted.
   * 
   * @return true if the value is encrypted, false otherwise
   */
  public boolean isValueEncrypted() {
    return isEncrypted;
  }

  /**
   * Checks if the configuration can be edited by users.
   * 
   * @return true if the configuration is editable, false otherwise
   */
  public boolean canBeEdited() {
    return isEditable && !isSystem;
  }

  /**
   * Gets the configuration value as a String.
   * 
   * @return the configuration value as String
   */
  public String getStringValue() {
    return configValue;
  }

  /**
   * Gets the configuration value as an Integer.
   * 
   * @return the configuration value as Integer, null if not a valid integer
   */
  public Integer getIntegerValue() {
    if (configValue == null || configType != ConfigType.INTEGER) {
      return null;
    }
    try {
      return Integer.valueOf(configValue);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Gets the configuration value as a Boolean.
   * 
   * @return the configuration value as Boolean, null if not a valid boolean
   */
  public Boolean getBooleanValue() {
    if (configValue == null || configType != ConfigType.BOOLEAN) {
      return null;
    }
    return Boolean.valueOf(configValue);
  }

  /**
   * Gets the configuration value as a Double.
   * 
   * @return the configuration value as Double, null if not a valid decimal
   */
  public Double getDecimalValue() {
    if (configValue == null || configType != ConfigType.DECIMAL) {
      return null;
    }
    try {
      return Double.valueOf(configValue);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Sets the configuration value from any object.
   * 
   * @param value the value to set
   */
  public void setValue(Object value) {
    if (value == null) {
      this.configValue = null;
    } else {
      this.configValue = value.toString();
    }
  }
}
