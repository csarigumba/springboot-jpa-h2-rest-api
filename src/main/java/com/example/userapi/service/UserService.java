package com.example.userapi.service;

import com.example.userapi.dto.*;

import java.util.List;

/**
 * Service interface defining business operations for user management.
 * Provides CRUD operations with business logic and validation.
 */
public interface UserService {

    /**
     * Create a new user account.
     * @param request user creation data
     * @return created user details
     * @throws DuplicateEmailException if email already exists
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * Retrieve a user by ID.
     * @param id user identifier
     * @return user details
     * @throws UserNotFoundException if user not found
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieve all users in the system.
     * @return list of all users
     */
    List<UserResponse> getAllUsers();

    /**
     * Update an existing user's information.
     * @param id user identifier
     * @param request updated user data
     * @return updated user details
     * @throws UserNotFoundException if user not found
     * @throws DuplicateEmailException if new email already exists
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Delete a user from the system.
     * @param id user identifier
     * @throws UserNotFoundException if user not found
     */
    void deleteUser(Long id);
}
