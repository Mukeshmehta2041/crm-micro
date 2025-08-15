package com.programmingmukesh.tenant.service.tenant_service.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Service Discovery configuration for the Tenant Service.
 * 
 * <p>
 * This configuration enables:
 * </p>
 * <ul>
 * <li>Eureka client for service registration and discovery</li>
 * <li>Feign clients for inter-service communication</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
@EnableFeignClients(basePackages = "com.programmingmukesh.tenant.service.tenant_service.client")
public class ServiceDiscoveryConfig {
  // Configuration for service discovery and Feign clients
  // Eureka client is auto-configured by Spring Boot
}
