package com.programmingmukesh.users.service.users_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * User entity representing user profile information and personal details.
 * 
 * <p>
 * This entity stores all user-related information including:
 * </p>
 * <ul>
 * <li>Personal information (name, email, phone, address)</li>
 * <li>Professional details (job title, department, company)</li>
 * <li>Account status and preferences</li>
 * <li>Audit trail and security settings</li>
 * <li>Work preferences and settings</li>
 * <li>Security and privacy settings</li>
 * </ul>
 * 
 * <p>
 * <strong>Database Indexes:</strong>
 * </p>
 * <ul>
 * <li>Primary key on id (UUID)</li>
 * <li>Unique index on email for fast lookups</li>
 * <li>Unique index on username for authentication</li>
 * <li>Index on tenantId for multi-tenancy filtering</li>
 * <li>Index on status for active user queries</li>
 * <li>Index on department for organizational queries</li>
 * <li>Index on managerId for hierarchical queries</li>
 * <li>Index on teamId for team-based queries</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_username", columnList = "username", unique = true),
    @Index(name = "idx_users_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_users_status", columnList = "status"),
    @Index(name = "idx_users_department", columnList = "department"),
    @Index(name = "idx_users_company", columnList = "company"),
    @Index(name = "idx_users_manager_id", columnList = "manager_id"),
    @Index(name = "idx_users_team_id", columnList = "team_id")
})
@Entity
public class User extends BaseEntity {

  /**
   * User's unique username for authentication.
   */
  @Column(name = "username", nullable = false, unique = true, length = 100)
  @NotBlank(message = "Username cannot be blank")
  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
  private String username;

  /**
   * User's first name.
   */
  @Column(name = "first_name", nullable = false, length = 100)
  @NotBlank(message = "First name cannot be blank")
  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
  private String firstName;

  /**
   * User's middle name.
   */
  @Column(name = "middle_name", length = 100)
  @Size(max = 100, message = "Middle name cannot exceed 100 characters")
  private String middleName;

  /**
   * User's last name.
   */
  @Column(name = "last_name", nullable = false, length = 100)
  @NotBlank(message = "Last name cannot be blank")
  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
  private String lastName;

  /**
   * User's display name for public profiles.
   */
  @Column(name = "display_name", length = 200)
  @Size(max = 200, message = "Display name cannot exceed 200 characters")
  private String displayName;

  /**
   * User's email address (unique across the system).
   */
  @Column(name = "email", nullable = false, unique = true, length = 255)
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be in valid format")
  @Size(max = 255, message = "Email cannot exceed 255 characters")
  private String email;

  /**
   * User's phone number.
   */
  @Column(name = "phone_number", length = 20)
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Phone number can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Phone number cannot exceed 20 characters")
  private String phoneNumber;

  /**
   * User's work phone number.
   */
  @Column(name = "work_phone", length = 20)
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Work phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Work phone cannot exceed 20 characters")
  private String workPhone;

  /**
   * User's mobile phone number.
   */
  @Column(name = "mobile_phone", length = 20)
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Mobile phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Mobile phone cannot exceed 20 characters")
  private String mobilePhone;

  /**
   * User's date of birth.
   */
  @Column(name = "birth_date")
  @Past(message = "Date of birth must be in the past")
  private LocalDate birthDate;

  /**
   * User's hire date.
   */
  @Column(name = "hire_date")
  private LocalDate hireDate;

  /**
   * User's gender.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 10)
  private Gender gender;

  /**
   * User's job title or position.
   */
  @Column(name = "job_title", length = 150)
  @Size(max = 150, message = "Job title cannot exceed 150 characters")
  private String jobTitle;

  /**
   * User's department or team.
   */
  @Column(name = "department", length = 100)
  @Size(max = 100, message = "Department cannot exceed 100 characters")
  private String department;

  /**
   * User's company or organization.
   */
  @Column(name = "company", length = 100)
  @Size(max = 100, message = "Company cannot exceed 100 characters")
  private String company;

