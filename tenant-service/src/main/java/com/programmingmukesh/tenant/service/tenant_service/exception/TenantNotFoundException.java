package com.programmingmukesh.tenant.service.tenant_service.exception;

/**
 * Exception thrown when a tenant is not found.
 * 
 * <p>
 * This exception is thrown when attempting to access a tenant
 * that does not exist in the system.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class TenantNotFoundException extends RuntimeException {

  /**
   * Constructs a new TenantNotFoundException with the specified detail message.
   * 
   * @param message the detail message
   */
  public TenantNotFoundException(String message) {
    super(message);
  }

  /**
   * Constructs a new TenantNotFoundException with the specified detail message
   * and cause.
   * 
   * @param message the detail message
   * @param cause   the cause
   */
  public TenantNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
