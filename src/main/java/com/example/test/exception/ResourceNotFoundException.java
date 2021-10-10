package com.example.test.exception;

/**
 * This exception represents absence of a resource that is needed for a client request
 */
public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
