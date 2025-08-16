package com.programmingmukesh.users.service.users_service.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

import com.programmingmukesh.users.service.users_service.entity.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update User Request DTO for partial user updates.
 * All fields are optional to support PATCH operations.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
  private String username;

  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  private String firstName;

  @Size(max = 100, message = "Middle name cannot exceed 100 characters")
  private String middleName;

  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  private String lastName;

  @Size(max = 200, message = "Display name cannot exceed 200 characters")
  private String displayName;

  @Email(message = "Invalid email format")
  @Size(max = 255, message = "Email cannot exceed 255 characters")
  private String email;

  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Invalid phone number format")
  private String phoneNumber;

  private String workPhone;
  private String mobilePhone;
  private LocalDate birthDate;
  private LocalDate hireDate;
  private Gender gender;

  @Size(max = 200, message = "Job title cannot exceed 200 characters")
  private String jobTitle;

  @Size(max = 100, message = "Department cannot exceed 100 characters")
  private String department;

  @Size(max = 100, message = "Company cannot exceed 100 characters")
  private String company;

  private String employeeId;
  private String costCenter;
  private String officeLocation;
  private String profileImageUrl;

  @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
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
  private LocalTime workingHoursStart;
  private LocalTime workingHoursEnd;
  private Set<DayOfWeek> workingDays;
  private Set<String> skills;
  private Set<String> certifications;
  private Set<String> spokenLanguages;
  private String emergencyContactName;
  private String emergencyContactPhone;
  private String emergencyContactRelationship;
  private String customFields;
  private Boolean gdprConsentGiven;
  private Boolean marketingConsentGiven;
  private String profileVisibility;
  private String activityVisibility;
  private String emailVisibility;
  private String phoneVisibility;

  /**
   * Checks if the request has any fields to update.
   * 
   * @return true if at least one field is provided
   */
  public boolean hasUpdates() {
    return username != null || firstName != null || middleName != null || lastName != null ||
        displayName != null || email != null || phoneNumber != null || workPhone != null ||
        mobilePhone != null || birthDate != null || hireDate != null || gender != null ||
        jobTitle != null || department != null || company != null || employeeId != null ||
        costCenter != null || officeLocation != null || profileImageUrl != null || bio != null ||
        websiteUrl != null || linkedinUrl != null || twitterHandle != null || addressLine1 != null ||
        addressLine2 != null || city != null || stateProvince != null || postalCode != null ||
        country != null || timezone != null || language != null || dateFormat != null ||
        timeFormat != null || themePreference != null || currencyPreference != null ||
        managerId != null || teamId != null || workingHoursStart != null || workingHoursEnd != null ||
        workingDays != null || skills != null || certifications != null || spokenLanguages != null ||
        emergencyContactName != null || emergencyContactPhone != null || emergencyContactRelationship != null ||
        customFields != null || gdprConsentGiven != null || marketingConsentGiven != null ||
        profileVisibility != null || activityVisibility != null || emailVisibility != null ||
        phoneVisibility != null;
  }
}
