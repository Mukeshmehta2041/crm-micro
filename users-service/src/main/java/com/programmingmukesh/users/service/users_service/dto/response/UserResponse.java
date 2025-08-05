package com.programmingmukesh.users.service.users_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.programmingmukesh.users.service.users_service.entity.Gender;
import com.programmingmukesh.users.service.users_service.entity.UserStatus;

/**
 * User Response DTO for API responses.
 * 
 * <p>
 * This DTO represents user data that is safe to expose in API responses.
 * It excludes sensitive information like passwords and internal audit fields.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

  /**
   * User's unique identifier.
   */
  private UUID id;

  /**
   * User's unique username for authentication.
   */
  private String username;

  /**
   * User's first name.
   */
  private String firstName;

  /**
   * User's middle name.
   */
  private String middleName;

  /**
   * User's last name.
   */
  private String lastName;

  /**
   * User's display name for public profiles.
   */
  private String displayName;

  /**
   * User's email address.
   */
  private String email;

  /**
   * User's phone number.
   */
  private String phoneNumber;

  /**
   * User's work phone number.
   */
  private String workPhone;

  /**
   * User's mobile phone number.
   */
  private String mobilePhone;

  /**
   * User's date of birth.
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthDate;

  /**
   * User's hire date.
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate hireDate;

  /**
   * User's gender.
   */
  private Gender gender;

  /**
   * User's job title or position.
   */
  private String jobTitle;

  /**
   * User's department or team.
   */
  private String department;

  /**
   * User's company or organization.
   */
  private String company;

  /**
   * User's employee ID.
   */
  private String employeeId;

  /**
   * User's cost center.
   */
  private String costCenter;

  /**
   * User's office location.
   */
  private String officeLocation;

  /**
   * User's profile picture URL.
   */
  private String profileImageUrl;

  /**
   * User's bio or description.
   */
  private String bio;

  /**
   * User's website URL.
   */
  private String websiteUrl;

  /**
   * User's LinkedIn URL.
   */
  private String linkedinUrl;

  /**
   * User's Twitter handle.
   */
  private String twitterHandle;

  /**
   * User's address line 1.
   */
  private String addressLine1;

  /**
   * User's address line 2.
   */
  private String addressLine2;

  /**
   * User's city.
   */
  private String city;

  /**
   * User's state or province.
   */
  private String stateProvince;

  /**
   * User's postal code.
   */
  private String postalCode;

  /**
   * User's country.
   */
  private String country;

  /**
   * User's account status.
   */
  private UserStatus status;

  /**
   * User's timezone.
   */
  private String timezone;

  /**
   * User's preferred language.
   */
  private String language;

  /**
   * User's date format preference.
   */
  private String dateFormat;

  /**
   * User's time format preference.
   */
  private String timeFormat;

  /**
   * User's theme preference.
   */
  private String themePreference;

  /**
   * User's currency preference.
   */
  private String currencyPreference;

  /**
   * User's manager ID for hierarchical structure.
   */
  private UUID managerId;

  /**
   * User's team ID.
   */
  private UUID teamId;

  /**
   * User's last activity timestamp.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime lastActivityAt;

  /**
   * User's last login timestamp.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime lastLoginAt;

  /**
   * User's login count.
   */
  private Long loginCount;

  /**
   * User's account verification status.
   */
  private Boolean emailVerified;

  /**
   * User's phone verification status.
   */
  private Boolean phoneVerified;

  /**
   * User's two-factor authentication status.
   */
  private Boolean twoFactorEnabled;

  /**
   * User's onboarding completion status.
   */
  private Boolean onboardingCompleted;

  /**
   * User's onboarding step.
   */
  private Integer onboardingStep;

  /**
   * User's onboarding completion timestamp.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime onboardingCompletedAt;

  /**
   * User's email notifications preference.
   */
  private Boolean emailNotificationsEnabled;

  /**
   * User's push notifications preference.
   */
  private Boolean pushNotificationsEnabled;

  /**
   * User's SMS notifications preference.
   */
  private Boolean smsNotificationsEnabled;

  /**
   * User's profile visibility setting.
   */
  private String profileVisibility;

  /**
   * User's activity visibility setting.
   */
  private String activityVisibility;

  /**
   * User's email visibility setting.
   */
  private String emailVisibility;

  /**
   * User's phone visibility setting.
   */
  private String phoneVisibility;

  /**
   * User's working hours start time.
   */
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime workingHoursStart;

  /**
   * User's working hours end time.
   */
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime workingHoursEnd;

  /**
   * User's working days.
   */
  private Set<DayOfWeek> workingDays;

  /**
   * User's skills.
   */
  private Set<String> skills;

  /**
   * User's certifications.
   */
  private Set<String> certifications;

  /**
   * User's spoken languages.
   */
  private Set<String> spokenLanguages;

  /**
   * User's emergency contact name.
   */
  private String emergencyContactName;

  /**
   * User's emergency contact phone.
   */
  private String emergencyContactPhone;

  /**
   * User's emergency contact relationship.
   */
  private String emergencyContactRelationship;

  /**
   * User's custom fields (JSON format).
   */
  private String customFields;

  /**
   * User's GDPR consent status.
   */
  private Boolean gdprConsentGiven;

  /**
   * User's GDPR consent date.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime gdprConsentDate;

  /**
   * User's marketing consent status.
   */
  private Boolean marketingConsentGiven;

  /**
   * User's marketing consent date.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime marketingConsentDate;

  /**
   * User's account creation timestamp.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * User's last update timestamp.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  /**
   * User's full name (computed field).
   * 
   * @return the full name of the user
   */
  public String getFullName() {
    if (firstName == null && lastName == null) {
      return null;
    }
    if (firstName == null) {
      return lastName;
    }
    if (lastName == null) {
      return firstName;
    }
    return firstName + " " + lastName;
  }

  /**
   * Checks if the user account is active.
   * 
   * @return true if the user is active, false otherwise
   */
  public boolean isActive() {
    return status == UserStatus.ACTIVE;
  }

  /**
   * Checks if the user account is deleted.
   * 
   * @return true if the user is deleted, false otherwise
   */
  public boolean isDeleted() {
    return status == UserStatus.DELETED;
  }
}
