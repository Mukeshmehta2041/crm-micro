package com.programmingmukesh.users.service.users_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.programmingmukesh.users.service.users_service.entity.User;
import com.programmingmukesh.users.service.users_service.entity.UserStatus;

/**
 * User Repository interface for database operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<User> {

  /**
   * Finds a user by username.
   * 
   * @param username the username
   * @return the user if found
   */
  Optional<User> findByUsername(String username);

  /**
   * Finds a user by email.
   * 
   * @param email the email
   * @return the user if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Checks if a user exists by username.
   * 
   * @param username the username
   * @return true if exists, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Checks if a user exists by email.
   * 
   * @param email the email
   * @return true if exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Checks if a user exists by username or email.
   * 
   * @param username the username
   * @param email    the email
   * @return true if exists, false otherwise
   */
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username OR u.email = :email")
  boolean existsByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

  /**
   * Finds users by status, excluding deleted users.
   * 
   * @param status   the status to exclude
   * @param pageable the pagination parameters
   * @return the page of users
   */
  Page<User> findByStatusNotAndDeletedAtIsNull(UserStatus status, Pageable pageable);

  /**
   * Finds users by department, excluding deleted users.
   * 
   * @param department the department
   * @param status     the status to exclude
   * @return the list of users
   */
  List<User> findByDepartmentAndStatusNotAndDeletedAtIsNull(String department, UserStatus status);

  /**
   * Finds users by company, excluding deleted users.
   * 
   * @param company the company
   * @param status  the status to exclude
   * @return the list of users
   */
  List<User> findByCompanyAndStatusNotAndDeletedAtIsNull(String company, UserStatus status);

  /**
   * Counts users by status, excluding deleted users.
   * 
   * @param status the status to exclude
   * @return the count
   */
  long countByStatusNotAndDeletedAtIsNull(UserStatus status);

  /**
   * Finds users by tenant ID.
   * 
   * @param tenantId the tenant ID
   * @return the list of users
   */
  List<User> findByTenantId(UUID tenantId);

  /**
   * Finds users by manager ID.
   * 
   * @param managerId the manager ID
   * @return the list of users
   */
  List<User> findByManagerId(UUID managerId);

  /**
   * Finds users by team ID.
   * 
   * @param teamId the team ID
   * @return the list of users
   */
  List<User> findByTeamId(UUID teamId);

  /**
   * Finds active users by department.
   * 
   * @param department the department
   * @return the list of active users
   */
  @Query("SELECT u FROM User u WHERE u.department = :department AND u.status = 'ACTIVE' AND u.deletedAt IS NULL")
  List<User> findActiveUsersByDepartment(@Param("department") String department);

  /**
   * Finds users by email verification status.
   * 
   * @param emailVerified the email verification status
   * @return the list of users
   */
  List<User> findByEmailVerified(Boolean emailVerified);

  /**
   * Finds users by onboarding completion status.
   * 
   * @param onboardingCompleted the onboarding completion status
   * @return the list of users
   */
  List<User> findByOnboardingCompleted(Boolean onboardingCompleted);

  /**
   * Finds users created within a date range.
   * 
   * @param startDate the start date
   * @param endDate   the end date
   * @return the list of users
   */
  @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.deletedAt IS NULL")
  List<User> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
      @Param("endDate") java.time.LocalDateTime endDate);

  /**
   * Finds users by status.
   * 
   * @param status the status
   * @return the list of users
   */
  List<User> findByStatus(UserStatus status);

  /**
   * Finds users by job title.
   * 
   * @param jobTitle the job title
   * @return the list of users
   */
  List<User> findByJobTitle(String jobTitle);

  /**
   * Finds users by gender.
   * 
   * @param gender the gender
   * @return the list of users
   */
  List<User> findByGender(com.programmingmukesh.users.service.users_service.entity.Gender gender);
}