  /**
   * User's employee ID.
   */
  @Column(name = "employee_id", length = 50)
  @Size(max = 50, message = "Employee ID cannot exceed 50 characters")
  private String employeeId;

  /**
   * User's cost center.
   */
  @Column(name = "cost_center", length = 100)
  @Size(max = 100, message = "Cost center cannot exceed 100 characters")
  private String costCenter;

  /**
   * User's office location.
   */
  @Column(name = "office_location", length = 200)
  @Size(max = 200, message = "Office location cannot exceed 200 characters")
  private String officeLocation;

  /**
   * User's profile picture URL.
   */
  @Column(name = "profile_image_url", length = 500)
  @Size(max = 500, message = "Profile image URL cannot exceed 500 characters")
  private String profileImageUrl;

  /**
   * User's bio or description.
   */
  @Column(name = "bio", columnDefinition = "TEXT")
  @Size(max = 2000, message = "Bio cannot exceed 2000 characters")
  private String bio;

  /**
   * User's website URL.
   */
  @Column(name = "website_url", length = 500)
  @Size(max = 500, message = "Website URL cannot exceed 500 characters")
  private String websiteUrl;

  /**
   * User's LinkedIn URL.
   */
  @Column(name = "linkedin_url", length = 500)
  @Size(max = 500, message = "LinkedIn URL cannot exceed 500 characters")
  private String linkedinUrl;

  /**
   * User's Twitter handle.
   */
  @Column(name = "twitter_handle", length = 100)
  @Size(max = 100, message = "Twitter handle cannot exceed 100 characters")
  private String twitterHandle;

  /**
   * User's address line 1.
   */
  @Column(name = "address_line1", length = 255)
  @Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
  private String addressLine1;

  /**
   * User's address line 2.
   */
  @Column(name = "address_line2", length = 255)
  @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
  private String addressLine2;

  /**
   * User's city.
   */
  @Column(name = "city", length = 100)
  @Size(max = 100, message = "City cannot exceed 100 characters")
  private String city;

  /**
   * User's state or province.
   */
  @Column(name = "state_province", length = 100)
  @Size(max = 100, message = "State/province cannot exceed 100 characters")
  private String stateProvince;

  /**
   * User's postal code.
   */
  @Column(name = "postal_code", length = 20)
  @Size(max = 20, message = "Postal code cannot exceed 20 characters")
  private String postalCode;

  /**
   * User's country.
   */
  @Column(name = "country", length = 100)
  @Size(max = 100, message = "Country cannot exceed 100 characters")
  private String country;

  /**
   * User's account status.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @NotNull(message = "Status cannot be null")
  private UserStatus status = UserStatus.ACTIVE;

  /**
   * User's timezone.
   */
  @Column(name = "timezone", length = 50)
  @Size(max = 50, message = "Timezone cannot exceed 50 characters")
  private String timezone = "UTC";

  /**
   * User's preferred language.
   */
  @Column(name = "language", length = 10)
  @Size(max = 10, message = "Language cannot exceed 10 characters")
  private String language = "en";

  /**
   * User's date format preference.
   */
  @Column(name = "date_format", length = 20)
  @Size(max = 20, message = "Date format cannot exceed 20 characters")
  private String dateFormat = "MM/dd/yyyy";

  /**
   * User's time format preference.
   */
  @Column(name = "time_format", length = 10)
  @Size(max = 10, message = "Time format cannot exceed 10 characters")
  private String timeFormat = "12h";

  /**
   * User's theme preference.
   */
  @Column(name = "theme_preference", length = 20)
  @Size(max = 20, message = "Theme preference cannot exceed 20 characters")
  private String themePreference = "light";

  /**
   * User's currency preference.
   */
  @Column(name = "currency_preference", length = 10)
  @Size(max = 10, message = "Currency preference cannot exceed 10 characters")
  private String currencyPreference = "USD";

