package com.programmingmukesh.tenant.service.tenant_service.exception;

/**
 * Exception thrown when attempting to create a tenant that already exists.
 * 
 * <p>
 * This exception is thrown when attempting to create a tenant with a
 * subdomain or custom domain that is already taken.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class TenantAlreadyExistsException extends RuntimeException {

  /**
   * Constructs a new TenantAlreadyExistsException with the specified detail
   * message.
   * 
   * @param message the detail message
   */
  public TenantAlreadyExistsException(String message) {
    super(message);
  }

  /**
   * Constructs a new TenantAlreadyExistsException with the specified detail
   * message and cause.
   * 
   * @param message the detail message
   * @param cause   the cause
   */
  public TenantAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
