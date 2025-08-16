package com.programmingmukesh.tenant.service.tenant_service.entity;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class providing common fields for all entities in the Tenant
 * microservice system.
 * 
 * <p>
 * This class provides the following common fields:
 * </p>
 * <ul>
 * <li><strong>id</strong>: Primary key using UUID for better distribution and
 * security</li>
 * <li><strong>tenantId</strong>: Multi-tenancy support for SaaS
 * architecture</li>
 * <li><strong>createdAt</strong>: Audit trail - when the record was
 * created</li>
 * <li><strong>updatedAt</strong>: Audit trail - when the record was last
 * modified</li>
 * </ul>
 * 
 * <p>
 * <strong>Best Practices Implemented:</strong>
 * </p>
 * <ul>
 * <li>UUID primary keys for better distribution and security</li>
 * <li>Multi-tenancy support with tenantId</li>
 * <li>Audit fields for tracking creation and modification times</li>
 * <li>Proper JPA annotations for database mapping</li>
 * <li>Validation annotations for data integrity</li>
 * <li>Lombok annotations for reducing boilerplate code</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {

  /**
   * Primary key using UUID for better distribution and security.
   * UUIDs are globally unique and don't require coordination between servers.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private UUID id;

  /**
   * Tenant identifier for multi-tenancy support.
   * For Tenant entity, this will be self-referencing (same as id).
   * For other entities, this references the tenant they belong to.
   */
  @Column(name = "tenant_id", length = 36)
  private UUID tenantId;

  /**
   * Timestamp when the record was created.
   * Automatically set by JPA lifecycle callbacks.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp when the record was last modified.
   * Automatically updated by JPA lifecycle callbacks.
   */
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * JPA lifecycle callback to set creation timestamp.
   * Called before the entity is persisted for the first time.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  /**
   * JPA lifecycle callback to update modification timestamp.
   * Called before the entity is updated.
   */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