  /**
   * User's manager ID for hierarchical structure.
   */
  @Column(name = "manager_id", length = 36)
  private UUID managerId;

  /**
   * User's team ID.
   */
  @Column(name = "team_id", length = 36)
  private UUID teamId;

  /**
   * User's last activity timestamp.
   */
  @Column(name = "last_activity_at")
  private LocalDateTime lastActivityAt;

  /**
   * User's last login timestamp.
   */
  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  /**
   * User's login count.
   */
  @Column(name = "login_count")
  @Min(value = 0, message = "Login count cannot be negative")
  private Long loginCount = 0L;

  /**
   * User's failed login attempts.
   */
  @Column(name = "failed_login_attempts")
  @Min(value = 0, message = "Failed login attempts cannot be negative")
  @Max(value = 10, message = "Failed login attempts cannot exceed 10")
  private Integer failedLoginAttempts = 0;

  /**
   * User's account lockout timestamp.
   */
  @Column(name = "account_locked_until")
  private LocalDateTime accountLockedUntil;

  /**
   * User's last password change timestamp.
   */
  @Column(name = "last_password_change_at")
  private LocalDateTime lastPasswordChangeAt;

  /**
   * User's account verification status.
   */
  @Column(name = "email_verified", nullable = false)
  @NotNull(message = "Email verified status cannot be null")
  private Boolean emailVerified = false;

  /**
   * User's phone verification status.
   */
  @Column(name = "phone_verified", nullable = false)
  @NotNull(message = "Phone verified status cannot be null")
  private Boolean phoneVerified = false;

  /**
   * User's two-factor authentication status.
   */
  @Column(name = "two_factor_enabled", nullable = false)
  @NotNull(message = "Two-factor enabled status cannot be null")
  private Boolean twoFactorEnabled = false;

  /**
   * User's onboarding completion status.
   */
  @Column(name = "onboarding_completed", nullable = false)
  @NotNull(message = "Onboarding completed status cannot be null")
  private Boolean onboardingCompleted = false;

  /**
   * User's onboarding step.
   */
  @Column(name = "onboarding_step")
  @Min(value = 0, message = "Onboarding step cannot be negative")
  private Integer onboardingStep = 0;

  /**
   * User's onboarding completion timestamp.
   */
  @Column(name = "onboarding_completed_at")
  private LocalDateTime onboardingCompletedAt;

  /**
   * User's email notifications preference.
   */
  @Column(name = "email_notifications_enabled", nullable = false)
  @NotNull(message = "Email notifications enabled status cannot be null")
  private Boolean emailNotificationsEnabled = true;

  /**
   * User's push notifications preference.
   */
  @Column(name = "push_notifications_enabled", nullable = false)
  @NotNull(message = "Push notifications enabled status cannot be null")
  private Boolean pushNotificationsEnabled = true;

  /**
   * User's SMS notifications preference.
   */
  @Column(name = "sms_notifications_enabled", nullable = false)
  @NotNull(message = "SMS notifications enabled status cannot be null")
  private Boolean smsNotificationsEnabled = false;

  /**
   * User's profile visibility setting.
   */
  @Column(name = "profile_visibility", length = 20)
  @Size(max = 20, message = "Profile visibility cannot exceed 20 characters")
  private String profileVisibility = "TEAM";

  /**
   * User's activity visibility setting.
   */
  @Column(name = "activity_visibility", length = 20)
  @Size(max = 20, message = "Activity visibility cannot exceed 20 characters")
  private String activityVisibility = "TEAM";

  /**
   * User's email visibility setting.
   */
  @Column(name = "email_visibility", length = 20)
  @Size(max = 20, message = "Email visibility cannot exceed 20 characters")
  private String emailVisibility = "TEAM";

  /**
   * User's phone visibility setting.
   */
  @Column(name = "phone_visibility", length = 20)
  @Size(max = 20, message = "Phone visibility cannot exceed 20 characters")
  private String phoneVisibility = "TEAM";

