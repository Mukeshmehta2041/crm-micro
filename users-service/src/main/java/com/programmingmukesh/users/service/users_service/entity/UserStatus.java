package com.programmingmukesh.users.service.users_service.entity;

/**
 * User account status enumeration
 * 
 * @author Programming Mukesh
 * @version 1.0
 */
public enum UserStatus {
  /**
   * Active user account
   */
  ACTIVE,

  /**
   * Inactive user account
   */
  INACTIVE,

  /**
   * Suspended user account
   */
  SUSPENDED,

  /**
   * Pending verification
   */
  PENDING,

  /**
   * Deleted user account
   */
  DELETED
}