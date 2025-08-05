package com.programmingmukesh.users.service.users_service.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

import lombok.*;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmingmukesh.users.service.users_service.entity.Gender;

/**
 * Create User Request DTO for user registration and creation.
 * 
 * <p>
 * This DTO represents the data required to create a new user account.
 * It includes validation annotations to ensure data integrity.
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
public class CreateUserRequest {

  /**
   * User's unique username for authentication.
   */
  @NotBlank(message = "Username cannot be blank")
  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
  private String username;

  /**
   * User's first name.
   */
  @NotBlank(message = "First name cannot be blank")
  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
  private String firstName;

  /**
   * User's middle name.
   */
  @Size(max = 100, message = "Middle name cannot exceed 100 characters")
  private String middleName;

  /**
   * User's last name.
   */
  @NotBlank(message = "Last name cannot be blank")
  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
  private String lastName;

  /**
   * User's display name for public profiles.
   */
  @Size(max = 200, message = "Display name cannot exceed 200 characters")
  private String displayName;

  /**
   * User's email address (unique across the system).
   */
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be in valid format")
  @Size(max = 255, message = "Email cannot exceed 255 characters")
  private String email;

  /**
   * User's phone number.
   */
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Phone number can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Phone number cannot exceed 20 characters")
  private String phoneNumber;

  /**
   * User's work phone number.
   */
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Work phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Work phone cannot exceed 20 characters")
  private String workPhone;

  /**
   * User's mobile phone number.
   */
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Mobile phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Mobile phone cannot exceed 20 characters")
  private String mobilePhone;

  /**
   * User's date of birth.
   */
  @Past(message = "Date of birth must be in the past")
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
  @Size(max = 150, message = "Job title cannot exceed 150 characters")
  private String jobTitle;

  /**
   * User's department or team.
   */
  @Size(max = 100, message = "Department cannot exceed 100 characters")
  private String department;

  /**
   * User's company or organization.
   */
  @Size(max = 100, message = "Company cannot exceed 100 characters")
  private String company;

  /**
   * User's employee ID.
   */
  @Size(max = 50, message = "Employee ID cannot exceed 50 characters")
  private String employeeId;

  /**
   * User's cost center.
   */
  @Size(max = 100, message = "Cost center cannot exceed 100 characters")
  private String costCenter;

  /**
   * User's office location.
   */
  @Size(max = 200, message = "Office location cannot exceed 200 characters")
  private String officeLocation;

  /**
   * User's profile picture URL.
   */
  @Size(max = 500, message = "Profile image URL cannot exceed 500 characters")
  private String profileImageUrl;

  /**
   * User's bio or description.
   */
  @Size(max = 2000, message = "Bio cannot exceed 2000 characters")
  private String bio;

  /**
   * User's website URL.
   */
  @Size(max = 500, message = "Website URL cannot exceed 500 characters")
  private String websiteUrl;

  /**
   * User's LinkedIn URL.
   */
  @Size(max = 500, message = "LinkedIn URL cannot exceed 500 characters")
  private String linkedinUrl;

  /**
   * User's Twitter handle.
   */
  @Size(max = 100, message = "Twitter handle cannot exceed 100 characters")
  private String twitterHandle;

  /**
   * User's address line 1.
   */
  @Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
  private String addressLine1;

  /**
   * User's address line 2.
   */
  @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
  private String addressLine2;

  /**
   * User's city.
   */
  @Size(max = 100, message = "City cannot exceed 100 characters")
  private String city;

  /**
   * User's state or province.
   */
  @Size(max = 100, message = "State/province cannot exceed 100 characters")
  private String stateProvince;

  /**
   * User's postal code.
   */
  @Size(max = 20, message = "Postal code cannot exceed 20 characters")
  private String postalCode;

  /**
   * User's country.
   */
  @Size(max = 100, message = "Country cannot exceed 100 characters")
  private String country;

  /**
   * User's timezone.
   */
  @Size(max = 50, message = "Timezone cannot exceed 50 characters")
  private String timezone = "UTC";

  /**
   * User's preferred language.
   */
  @Size(max = 10, message = "Language cannot exceed 10 characters")
  private String language = "en";

  /**
   * User's date format preference.
   */
  @Size(max = 20, message = "Date format cannot exceed 20 characters")
  private String dateFormat = "MM/dd/yyyy";

  /**
   * User's time format preference.
   */
  @Size(max = 10, message = "Time format cannot exceed 10 characters")
  private String timeFormat = "12h";

  /**
   * User's theme preference.
   */
  @Size(max = 20, message = "Theme preference cannot exceed 20 characters")
  private String themePreference = "light";

  /**
   * User's currency preference.
   */
  @Size(max = 10, message = "Currency preference cannot exceed 10 characters")
  private String currencyPreference = "USD";

  /**
   * User's manager ID for hierarchical structure.
   */
  private UUID managerId;

  /**
   * User's team ID.
   */
  private UUID teamId;

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
  @Size(max = 200, message = "Emergency contact name cannot exceed 200 characters")
  private String emergencyContactName;

  /**
   * User's emergency contact phone.
   */
  @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Emergency contact phone can only contain digits, spaces, parentheses, hyphens, and plus sign")
  @Size(max = 20, message = "Emergency contact phone cannot exceed 20 characters")
  private String emergencyContactPhone;

  /**
   * User's emergency contact relationship.
   */
  @Size(max = 100, message = "Emergency contact relationship cannot exceed 100 characters")
  private String emergencyContactRelationship;

  /**
   * User's custom fields (JSON format).
   */
  @Size(max = 5000, message = "Custom fields JSON cannot exceed 5000 characters")
  private String customFields;

  /**
   * User's GDPR consent status.
   */
  @NotNull(message = "GDPR consent status cannot be null")
  private Boolean gdprConsentGiven = false;

  /**
   * User's marketing consent status.
   */
  @NotNull(message = "Marketing consent status cannot be null")
  private Boolean marketingConsentGiven = false;

  /**
   * User's profile visibility setting.
   */
  @Size(max = 20, message = "Profile visibility cannot exceed 20 characters")
  private String profileVisibility = "TEAM";

  /**
   * User's activity visibility setting.
   */
  @Size(max = 20, message = "Activity visibility cannot exceed 20 characters")
  private String activityVisibility = "TEAM";

  /**
   * User's email visibility setting.
   */
  @Size(max = 20, message = "Email visibility cannot exceed 20 characters")
  private String emailVisibility = "TEAM";

  /**
   * User's phone visibility setting.
   */
  @Size(max = 20, message = "Phone visibility cannot exceed 20 characters")
  private String phoneVisibility = "TEAM";

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
   * Validates that either email or phone number is provided.
   * 
   * @return true if at least one contact method is provided
   */
  public boolean hasContactMethod() {
    return (email != null && !email.trim().isEmpty()) ||
        (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
        (mobilePhone != null && !mobilePhone.trim().isEmpty());
  }

  /**
   * Validates that the user has required information for account creation.
   * 
   * @return true if the user has minimum required information
   */
  public boolean hasMinimumRequiredInfo() {
    return username != null && !username.trim().isEmpty() &&
        firstName != null && !firstName.trim().isEmpty() &&
        lastName != null && !lastName.trim().isEmpty() &&
        hasContactMethod();
  }
}