  /**
   * User's working hours start time.
   */
  @Column(name = "working_hours_start")
  private LocalTime workingHoursStart;

  /**
   * User's working hours end time.
   */
  @Column(name = "working_hours_end")
  private LocalTime workingHoursEnd;

  /**
   * User's working days.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "user_working_days", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "day_of_week")
  private Set<DayOfWeek> workingDays;

  /**
   * User's skills.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "skill", length = 100)
  private Set<String> skills;

  /**
   * User's certifications.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_certifications", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "certification", length = 200)
  private Set<String> certifications;

  /**
   * User's spoken languages.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "language_code", length = 10)
  private Set<String> spokenLanguages;

  /**
   * User's emergency contact name.
   */
  @Column(name = "emergency_contact_name", length = 200)
  @Size(max = 200, message = "Emergency contact name cannot exceed 200 characters")
  private String emergencyContactName;

  /**
   * User's emergency contact phone.
   */
  @Column(name = "emergency_contact_phone", length = 20)
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Emergency contact phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Emergency contact phone cannot exceed 20 characters")
  private String emergencyContactPhone;

  /**
   * User's emergency contact relationship.
   */
  @Column(name = "emergency_contact_relationship", length = 100)
  @Size(max = 100, message = "Emergency contact relationship cannot exceed 100 characters")
  private String emergencyContactRelationship;

  /**
   * User's custom fields (JSON format).
   */
  @Column(name = "custom_fields", columnDefinition = "TEXT")
  @Size(max = 5000, message = "Custom fields JSON cannot exceed 5000 characters")
  private String customFields;

  /**
   * User's GDPR consent status.
   */
  @Column(name = "gdpr_consent_given", nullable = false)
  @NotNull(message = "GDPR consent status cannot be null")
  private Boolean gdprConsentGiven = false;

  /**
   * User's GDPR consent date.
   */
  @Column(name = "gdpr_consent_date")
  private LocalDateTime gdprConsentDate;

  /**
   * User's marketing consent status.
   */
  @Column(name = "marketing_consent_given", nullable = false)
  @NotNull(message = "Marketing consent status cannot be null")
  private Boolean marketingConsentGiven = false;

  /**
   * User's marketing consent date.
   */
  @Column(name = "marketing_consent_date")
  private LocalDateTime marketingConsentDate;

  /**
   * User's account deletion timestamp.
   */
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  /**
   * User's full name (computed field).
   * 
   * @return the full name of the user
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * Checks if the user account is active.
   * 
   * @return true if the user is active, false otherwise
   */
  public boolean isActive() {
    return status == UserStatus.ACTIVE && deletedAt == null;
  }

  /**
   * Checks if the user account is deleted.
   * 
   * @return true if the user is deleted, false otherwise
   */
  public boolean isDeleted() {
    return deletedAt != null;
  }

  /**
   * Checks if the user account is locked.
   * 
   * @return true if the user account is locked, false otherwise
   */
  public boolean isAccountLocked() {
    return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
  }

  /**
   * Soft delete the user account.
   */
  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
    this.status = UserStatus.DELETED;
  }

  /**
   * Update the last activity timestamp.
   */
  public void updateLastActivity() {
    this.lastActivityAt = LocalDateTime.now();
  }

  /**
   * Update the last login timestamp and increment login count.
   */
  public void updateLastLogin() {
    this.lastLoginAt = LocalDateTime.now();
    this.loginCount++;
    this.failedLoginAttempts = 0;
    this.accountLockedUntil = null;
  }

  /**
   * Increment failed login attempts.
   */
  public void incrementFailedLoginAttempts() {
    this.failedLoginAttempts++;
  }

  /**
   * Reset failed login attempts.
   */
  public void resetFailedLoginAttempts() {
    this.failedLoginAttempts = 0;
    this.accountLockedUntil = null;
  }

  /**
   * Update password change timestamp.
   */
  public void updatePasswordChangedAt() {
    this.lastPasswordChangeAt = LocalDateTime.now();
  }
}