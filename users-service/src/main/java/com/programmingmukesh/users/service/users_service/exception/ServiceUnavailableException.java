package com.programmingmukesh.users.service.users_service.exception;

/**
 * Exception thrown when service is temporarily unavailable.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}