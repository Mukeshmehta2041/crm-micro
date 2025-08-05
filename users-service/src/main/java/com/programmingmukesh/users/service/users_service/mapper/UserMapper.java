package com.programmingmukesh.users.service.users_service.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.response.UserResponse;
import com.programmingmukesh.users.service.users_service.entity.User;
import com.programmingmukesh.users.service.users_service.entity.UserStatus;

/**
 * User Mapper for converting between User entity and DTOs.
 * 
 * <p>
 * This mapper handles the conversion between:
 * </p>
 * <ul>
 * <li>CreateUserRequest → User entity</li>
 * <li>User entity → UserResponse</li>
 * <li>User entity updates</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Component
public class UserMapper {

  /**
   * Converts CreateUserRequest to User entity for new user creation.
   * 
   * @param request the create user request
   * @return the user entity
   */
  public User toEntity(CreateUserRequest request) {
    if (request == null) {
      return null;
    }

    User user = new User();

    // Set basic information
    user.setUsername(request.getUsername());
    user.setFirstName(request.getFirstName());
    user.setMiddleName(request.getMiddleName());
    user.setLastName(request.getLastName());
    user.setDisplayName(request.getDisplayName());
    user.setEmail(request.getEmail());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setWorkPhone(request.getWorkPhone());
    user.setMobilePhone(request.getMobilePhone());
    user.setBirthDate(request.getBirthDate());
    user.setHireDate(request.getHireDate());
    user.setGender(request.getGender());

    // Set professional information
    user.setJobTitle(request.getJobTitle());
    user.setDepartment(request.getDepartment());
    user.setCompany(request.getCompany());
    user.setEmployeeId(request.getEmployeeId());
    user.setCostCenter(request.getCostCenter());
    user.setOfficeLocation(request.getOfficeLocation());

    // Set social information
    user.setProfileImageUrl(request.getProfileImageUrl());
    user.setBio(request.getBio());
    user.setWebsiteUrl(request.getWebsiteUrl());
    user.setLinkedinUrl(request.getLinkedinUrl());
    user.setTwitterHandle(request.getTwitterHandle());

    // Set address information
    user.setAddressLine1(request.getAddressLine1());
    user.setAddressLine2(request.getAddressLine2());
    user.setCity(request.getCity());
    user.setStateProvince(request.getStateProvince());
    user.setPostalCode(request.getPostalCode());
    user.setCountry(request.getCountry());

    // Set preferences
    user.setTimezone(request.getTimezone() != null ? request.getTimezone() : "UTC");
    user.setLanguage(request.getLanguage() != null ? request.getLanguage() : "en");
    user.setDateFormat(request.getDateFormat() != null ? request.getDateFormat() : "MM/dd/yyyy");
    user.setTimeFormat(request.getTimeFormat() != null ? request.getTimeFormat() : "12h");
    user.setThemePreference(request.getThemePreference() != null ? request.getThemePreference() : "light");
    user.setCurrencyPreference(request.getCurrencyPreference() != null ? request.getCurrencyPreference() : "USD");

    // Set organizational information
    user.setManagerId(request.getManagerId());
    user.setTeamId(request.getTeamId());

    // Set work preferences
    user.setWorkingHoursStart(request.getWorkingHoursStart());
    user.setWorkingHoursEnd(request.getWorkingHoursEnd());
    user.setWorkingDays(request.getWorkingDays());
    user.setSkills(request.getSkills());
    user.setCertifications(request.getCertifications());
    user.setSpokenLanguages(request.getSpokenLanguages());

    // Set emergency contact
    user.setEmergencyContactName(request.getEmergencyContactName());
    user.setEmergencyContactPhone(request.getEmergencyContactPhone());
    user.setEmergencyContactRelationship(request.getEmergencyContactRelationship());

    // Set custom fields
    user.setCustomFields(request.getCustomFields());

    // Set consent information
    user.setGdprConsentGiven(request.getGdprConsentGiven() != null ? request.getGdprConsentGiven() : false);
    user.setMarketingConsentGiven(
        request.getMarketingConsentGiven() != null ? request.getMarketingConsentGiven() : false);

    // Set visibility preferences
    user.setProfileVisibility(request.getProfileVisibility() != null ? request.getProfileVisibility() : "TEAM");
    user.setActivityVisibility(request.getActivityVisibility() != null ? request.getActivityVisibility() : "TEAM");
    user.setEmailVisibility(request.getEmailVisibility() != null ? request.getEmailVisibility() : "TEAM");
    user.setPhoneVisibility(request.getPhoneVisibility() != null ? request.getPhoneVisibility() : "TEAM");

    // Set default values for new user
    user.setStatus(UserStatus.ACTIVE);
    user.setEmailVerified(false);
    user.setPhoneVerified(false);
    user.setTwoFactorEnabled(false);
    user.setOnboardingCompleted(false);
    user.setOnboardingStep(0);
    user.setEmailNotificationsEnabled(true);
    user.setPushNotificationsEnabled(true);
    user.setSmsNotificationsEnabled(false);
    user.setLoginCount(0L);
    user.setFailedLoginAttempts(0);

    // Set timestamps
    LocalDateTime now = LocalDateTime.now();
    user.setCreatedAt(now);
    user.setUpdatedAt(now);

    // Set GDPR consent date if given
    if (Boolean.TRUE.equals(request.getGdprConsentGiven())) {
      user.setGdprConsentDate(now);
    }

    // Set marketing consent date if given
    if (Boolean.TRUE.equals(request.getMarketingConsentGiven())) {
      user.setMarketingConsentDate(now);
    }

    return user;
  }

  /**
   * Converts User entity to UserResponse for API responses.
   * 
   * @param user the user entity
   * @return the user response
   */
  public UserResponse toResponse(User user) {
    if (user == null) {
      return null;
    }

    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .firstName(user.getFirstName())
        .middleName(user.getMiddleName())
        .lastName(user.getLastName())
        .displayName(user.getDisplayName())
        .email(user.getEmail())
        .phoneNumber(user.getPhoneNumber())
        .workPhone(user.getWorkPhone())
        .mobilePhone(user.getMobilePhone())
        .birthDate(user.getBirthDate())
        .hireDate(user.getHireDate())
        .gender(user.getGender())
        .jobTitle(user.getJobTitle())
        .department(user.getDepartment())
        .company(user.getCompany())
        .employeeId(user.getEmployeeId())
        .costCenter(user.getCostCenter())
        .officeLocation(user.getOfficeLocation())
        .profileImageUrl(user.getProfileImageUrl())
        .bio(user.getBio())
        .websiteUrl(user.getWebsiteUrl())
        .linkedinUrl(user.getLinkedinUrl())
        .twitterHandle(user.getTwitterHandle())
        .addressLine1(user.getAddressLine1())
        .addressLine2(user.getAddressLine2())
        .city(user.getCity())
        .stateProvince(user.getStateProvince())
        .postalCode(user.getPostalCode())
        .country(user.getCountry())
        .status(user.getStatus())
        .timezone(user.getTimezone())
        .language(user.getLanguage())
        .dateFormat(user.getDateFormat())
        .timeFormat(user.getTimeFormat())
        .themePreference(user.getThemePreference())
        .currencyPreference(user.getCurrencyPreference())
        .managerId(user.getManagerId())
        .teamId(user.getTeamId())
        .lastActivityAt(user.getLastActivityAt())
        .lastLoginAt(user.getLastLoginAt())
        .loginCount(user.getLoginCount())
        .emailVerified(user.getEmailVerified())
        .phoneVerified(user.getPhoneVerified())
        .twoFactorEnabled(user.getTwoFactorEnabled())
        .onboardingCompleted(user.getOnboardingCompleted())
        .onboardingStep(user.getOnboardingStep())
        .onboardingCompletedAt(user.getOnboardingCompletedAt())
        .emailNotificationsEnabled(user.getEmailNotificationsEnabled())
        .pushNotificationsEnabled(user.getPushNotificationsEnabled())
        .smsNotificationsEnabled(user.getSmsNotificationsEnabled())
        .profileVisibility(user.getProfileVisibility())
        .activityVisibility(user.getActivityVisibility())
        .emailVisibility(user.getEmailVisibility())
        .phoneVisibility(user.getPhoneVisibility())
        .workingHoursStart(user.getWorkingHoursStart())
        .workingHoursEnd(user.getWorkingHoursEnd())
        .workingDays(user.getWorkingDays())
        .skills(user.getSkills())
        .certifications(user.getCertifications())
        .spokenLanguages(user.getSpokenLanguages())
        .emergencyContactName(user.getEmergencyContactName())
        .emergencyContactPhone(user.getEmergencyContactPhone())
        .emergencyContactRelationship(user.getEmergencyContactRelationship())
        .customFields(user.getCustomFields())
        .gdprConsentGiven(user.getGdprConsentGiven())
        .gdprConsentDate(user.getGdprConsentDate())
        .marketingConsentGiven(user.getMarketingConsentGiven())
        .marketingConsentDate(user.getMarketingConsentDate())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }

  /**
   * Updates an existing User entity with data from CreateUserRequest.
   * 
   * @param user    the existing user entity
   * @param request the update request
   * @return the updated user entity
   */
  public User updateEntity(User user, CreateUserRequest request) {
    if (user == null || request == null) {
      return user;
    }

    // Update basic information (only if provided)
    if (request.getUsername() != null) {
      user.setUsername(request.getUsername());
    }
    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getMiddleName() != null) {
      user.setMiddleName(request.getMiddleName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    if (request.getDisplayName() != null) {
      user.setDisplayName(request.getDisplayName());
    }
    if (request.getEmail() != null) {
      user.setEmail(request.getEmail());
    }
    if (request.getPhoneNumber() != null) {
      user.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getWorkPhone() != null) {
      user.setWorkPhone(request.getWorkPhone());
    }
    if (request.getMobilePhone() != null) {
      user.setMobilePhone(request.getMobilePhone());
    }
    if (request.getBirthDate() != null) {
      user.setBirthDate(request.getBirthDate());
    }
    if (request.getHireDate() != null) {
      user.setHireDate(request.getHireDate());
    }
    if (request.getGender() != null) {
      user.setGender(request.getGender());
    }

    // Update professional information
    if (request.getJobTitle() != null) {
      user.setJobTitle(request.getJobTitle());
    }
    if (request.getDepartment() != null) {
      user.setDepartment(request.getDepartment());
    }
    if (request.getCompany() != null) {
      user.setCompany(request.getCompany());
    }
    if (request.getEmployeeId() != null) {
      user.setEmployeeId(request.getEmployeeId());
    }
    if (request.getCostCenter() != null) {
      user.setCostCenter(request.getCostCenter());
    }
    if (request.getOfficeLocation() != null) {
      user.setOfficeLocation(request.getOfficeLocation());
    }

    // Update social information
    if (request.getProfileImageUrl() != null) {
      user.setProfileImageUrl(request.getProfileImageUrl());
    }
    if (request.getBio() != null) {
      user.setBio(request.getBio());
    }
    if (request.getWebsiteUrl() != null) {
      user.setWebsiteUrl(request.getWebsiteUrl());
    }
    if (request.getLinkedinUrl() != null) {
      user.setLinkedinUrl(request.getLinkedinUrl());
    }
    if (request.getTwitterHandle() != null) {
      user.setTwitterHandle(request.getTwitterHandle());
    }

    // Update address information
    if (request.getAddressLine1() != null) {
      user.setAddressLine1(request.getAddressLine1());
    }
    if (request.getAddressLine2() != null) {
      user.setAddressLine2(request.getAddressLine2());
    }
    if (request.getCity() != null) {
      user.setCity(request.getCity());
    }
    if (request.getStateProvince() != null) {
      user.setStateProvince(request.getStateProvince());
    }
    if (request.getPostalCode() != null) {
      user.setPostalCode(request.getPostalCode());
    }
    if (request.getCountry() != null) {
      user.setCountry(request.getCountry());
    }

    // Update preferences
    if (request.getTimezone() != null) {
      user.setTimezone(request.getTimezone());
    }
    if (request.getLanguage() != null) {
      user.setLanguage(request.getLanguage());
    }
    if (request.getDateFormat() != null) {
      user.setDateFormat(request.getDateFormat());
    }
    if (request.getTimeFormat() != null) {
      user.setTimeFormat(request.getTimeFormat());
    }
    if (request.getThemePreference() != null) {
      user.setThemePreference(request.getThemePreference());
    }
    if (request.getCurrencyPreference() != null) {
      user.setCurrencyPreference(request.getCurrencyPreference());
    }

    // Update organizational information
    if (request.getManagerId() != null) {
      user.setManagerId(request.getManagerId());
    }
    if (request.getTeamId() != null) {
      user.setTeamId(request.getTeamId());
    }

    // Update work preferences
    if (request.getWorkingHoursStart() != null) {
      user.setWorkingHoursStart(request.getWorkingHoursStart());
    }
    if (request.getWorkingHoursEnd() != null) {
      user.setWorkingHoursEnd(request.getWorkingHoursEnd());
    }
    if (request.getWorkingDays() != null) {
      user.setWorkingDays(request.getWorkingDays());
    }
    if (request.getSkills() != null) {
      user.setSkills(request.getSkills());
    }
    if (request.getCertifications() != null) {
      user.setCertifications(request.getCertifications());
    }
    if (request.getSpokenLanguages() != null) {
      user.setSpokenLanguages(request.getSpokenLanguages());
    }

    // Update emergency contact
    if (request.getEmergencyContactName() != null) {
      user.setEmergencyContactName(request.getEmergencyContactName());
    }
    if (request.getEmergencyContactPhone() != null) {
      user.setEmergencyContactPhone(request.getEmergencyContactPhone());
    }
    if (request.getEmergencyContactRelationship() != null) {
      user.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
    }

    // Update custom fields
    if (request.getCustomFields() != null) {
      user.setCustomFields(request.getCustomFields());
    }

    // Update consent information
    if (request.getGdprConsentGiven() != null) {
      user.setGdprConsentGiven(request.getGdprConsentGiven());
      if (Boolean.TRUE.equals(request.getGdprConsentGiven()) && user.getGdprConsentDate() == null) {
        user.setGdprConsentDate(LocalDateTime.now());
      }
    }
    if (request.getMarketingConsentGiven() != null) {
      user.setMarketingConsentGiven(request.getMarketingConsentGiven());
      if (Boolean.TRUE.equals(request.getMarketingConsentGiven()) && user.getMarketingConsentDate() == null) {
        user.setMarketingConsentDate(LocalDateTime.now());
      }
    }

    // Update visibility preferences
    if (request.getProfileVisibility() != null) {
      user.setProfileVisibility(request.getProfileVisibility());
    }
    if (request.getActivityVisibility() != null) {
      user.setActivityVisibility(request.getActivityVisibility());
    }
    if (request.getEmailVisibility() != null) {
      user.setEmailVisibility(request.getEmailVisibility());
    }
    if (request.getPhoneVisibility() != null) {
      user.setPhoneVisibility(request.getPhoneVisibility());
    }

    // Update timestamp
    user.setUpdatedAt(LocalDateTime.now());

    return user;
  }

  /**
   * Creates a minimal UserResponse for basic user information.
   * 
   * @param user the user entity
   * @return the minimal user response
   */
  public UserResponse toMinimalResponse(User user) {
    if (user == null) {
      return null;
    }

    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .displayName(user.getDisplayName())
        .email(user.getEmail())
        .profileImageUrl(user.getProfileImageUrl())
        .status(user.getStatus())
        .createdAt(user.getCreatedAt())
        .build();
  }
}