package com.programmingmukesh.auth.service.auth_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.programmingmukesh.auth.service.auth_service.entity.UserCredential;

/**
 * Repository interface for UserCredential entity operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

  /**
   * Find user credential by username.
   * 
   * @param username the username to search for
   * @return Optional containing the user credential if found
   */
  Optional<UserCredential> findByUsername(String username);

  /**
   * Find user credential by email.
   * 
   * @param email the email to search for
   * @return Optional containing the user credential if found
   */
  Optional<UserCredential> findByEmail(String email);

  /**
   * Check if username exists.
   * 
   * @param username the username to check
   * @return true if username exists, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Check if email exists.
   * 
   * @param email the email to check
   * @return true if email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Find user credential by user ID.
   * 
   * @param userId the user ID to search for
   * @return Optional containing the user credential if found
   */
  Optional<UserCredential> findByUserId(UUID userId);
}