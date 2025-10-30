package com.example.userapi.dto;

import lombok.Data;

/**
 * Data Transfer Object for user responses.
 * Returns user data to API clients in a consistent format.
 */
@Data
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;
}
