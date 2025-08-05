package com.programmingmukesh.auth.service.auth_service.entity;

/**
 * Multi-Factor Authentication methods supported by the system
 * 
 * @author Programming Mukesh
 * @version 1.0
 */
public enum MfaMethod {
  /**
   * Time-based One-Time Password (TOTP)
   * Uses apps like Google Authenticator, Authy, etc.
   */
  TOTP,

  /**
   * SMS-based One-Time Password
   * Sends codes via SMS to user's phone
   */
  SMS,

  /**
   * Email-based One-Time Password
   * Sends codes via email to user's email address
   */
  EMAIL,

  /**
   * Hardware Security Key (FIDO2/U2F)
   * Uses physical security keys like YubiKey
   */
  HARDWARE_KEY,

  /**
   * Push Notification
   * Sends push notifications to mobile app
   */
  PUSH_NOTIFICATION
}