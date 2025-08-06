package com.programmingmukesh.auth.service.auth_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user response from users service.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

  private UUID id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String displayName;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String fullName;

  // Additional fields that might be present in the response
  private String middleName;
  private String phoneNumber;
  private String department;
  private String company;
  private String jobTitle;
  private String employeeId;
  private String officeLocation;
  private String profileImageUrl;
  private String bio;
  private String websiteUrl;
  private String linkedinUrl;
  private String twitterHandle;
  private String addressLine1;
  private String addressLine2;
  private String city;
  private String stateProvince;
  private String postalCode;
  private String country;
  private String timezone;
  private String language;
  private String dateFormat;
  private String timeFormat;
  private String themePreference;
  private String currencyPreference;
  private UUID managerId;
  private UUID teamId;
  private LocalDateTime lastActivityAt;
  private LocalDateTime lastLoginAt;
  private Long loginCount;
  private Integer failedLoginAttempts;
  private LocalDateTime accountLockedUntil;
  private LocalDateTime lastPasswordChangeAt;
  private Boolean emailVerified;
  private Boolean phoneVerified;
  private Boolean twoFactorEnabled;
  private Boolean onboardingCompleted;
  private Integer onboardingStep;
  private LocalDateTime onboardingCompletedAt;
  private Boolean emailNotificationsEnabled;
  private Boolean pushNotificationsEnabled;
  private Boolean smsNotificationsEnabled;
  private String profileVisibility;
  private String activityVisibility;
  private String emailVisibility;
  private String phoneVisibility;
  private String emergencyContactName;
  private String emergencyContactPhone;
  private String emergencyContactRelationship;
  private String customFields;
}