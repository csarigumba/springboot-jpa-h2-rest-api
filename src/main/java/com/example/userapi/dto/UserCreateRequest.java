package com.example.userapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object for user creation requests.
 * Validates user input for creating new user accounts.
 */
@Data
public class UserCreateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;
}
