package com.programmingmukesh.auth.service.auth_service.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.programmingmukesh.auth.service.auth_service.dto.ApiError;
import com.programmingmukesh.auth.service.auth_service.dto.ApiResponse;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateTenantRequest;
import com.programmingmukesh.auth.service.auth_service.dto.response.TenantResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Fallback implementation for TenantServiceClient.
 * 
 * <p>
 * This fallback is triggered when the Tenant Service is unavailable
 * or when circuit breaker is open.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Component
@Slf4j
public class TenantServiceClientFallback implements TenantServiceClient {

  @Override
  public ApiResponse<TenantResponse> createTenant(CreateTenantRequest request) {
    log.error("Tenant Service is unavailable - falling back for createTenant");
    return ApiResponse.error("Tenant Service is unavailable",
        "Tenant Service is currently unavailable. Please try again later.");
  }

  @Override
  public ApiResponse<TenantResponse> getTenantById(UUID tenantId) {
    log.error("Tenant Service is unavailable - falling back for getTenantById: {}", tenantId);
    return ApiResponse.error("Tenant Service is unavailable",
        "Tenant Service is currently unavailable. Please try again later.");
  }

  @Override
  public ApiResponse<TenantResponse> getTenantBySubdomain(String subdomain) {
    log.error("Tenant Service is unavailable - falling back for getTenantBySubdomain: {}", subdomain);
    return ApiResponse.error("Tenant Service is unavailable",
        "Tenant Service is currently unavailable. Please try again later.");
  }

  @Override
  public ApiResponse<Boolean> checkSubdomainAvailability(String subdomain) {
    log.error("Tenant Service is unavailable - falling back for checkSubdomainAvailability: {}", subdomain);
    return ApiResponse.error("Tenant Service is unavailable",
        "Tenant Service is currently unavailable. Please try again later.");
  }
}
