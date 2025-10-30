package com.example.userapi.repository;

import com.example.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity database operations.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email address.
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email exists.
     * @param email the email to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
