package com.programmingmukesh.auth.service.auth_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;

/**
 * UserCredential entity representing user authentication credentials and
 * security settings.
 * 
 * <p>
 * This entity stores all authentication-related information including:
 * </p>
 * <ul>
 * <li>User identification (userId, username, email)</li>
 * <li>Password security (hashed password, password change tracking)</li>
 * <li>Account security (login attempts, account locking)</li>
 * <li>Multi-factor authentication settings</li>
 * <li>Email verification status</li>
 * <li>Trusted devices for enhanced security</li>
 * </ul>
 * 
 * <p>
 * <strong>Security Features:</strong>
 * </p>
 * <ul>
 * <li>Password hashing for secure storage</li>
 * <li>Account lockout mechanism after failed attempts</li>
 * <li>Multi-factor authentication support</li>
 * <li>Email verification tracking</li>
 * <li>Trusted device management</li>
 * <li>Audit trail for security events</li>
 * </ul>
 * 
 * <p>
 * <strong>Database Indexes:</strong>
 * </p>
 * <ul>
 * <li>Primary key on id (UUID)</li>
 * <li>Unique index on username for fast login lookups</li>
 * <li>Unique index on email for fast login and verification</li>
 * <li>Index on userId for user relationship queries</li>
 * <li>Index on tenantId for multi-tenancy filtering</li>
 * <li>Index on emailVerified for verification status queries</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Table(name = "user_credentials", indexes = {
    @Index(name = "idx_user_credentials_username", columnList = "username", unique = true),
    @Index(name = "idx_user_credentials_email", columnList = "email", unique = true),
    @Index(name = "idx_user_credentials_user_id", columnList = "user_id"),
    @Index(name = "idx_user_credentials_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_user_credentials_email_verified", columnList = "email_verified"),
    @Index(name = "idx_user_credentials_mfa_enabled", columnList = "mfa_enabled"),
    @Index(name = "idx_user_credentials_account_locked", columnList = "account_locked_until")
})
@Entity
public class UserCredential extends BaseEntity {

  /**
   * Reference to the main user entity.
   * This creates a relationship with the user profile information.
   */
  @Column(name = "user_id", nullable = false, length = 36)
  @NotNull(message = "User ID cannot be null")
  private UUID userId;

  /**
   * Unique username for login authentication.
   * Must be alphanumeric with optional underscores and hyphens.
   */
  @Column(name = "username", nullable = false, unique = true, length = 50)
  @NotBlank(message = "Username cannot be blank")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
  private String username;

  /**
   * User's email address for login and notifications.
   * Must be a valid email format and unique across the system.
   */
  @Column(name = "email", nullable = false, unique = true, length = 255)
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be in valid format")
  @Size(max = 255, message = "Email cannot exceed 255 characters")
  private String email;

  /**
   * Securely hashed password using strong cryptographic algorithms.
   * Never store plain text passwords in the database.
   */
  @Column(name = "password_hash", nullable = false, length = 255)
  @NotBlank(message = "Password hash cannot be blank")
  @Size(max = 255, message = "Password hash cannot exceed 255 characters")
  private String passwordHash;

  /**
   * Timestamp of the user's last successful login.
   * Used for security monitoring and session management.
   */
  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  /**
   * Timestamp when the password was last changed.
   * Used for password expiration policies and security audits.
   */
  @Column(name = "password_changed_at")
  private LocalDateTime passwordChangedAt;

  /**
   * Counter for failed login attempts.
   * Used to implement account lockout policies.
   */
  @Column(name = "failed_login_attempts", nullable = false)
  @Min(value = 0, message = "Failed login attempts cannot be negative")
  @Max(value = 10, message = "Failed login attempts cannot exceed 10")
  private Integer failedLoginAttempts = 0;

  /**
   * Timestamp until which the account is locked due to security violations.
   * Null when account is not locked.
   */
  @Column(name = "account_locked_until")
  private LocalDateTime accountLockedUntil;

  /**
   * Flag indicating whether the user's email has been verified.
   * Required for certain operations and security features.
   */
  @Column(name = "email_verified", nullable = false)
  @NotNull(message = "Email verified status cannot be null")
  private Boolean emailVerified = false;

  /**
   * Flag indicating whether multi-factor authentication is enabled.
   * When true, additional authentication factors are required.
   */
  @Column(name = "mfa_enabled", nullable = false)
  @NotNull(message = "MFA enabled status cannot be null")
  private Boolean mfaEnabled = false;

  /**
   * Secret key for TOTP-based multi-factor authentication.
   * Stored securely and used to generate time-based codes.
   */
  @Column(name = "mfa_secret", length = 32)
  @Size(max = 32, message = "MFA secret cannot exceed 32 characters")
  private String mfaSecret;

  /**
   * JSON array of backup codes for account recovery.
   * Used when primary MFA methods are unavailable.
   */
  @Column(name = "backup_codes", columnDefinition = "TEXT")
  @Size(max = 1000, message = "Backup codes JSON cannot exceed 1000 characters")
  private String backupCodes; // JSON array of backup codes

  /**
   * The type of multi-factor authentication method being used.
   * Determines the authentication flow and UI components.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "mfa_method", length = 20)
  private MfaMethod mfaMethod;

  /**
   * JSON array of trusted device fingerprints.
   * Used to remember devices and reduce MFA prompts.
   */
  @Column(name = "trusted_devices", columnDefinition = "TEXT")
  @Size(max = 2000, message = "Trusted devices JSON cannot exceed 2000 characters")
  private String trustedDevices; // JSON array of trusted device fingerprints

  /**
   * Checks if the account is currently locked.
   * 
   * @return true if the account is locked, false otherwise
   */
  public boolean isAccountLocked() {
    return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
  }

  /**
   * Increments the failed login attempts counter.
   * Should be called when a login attempt fails.
   */
  public void incrementFailedLoginAttempts() {
    this.failedLoginAttempts++;
  }

  /**
   * Resets the failed login attempts counter.
   * Should be called after a successful login.
   */
  public void resetFailedLoginAttempts() {
    this.failedLoginAttempts = 0;
    this.accountLockedUntil = null;
  }

  /**
   * Updates the last login timestamp.
   * Should be called after a successful login.
   */
  public void updateLastLogin() {
    this.lastLoginAt = LocalDateTime.now();
    resetFailedLoginAttempts();
  }

  /**
   * Updates the password change timestamp.
   * Should be called when the password is changed.
   */
  public void updatePasswordChangedAt() {
    this.passwordChangedAt = LocalDateTime.now();
  }
}
