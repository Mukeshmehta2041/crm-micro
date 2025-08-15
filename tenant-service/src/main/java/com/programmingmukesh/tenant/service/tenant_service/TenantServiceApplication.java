package com.programmingmukesh.tenant.service.tenant_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Tenant Service.
 * 
 * <p>
 * This is the entry point for the Tenant Service microservice.
 * It handles tenant management operations including:
 * </p>
 * <ul>
 * <li>Tenant registration and onboarding</li>
 * <li>Tenant configuration management</li>
 * <li>Subscription and plan management</li>
 * <li>Multi-tenancy support</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class TenantServiceApplication {

  /**
   * Main method to start the Tenant Service application.
   * 
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(TenantServiceApplication.class, args);
  }
}
