package com.programmingmukesh.users.service.users_service.exception;

/**
 * Exception thrown when cache operations fail.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class CacheException extends RuntimeException {

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}