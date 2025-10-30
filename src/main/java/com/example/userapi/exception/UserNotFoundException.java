package com.example.userapi.exception;

/**
 * Exception thrown when a requested user is not found in the system.
 * Maps to HTTP 404 Not Found response.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User with ID " + id + " not found");
    }
}
