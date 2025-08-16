package com.programmingmukesh.auth.service.auth_service.exception;

/**
 * Exception thrown when attempting to create a tenant that already exists.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class TenantAlreadyExistsException extends RuntimeException {

  public TenantAlreadyExistsException(String message) {
    super(message);
  }

  public TenantAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}