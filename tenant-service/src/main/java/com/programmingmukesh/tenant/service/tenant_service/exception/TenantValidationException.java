package com.programmingmukesh.tenant.service.tenant_service.exception;

/**
 * Exception thrown when tenant validation fails.
 * 
 * <p>
 * This exception is thrown when tenant data validation fails,
 * such as invalid input parameters or business rule violations.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class TenantValidationException extends RuntimeException {

  /**
   * Constructs a new TenantValidationException with the specified detail message.
   * 
   * @param message the detail message
   */
  public TenantValidationException(String message) {
    super(message);
  }

  /**
   * Constructs a new TenantValidationException with the specified detail message
   * and cause.
   * 
   * @param message the detail message
   * @param cause   the cause
   */
  public TenantValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
