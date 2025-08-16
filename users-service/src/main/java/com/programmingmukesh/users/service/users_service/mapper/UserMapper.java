package com.programmingmukesh.users.service.users_service.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.programmingmukesh.users.service.users_service.dto.request.CreateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.request.UpdateUserRequest;
import com.programmingmukesh.users.service.users_service.dto.response.UserResponse;
import com.programmingmukesh.users.service.users_service.entity.User;
import com.programmingmukesh.users.service.users_service.entity.UserStatus;

/**
 * User Mapper for converting between User entity and DTOs using MapStruct.
 * 
 * <p>
 * This mapper handles the conversion between:
 * </p>
 * <ul>
 * <li>CreateUserRequest → User entity</li>
 * <li>User entity → UserResponse</li>
 * <li>User entity updates with UpdateUserRequest</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  /**
   * Converts CreateUserRequest to User entity for new user creation.
   * 
   * @param request the create user request
   * @return the user entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tenantId", ignore = true)
  @Mapping(target = "status", constant = "ACTIVE")
  @Mapping(target = "emailVerified", constant = "false")
  @Mapping(target = "phoneVerified", constant = "false")
  @Mapping(target = "twoFactorEnabled", constant = "false")
  @Mapping(target = "onboardingCompleted", constant = "false")
  @Mapping(target = "onboardingStep", constant = "0")
  @Mapping(target = "emailNotificationsEnabled", constant = "true")
  @Mapping(target = "pushNotificationsEnabled", constant = "true")
  @Mapping(target = "smsNotificationsEnabled", constant = "false")
  @Mapping(target = "loginCount", constant = "0L")
  @Mapping(target = "failedLoginAttempts", constant = "0")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "lastActivityAt", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "accountLockedUntil", ignore = true)
  @Mapping(target = "lastPasswordChangeAt", ignore = true)
  @Mapping(target = "onboardingCompletedAt", ignore = true)
  @Mapping(target = "timezone", defaultValue = "UTC")
  @Mapping(target = "language", defaultValue = "en")
  @Mapping(target = "dateFormat", defaultValue = "MM/dd/yyyy")
  @Mapping(target = "timeFormat", defaultValue = "12h")
  @Mapping(target = "themePreference", defaultValue = "light")
  @Mapping(target = "currencyPreference", defaultValue = "USD")
  @Mapping(target = "profileVisibility", defaultValue = "TEAM")
  @Mapping(target = "activityVisibility", defaultValue = "TEAM")
  @Mapping(target = "emailVisibility", defaultValue = "TEAM")
  @Mapping(target = "phoneVisibility", defaultValue = "TEAM")
  @Mapping(target = "gdprConsentGiven", defaultValue = "false")
  @Mapping(target = "marketingConsentGiven", defaultValue = "false")
  @Mapping(target = "gdprConsentDate", expression = "java(mapGdprConsentDate(request.getGdprConsentGiven()))")
  @Mapping(target = "marketingConsentDate", expression = "java(mapMarketingConsentDate(request.getMarketingConsentGiven()))")
  User toEntity(CreateUserRequest request);

  /**
   * Helper method to set GDPR consent date.
   */
  default LocalDateTime mapGdprConsentDate(Boolean gdprConsentGiven) {
    return Boolean.TRUE.equals(gdprConsentGiven) ? LocalDateTime.now() : null;
  }

  /**
   * Helper method to set marketing consent date.
   */
  default LocalDateTime mapMarketingConsentDate(Boolean marketingConsentGiven) {
    return Boolean.TRUE.equals(marketingConsentGiven) ? LocalDateTime.now() : null;
  }

  /**
   * Converts User entity to UserResponse for API responses.
   * 
   * @param user the user entity
   * @return the user response
   */
  UserResponse toResponse(User user);

  /**
   * Updates an existing User entity with data from CreateUserRequest.
   * 
   * @param user    the existing user entity
   * @param request the update request
   * @return the updated user entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tenantId", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "emailVerified", ignore = true)
  @Mapping(target = "phoneVerified", ignore = true)
  @Mapping(target = "twoFactorEnabled", ignore = true)
  @Mapping(target = "onboardingCompleted", ignore = true)
  @Mapping(target = "onboardingStep", ignore = true)
  @Mapping(target = "onboardingCompletedAt", ignore = true)
  @Mapping(target = "emailNotificationsEnabled", ignore = true)
  @Mapping(target = "pushNotificationsEnabled", ignore = true)
  @Mapping(target = "smsNotificationsEnabled", ignore = true)
  @Mapping(target = "loginCount", ignore = true)
  @Mapping(target = "failedLoginAttempts", ignore = true)
  @Mapping(target = "lastActivityAt", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "accountLockedUntil", ignore = true)
  @Mapping(target = "lastPasswordChangeAt", ignore = true)
  @Mapping(target = "gdprConsentDate", expression = "java(updateGdprConsentDate(user, request.getGdprConsentGiven()))")
  @Mapping(target = "marketingConsentDate", expression = "java(updateMarketingConsentDate(user, request.getMarketingConsentGiven()))")
  User updateEntity(@MappingTarget User user, CreateUserRequest request);

  /**
   * Helper method to update GDPR consent date.
   */
  default LocalDateTime updateGdprConsentDate(User user, Boolean gdprConsentGiven) {
    if (Boolean.TRUE.equals(gdprConsentGiven) && user.getGdprConsentDate() == null) {
      return LocalDateTime.now();
    }
    return user.getGdprConsentDate();
  }

  /**
   * Helper method to update marketing consent date.
   */
  default LocalDateTime updateMarketingConsentDate(User user, Boolean marketingConsentGiven) {
    if (Boolean.TRUE.equals(marketingConsentGiven) && user.getMarketingConsentDate() == null) {
      return LocalDateTime.now();
    }
    return user.getMarketingConsentDate();
  }

  /**
   * Updates an existing User entity with data from UpdateUserRequest.
   * 
   * @param user    the existing user entity
   * @param request the update request
   * @return the updated user entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tenantId", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "emailVerified", ignore = true)
  @Mapping(target = "phoneVerified", ignore = true)
  @Mapping(target = "twoFactorEnabled", ignore = true)
  @Mapping(target = "onboardingCompleted", ignore = true)
  @Mapping(target = "onboardingStep", ignore = true)
  @Mapping(target = "onboardingCompletedAt", ignore = true)
  @Mapping(target = "emailNotificationsEnabled", ignore = true)
  @Mapping(target = "pushNotificationsEnabled", ignore = true)
  @Mapping(target = "smsNotificationsEnabled", ignore = true)
  @Mapping(target = "loginCount", ignore = true)
  @Mapping(target = "failedLoginAttempts", ignore = true)
  @Mapping(target = "lastActivityAt", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "accountLockedUntil", ignore = true)
  @Mapping(target = "lastPasswordChangeAt", ignore = true)
  @Mapping(target = "gdprConsentDate", expression = "java(updateGdprConsentDateFromUpdate(user, request.getGdprConsentGiven()))")
  @Mapping(target = "marketingConsentDate", expression = "java(updateMarketingConsentDateFromUpdate(user, request.getMarketingConsentGiven()))")
  User updateEntity(@MappingTarget User user, UpdateUserRequest request);

  /**
   * Helper method to update GDPR consent date from UpdateUserRequest.
   */
  default LocalDateTime updateGdprConsentDateFromUpdate(User user, Boolean gdprConsentGiven) {
    if (Boolean.TRUE.equals(gdprConsentGiven) && user.getGdprConsentDate() == null) {
      return LocalDateTime.now();
    }
    return user.getGdprConsentDate();
  }

  /**
   * Helper method to update marketing consent date from UpdateUserRequest.
   */
  default LocalDateTime updateMarketingConsentDateFromUpdate(User user, Boolean marketingConsentGiven) {
    if (Boolean.TRUE.equals(marketingConsentGiven) && user.getMarketingConsentDate() == null) {
      return LocalDateTime.now();
    }
    return user.getMarketingConsentDate();
  }

  /**
   * Creates a minimal UserResponse for basic user information.
   * 
   * @param user the user entity
   * @return the minimal user response
   */
  @Mapping(target = "phoneNumber", ignore = true)
  @Mapping(target = "workPhone", ignore = true)
  @Mapping(target = "mobilePhone", ignore = true)
  @Mapping(target = "birthDate", ignore = true)
  @Mapping(target = "hireDate", ignore = true)
  @Mapping(target = "gender", ignore = true)
  @Mapping(target = "jobTitle", ignore = true)
  @Mapping(target = "department", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "employeeId", ignore = true)
  @Mapping(target = "costCenter", ignore = true)
  @Mapping(target = "officeLocation", ignore = true)
  @Mapping(target = "bio", ignore = true)
  @Mapping(target = "websiteUrl", ignore = true)
  @Mapping(target = "linkedinUrl", ignore = true)
  @Mapping(target = "twitterHandle", ignore = true)
  @Mapping(target = "addressLine1", ignore = true)
  @Mapping(target = "addressLine2", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "stateProvince", ignore = true)
  @Mapping(target = "postalCode", ignore = true)
  @Mapping(target = "country", ignore = true)
  @Mapping(target = "timezone", ignore = true)
  @Mapping(target = "language", ignore = true)
  @Mapping(target = "dateFormat", ignore = true)
  @Mapping(target = "timeFormat", ignore = true)
  @Mapping(target = "themePreference", ignore = true)
  @Mapping(target = "currencyPreference", ignore = true)
  @Mapping(target = "managerId", ignore = true)
  @Mapping(target = "teamId", ignore = true)
  @Mapping(target = "lastActivityAt", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "loginCount", ignore = true)
  @Mapping(target = "emailVerified", ignore = true)
  @Mapping(target = "phoneVerified", ignore = true)
  @Mapping(target = "twoFactorEnabled", ignore = true)
  @Mapping(target = "onboardingCompleted", ignore = true)
  @Mapping(target = "onboardingStep", ignore = true)
  @Mapping(target = "onboardingCompletedAt", ignore = true)
  @Mapping(target = "emailNotificationsEnabled", ignore = true)
  @Mapping(target = "pushNotificationsEnabled", ignore = true)
  @Mapping(target = "smsNotificationsEnabled", ignore = true)
  @Mapping(target = "profileVisibility", ignore = true)
  @Mapping(target = "activityVisibility", ignore = true)
  @Mapping(target = "emailVisibility", ignore = true)
  @Mapping(target = "phoneVisibility", ignore = true)
  @Mapping(target = "workingHoursStart", ignore = true)
  @Mapping(target = "workingHoursEnd", ignore = true)
  @Mapping(target = "workingDays", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "certifications", ignore = true)
  @Mapping(target = "spokenLanguages", ignore = true)
  @Mapping(target = "emergencyContactName", ignore = true)
  @Mapping(target = "emergencyContactPhone", ignore = true)
  @Mapping(target = "emergencyContactRelationship", ignore = true)
  @Mapping(target = "customFields", ignore = true)
  @Mapping(target = "gdprConsentGiven", ignore = true)
  @Mapping(target = "gdprConsentDate", ignore = true)
  @Mapping(target = "marketingConsentGiven", ignore = true)
  @Mapping(target = "marketingConsentDate", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  UserResponse toMinimalResponse(User user);
}