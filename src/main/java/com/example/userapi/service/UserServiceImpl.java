package com.example.userapi.service;

import com.example.userapi.dto.*;
import com.example.userapi.entity.User;
import com.example.userapi.exception.DuplicateEmailException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface.
 * Provides business logic for user management operations.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Create and save user entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        // Check if user exists
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Check email uniqueness (skip if email unchanged)
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
        }

        // Update user fields
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        // Check if user exists
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }

    /**
     * Map User entity to UserResponse DTO.
     */
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt().toString());
        response.setUpdatedAt(user.getUpdatedAt().toString());
        return response;
    }
}
