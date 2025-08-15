package com.programmingmukesh.auth.service.auth_service.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.programmingmukesh.auth.service.auth_service.config.FeignConfig;
import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateTenantRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.TenantResponse;

/**
 * Feign client for communicating with the Tenant Service.
 * 
 * <p>
 * This client provides methods for:
 * </p>
 * <ul>
 * <li>Creating tenants during registration</li>
 * <li>Retrieving tenant information</li>
 * <li>Validating tenant status</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@FeignClient(name = "tenant-service", fallback = TenantServiceClientFallback.class, configuration = FeignConfig.class)
public interface TenantServiceClient {

  /**
   * Creates a new tenant.
   * 
   * @param request the create tenant request
   * @return the created tenant response
   */
  @PostMapping("/api/v1/tenants")
  ApiResponse<TenantResponse> createTenant(@RequestBody CreateTenantRequest request);

  /**
   * Retrieves a tenant by ID.
   * 
   * @param tenantId the tenant ID
   * @return the tenant response
   */
  @GetMapping("/api/v1/tenants/{tenantId}")
  ApiResponse<TenantResponse> getTenantById(@PathVariable("tenantId") UUID tenantId);

  /**
   * Retrieves a tenant by subdomain.
   * 
   * @param subdomain the subdomain
   * @return the tenant response
   */
  @GetMapping("/api/v1/tenants/subdomain/{subdomain}")
  ApiResponse<TenantResponse> getTenantBySubdomain(@PathVariable("subdomain") String subdomain);

  /**
   * Checks if a subdomain is available.
   * 
   * @param subdomain the subdomain to check
   * @return availability response
   */
  @GetMapping("/api/v1/tenants/check-subdomain/{subdomain}")
  ApiResponse<Boolean> checkSubdomainAvailability(@PathVariable("subdomain") String subdomain);
}
