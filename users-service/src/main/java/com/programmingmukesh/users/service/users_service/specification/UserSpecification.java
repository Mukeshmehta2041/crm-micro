package com.programmingmukesh.users.service.users_service.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.programmingmukesh.users.service.users_service.entity.User;
import com.programmingmukesh.users.service.users_service.entity.UserStatus;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification helper class for building dynamic JPA queries for User entity.
 * 
 * <p>
 * This class provides methods to create complex search specifications
 * using the Criteria API for flexible and type-safe database queries.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public final class UserSpecification {

  private UserSpecification() {
    // Utility class - prevent instantiation
  }

  /**
   * Creates a comprehensive search specification based on multiple criteria.
   * 
   * @param query      general search term for name, username, or email
   * @param department filter by department
   * @param company    filter by company
   * @param status     filter by user status
   * @return the combined specification
   */
  public static Specification<User> createSearchSpecification(String query, String department,
      String company, String status) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Always exclude deleted users
      predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

      // General search query (searches in firstName, lastName, username, email)
      if (StringUtils.hasText(query)) {
        String searchTerm = "%" + query.toLowerCase().trim() + "%";

        Predicate firstNamePredicate = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("firstName")), searchTerm);
        Predicate lastNamePredicate = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("lastName")), searchTerm);
        Predicate usernamePredicate = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("username")), searchTerm);
        Predicate emailPredicate = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("email")), searchTerm);
        Predicate displayNamePredicate = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("displayName")), searchTerm);

        predicates.add(criteriaBuilder.or(
            firstNamePredicate, lastNamePredicate, usernamePredicate,
            emailPredicate, displayNamePredicate));
      }

      // Department filter
      if (StringUtils.hasText(department)) {
        predicates.add(criteriaBuilder.equal(
            criteriaBuilder.lower(root.get("department")),
            department.toLowerCase().trim()));
      }

      // Company filter
      if (StringUtils.hasText(company)) {
        predicates.add(criteriaBuilder.equal(
            criteriaBuilder.lower(root.get("company")),
            company.toLowerCase().trim()));
      }

      // Status filter
      if (StringUtils.hasText(status)) {
        try {
          UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
          predicates.add(criteriaBuilder.equal(root.get("status"), userStatus));
        } catch (IllegalArgumentException e) {
          // Invalid status provided, ignore this filter
        }
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * Creates specification for users by department.
   */
  public static Specification<User> byDepartment(String department) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(department)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get("department")),
              department.toLowerCase().trim()));
    };
  }

  /**
   * Creates specification for users by company.
   */
  public static Specification<User> byCompany(String company) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(company)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get("company")),
              company.toLowerCase().trim()));
    };
  }

  /**
   * Creates specification for users by status.
   */
  public static Specification<User> byStatus(UserStatus status) {
    return (root, query, criteriaBuilder) -> {
      if (status == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(root.get("status"), status));
    };
  }

  /**
   * Creates specification for active users only.
   */
  public static Specification<User> activeUsers() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and(
        criteriaBuilder.isNull(root.get("deletedAt")),
        criteriaBuilder.equal(root.get("status"), UserStatus.ACTIVE));
  }

  /**
   * Creates specification for users created within a date range.
   */
  public static Specification<User> createdBetween(java.time.LocalDateTime startDate,
      java.time.LocalDateTime endDate) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

      if (startDate != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
            root.get("createdAt"), startDate));
      }

      if (endDate != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(
            root.get("createdAt"), endDate));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * Creates specification for users by manager ID.
   */
  public static Specification<User> byManagerId(java.util.UUID managerId) {
    return (root, query, criteriaBuilder) -> {
      if (managerId == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(root.get("managerId"), managerId));
    };
  }

  /**
   * Creates specification for users by team ID.
   */
  public static Specification<User> byTeamId(java.util.UUID teamId) {
    return (root, query, criteriaBuilder) -> {
      if (teamId == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(root.get("teamId"), teamId));
    };
  }

  /**
   * Creates specification for users with verified email.
   */
  public static Specification<User> withVerifiedEmail() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and(
        criteriaBuilder.isNull(root.get("deletedAt")),
        criteriaBuilder.equal(root.get("emailVerified"), true));
  }

  /**
   * Creates specification for users with two-factor authentication enabled.
   */
  public static Specification<User> withTwoFactorEnabled() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and(
        criteriaBuilder.isNull(root.get("deletedAt")),
        criteriaBuilder.equal(root.get("twoFactorEnabled"), true));
  }

  /**
   * Creates specification for users who completed onboarding.
   */
  public static Specification<User> withCompletedOnboarding() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and(
        criteriaBuilder.isNull(root.get("deletedAt")),
        criteriaBuilder.equal(root.get("onboardingCompleted"), true));
  }

  /**
   * Creates specification for users with recent activity.
   */
  public static Specification<User> withRecentActivity(java.time.LocalDateTime since) {
    return (root, query, criteriaBuilder) -> {
      if (since == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.greaterThanOrEqualTo(root.get("lastActivityAt"), since));
    };
  }

  /**
   * Creates specification for users by role.
   */
  public static Specification<User> byRole(String role) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(role)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get("role")),
              role.toLowerCase().trim()));
    };
  }

  /**
   * Creates specification for users by location.
   */
  public static Specification<User> byLocation(String location) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(location)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get("location")),
              location.toLowerCase().trim()));
    };
  }

  /**
   * Creates specification for users by job title.
   */
  public static Specification<User> byJobTitle(String jobTitle) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(jobTitle)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get("jobTitle")),
              jobTitle.toLowerCase().trim()));
    };
  }

  /**
   * Creates specification for users with specific skills.
   */
  public static Specification<User> withSkills(List<String> skills) {
    return (root, query, criteriaBuilder) -> {
      if (skills == null || skills.isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      List<Predicate> skillPredicates = new ArrayList<>();
      for (String skill : skills) {
        if (StringUtils.hasText(skill)) {
          skillPredicates.add(
              criteriaBuilder.like(
                  criteriaBuilder.lower(root.get("skills")),
                  "%" + skill.toLowerCase().trim() + "%"));
        }
      }

      if (skillPredicates.isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.or(skillPredicates.toArray(new Predicate[0])));
    };
  }

  /**
   * Creates specification for users by hire date range.
   */
  public static Specification<User> hiredBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

      if (startDate != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
            root.get("hireDate"), startDate));
      }

      if (endDate != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(
            root.get("hireDate"), endDate));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * Creates specification for users by salary range.
   */
  public static Specification<User> bySalaryRange(java.math.BigDecimal minSalary, java.math.BigDecimal maxSalary) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

      if (minSalary != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
            root.get("salary"), minSalary));
      }

      if (maxSalary != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(
            root.get("salary"), maxSalary));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * Creates specification for users by work schedule.
   */
  public static Specification<User> byWorkSchedule(String workSchedule) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(workSchedule)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get("workSchedule")),
              workSchedule.toLowerCase().trim()));
    };
  }

  /**
   * Creates specification for users by contract type.
   */
  public static Specification<User> byContractType(String contractType) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(contractType)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(
          criteriaBuilder.isNull(root.get("deletedAt")),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get("contractType")),
              contractType.toLowerCase().trim()));
    };
  }
}