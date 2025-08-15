package com.programmingmukesh.tenant.service.tenant_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.programmingmukesh.tenant.service.tenant_service.entity.TenantConfiguration;
import com.programmingmukesh.tenant.service.tenant_service.entity.ConfigType;

/**
 * Repository interface for TenantConfiguration entity operations.
 * 
 * <p>
 * This repository provides data access methods for tenant configuration
 * management including:
 * </p>
 * <ul>
 * <li>Basic CRUD operations</li>
 * <li>Tenant-specific configuration queries</li>
 * <li>Configuration key-based lookups</li>
 * <li>Category and type-based filtering</li>
 * <li>System configuration management</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Repository
public interface TenantConfigurationRepository extends JpaRepository<TenantConfiguration, UUID> {

  /**
   * Finds all configurations for a specific tenant.
   * 
   * @param tenantId the tenant ID
   * @return List of configurations for the tenant
   */
  List<TenantConfiguration> findByTenantId(UUID tenantId);

  /**
   * Finds all configurations for a specific tenant with pagination.
   * 
   * @param tenantId the tenant ID
   * @param pageable pagination parameters
   * @return Page of configurations for the tenant
   */
  Page<TenantConfiguration> findByTenantId(UUID tenantId, Pageable pageable);

  /**
   * Finds a specific configuration by tenant ID and config key.
   * 
   * @param tenantId  the tenant ID
   * @param configKey the configuration key
   * @return Optional containing the configuration if found
   */
  Optional<TenantConfiguration> findByTenantIdAndConfigKey(UUID tenantId, String configKey);

  /**
   * Finds all configurations for a tenant by category.
   * 
   * @param tenantId the tenant ID
   * @param category the configuration category
   * @return List of configurations in the specified category
   */
  List<TenantConfiguration> findByTenantIdAndCategory(UUID tenantId, String category);

  /**
   * Finds all configurations for a tenant by category with pagination.
   * 
   * @param tenantId the tenant ID
   * @param category the configuration category
   * @param pageable pagination parameters
   * @return Page of configurations in the specified category
   */
  Page<TenantConfiguration> findByTenantIdAndCategory(UUID tenantId, String category, Pageable pageable);

  /**
   * Finds all configurations for a tenant by config type.
   * 
   * @param tenantId   the tenant ID
   * @param configType the configuration type
   * @return List of configurations with the specified type
   */
  List<TenantConfiguration> findByTenantIdAndConfigType(UUID tenantId, ConfigType configType);

  /**
   * Finds all system configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return List of system configurations
   */
  List<TenantConfiguration> findByTenantIdAndIsSystemTrue(UUID tenantId);

  /**
   * Finds all non-system (user) configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return List of user configurations
   */
  List<TenantConfiguration> findByTenantIdAndIsSystemFalse(UUID tenantId);

  /**
   * Finds all editable configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return List of editable configurations
   */
  List<TenantConfiguration> findByTenantIdAndIsEditableTrue(UUID tenantId);

  /**
   * Finds all encrypted configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return List of encrypted configurations
   */
  List<TenantConfiguration> findByTenantIdAndIsEncryptedTrue(UUID tenantId);

  /**
   * Searches configurations by key pattern for a tenant.
   * 
   * @param tenantId   the tenant ID
   * @param keyPattern the key pattern to search for
   * @return List of configurations matching the key pattern
   */
  @Query("SELECT tc FROM TenantConfiguration tc WHERE tc.tenantId = :tenantId AND tc.configKey LIKE :keyPattern")
  List<TenantConfiguration> findByTenantIdAndConfigKeyLike(@Param("tenantId") UUID tenantId,
      @Param("keyPattern") String keyPattern);

  /**
   * Searches configurations by value pattern for a tenant.
   * 
   * @param tenantId     the tenant ID
   * @param valuePattern the value pattern to search for
   * @return List of configurations matching the value pattern
   */
  @Query("SELECT tc FROM TenantConfiguration tc WHERE tc.tenantId = :tenantId AND tc.configValue LIKE :valuePattern")
  List<TenantConfiguration> findByTenantIdAndConfigValueLike(@Param("tenantId") UUID tenantId,
      @Param("valuePattern") String valuePattern);

  /**
   * Finds configurations by multiple categories for a tenant.
   * 
   * @param tenantId   the tenant ID
   * @param categories the list of categories
   * @return List of configurations in the specified categories
   */
  @Query("SELECT tc FROM TenantConfiguration tc WHERE tc.tenantId = :tenantId AND tc.category IN :categories")
  List<TenantConfiguration> findByTenantIdAndCategoryIn(@Param("tenantId") UUID tenantId,
      @Param("categories") List<String> categories);

  /**
   * Finds configurations by multiple config keys for a tenant.
   * 
   * @param tenantId   the tenant ID
   * @param configKeys the list of configuration keys
   * @return List of configurations with the specified keys
   */
  @Query("SELECT tc FROM TenantConfiguration tc WHERE tc.tenantId = :tenantId AND tc.configKey IN :configKeys")
  List<TenantConfiguration> findByTenantIdAndConfigKeyIn(@Param("tenantId") UUID tenantId,
      @Param("configKeys") List<String> configKeys);

  /**
   * Counts configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the number of configurations for the tenant
   */
  long countByTenantId(UUID tenantId);

  /**
   * Counts configurations by category for a tenant.
   * 
   * @param tenantId the tenant ID
   * @param category the configuration category
   * @return the number of configurations in the specified category
   */
  long countByTenantIdAndCategory(UUID tenantId, String category);

  /**
   * Counts system configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the number of system configurations
   */
  long countByTenantIdAndIsSystemTrue(UUID tenantId);

  /**
   * Counts user configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the number of user configurations
   */
  long countByTenantIdAndIsSystemFalse(UUID tenantId);

  /**
   * Checks if a configuration key exists for a tenant.
   * 
   * @param tenantId  the tenant ID
   * @param configKey the configuration key
   * @return true if the configuration exists, false otherwise
   */
  @Query("SELECT CASE WHEN COUNT(tc) > 0 THEN true ELSE false END FROM TenantConfiguration tc WHERE tc.tenantId = :tenantId AND tc.configKey = :configKey")
  boolean existsByTenantIdAndConfigKey(@Param("tenantId") UUID tenantId, @Param("configKey") String configKey);

  /**
   * Deletes all configurations for a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the number of deleted configurations
   */
  long deleteByTenantId(UUID tenantId);

  /**
   * Deletes configurations by category for a tenant.
   * 
   * @param tenantId the tenant ID
   * @param category the configuration category
   * @return the number of deleted configurations
   */
  long deleteByTenantIdAndCategory(UUID tenantId, String category);

  /**
   * Deletes a specific configuration by tenant ID and config key.
   * 
   * @param tenantId  the tenant ID
   * @param configKey the configuration key
   * @return the number of deleted configurations
   */
  long deleteByTenantIdAndConfigKey(UUID tenantId, String configKey);
}
