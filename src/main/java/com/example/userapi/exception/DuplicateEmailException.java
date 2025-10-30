package com.example.userapi.exception;

/**
 * Exception thrown when attempting to create or update a user with an email
 * that already exists in the system.
 * Maps to HTTP 409 Conflict response.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("User with email '" + email + "' already exists");
    }
}
