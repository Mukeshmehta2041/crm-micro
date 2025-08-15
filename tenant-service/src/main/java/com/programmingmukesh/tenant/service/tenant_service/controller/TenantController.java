package com.programmingmukesh.tenant.service.tenant_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.programmingmukesh.tenant.service.tenant_service.dto.ApiResponse;
import com.programmingmukesh.tenant.service.tenant_service.dto.ApiResponseMeta;
import com.programmingmukesh.tenant.service.tenant_service.dto.PaginationMeta;
import com.programmingmukesh.tenant.service.tenant_service.dto.request.CreateTenantRequest;
import com.programmingmukesh.tenant.service.tenant_service.dto.response.TenantResponse;
import com.programmingmukesh.tenant.service.tenant_service.entity.PlanType;
import com.programmingmukesh.tenant.service.tenant_service.entity.TenantStatus;
import com.programmingmukesh.tenant.service.tenant_service.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for tenant management operations.
 * 
 * <p>
 * This controller provides endpoints for:
 * </p>
 * <ul>
 * <li>Tenant creation and registration</li>
 * <li>Tenant retrieval and searching</li>
 * <li>Tenant updates and management</li>
 * <li>Plan and subscription management</li>
 * <li>Domain availability checking</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenant Management", description = "APIs for managing tenant organizations")
@Slf4j
public class TenantController {

  private final TenantService tenantService;

  @Autowired
  public TenantController(TenantService tenantService) {
    this.tenantService = tenantService;
  }

  /**
   * Creates a new tenant.
   * 
   * @param request the create tenant request
   * @return the created tenant response
   */
  @PostMapping
  @Operation(summary = "Create a new tenant", description = "Creates a new tenant organization")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tenant created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Tenant already exists")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
      @Valid @RequestBody CreateTenantRequest request) {
    log.info("Creating tenant with subdomain: {}", request.getSubdomain());

    TenantResponse tenant = tenantService.createTenant(request);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant, "Tenant created successfully");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Retrieves a tenant by ID.
   * 
   * @param tenantId the tenant ID
   * @return the tenant response
   */
  @GetMapping("/{tenantId}")
  @Operation(summary = "Get tenant by ID", description = "Retrieves a tenant by its unique identifier")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> getTenantById(
      @Parameter(description = "Tenant ID") @PathVariable UUID tenantId) {
    log.info("Retrieving tenant with ID: {}", tenantId);

    TenantResponse tenant = tenantService.getTenantById(tenantId);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves a tenant by subdomain.
   * 
   * @param subdomain the subdomain
   * @return the tenant response
   */
  @GetMapping("/subdomain/{subdomain}")
  @Operation(summary = "Get tenant by subdomain", description = "Retrieves a tenant by its subdomain")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> getTenantBySubdomain(
      @Parameter(description = "Tenant subdomain") @PathVariable String subdomain) {
    log.info("Retrieving tenant with subdomain: {}", subdomain);

    TenantResponse tenant = tenantService.getTenantBySubdomain(subdomain);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves a tenant by domain (subdomain or custom domain).
   * 
   * @param domain the domain
   * @return the tenant response
   */
  @GetMapping("/domain/{domain}")
  @Operation(summary = "Get tenant by domain", description = "Retrieves a tenant by its domain (subdomain or custom domain)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> getTenantByDomain(
      @Parameter(description = "Tenant domain") @PathVariable String domain) {
    log.info("Retrieving tenant with domain: {}", domain);

    TenantResponse tenant = tenantService.getTenantByDomain(domain);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves all tenants with pagination.
   * 
   * @param pageable the pagination parameters
   * @return the page of tenant responses
   */
  @GetMapping
  @Operation(summary = "Get all tenants", description = "Retrieves all tenants with pagination")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenants retrieved successfully")
  })
  public ResponseEntity<ApiResponse<List<TenantResponse>>> getAllTenants(Pageable pageable) {
    log.info("Retrieving all tenants with pagination: {}", pageable);

    Page<TenantResponse> page = tenantService.getAllTenants(pageable);
    PaginationMeta paginationMeta = PaginationMeta.fromPage(page);
    ApiResponseMeta meta = ApiResponseMeta.withPagination(paginationMeta);
    ApiResponse<List<TenantResponse>> response = ApiResponse.success(
        page.getContent(), "Tenants retrieved successfully", meta);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves tenants by status.
   * 
   * @param status   the tenant status
   * @param pageable the pagination parameters
   * @return the page of tenant responses
   */
  @GetMapping("/status/{status}")
  @Operation(summary = "Get tenants by status", description = "Retrieves tenants filtered by status")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenants retrieved successfully")
  })
  public ResponseEntity<ApiResponse<List<TenantResponse>>> getTenantsByStatus(
      @Parameter(description = "Tenant status") @PathVariable TenantStatus status,
      Pageable pageable) {
    log.info("Retrieving tenants with status: {}", status);

    Page<TenantResponse> page = tenantService.getTenantsByStatus(status, pageable);
    PaginationMeta paginationMeta = PaginationMeta.fromPage(page);
    ApiResponseMeta meta = ApiResponseMeta.withPagination(paginationMeta);
    ApiResponse<List<TenantResponse>> response = ApiResponse.success(
        page.getContent(), "Tenants retrieved successfully", meta);

    return ResponseEntity.ok(response);
  }

  /**
   * Searches tenants by name.
   * 
   * @param name     the name to search for
   * @param pageable the pagination parameters
   * @return the page of tenant responses
   */
  @GetMapping("/search")
  @Operation(summary = "Search tenants by name", description = "Searches tenants by name (case-insensitive)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully")
  })
  public ResponseEntity<ApiResponse<List<TenantResponse>>> searchTenantsByName(
      @Parameter(description = "Name to search for") @RequestParam String name,
      Pageable pageable) {
    log.info("Searching tenants with name containing: {}", name);

    Page<TenantResponse> page = tenantService.searchTenantsByName(name, pageable);
    PaginationMeta paginationMeta = PaginationMeta.fromPage(page);
    ApiResponseMeta meta = ApiResponseMeta.withPagination(paginationMeta);
    ApiResponse<List<TenantResponse>> response = ApiResponse.success(
        page.getContent(), "Search completed successfully", meta);

    return ResponseEntity.ok(response);
  }

  /**
   * Updates a tenant.
   * 
   * @param tenantId the tenant ID
   * @param request  the update request
   * @return the updated tenant response
   */
  @PutMapping("/{tenantId}")
  @Operation(summary = "Update tenant", description = "Updates an existing tenant")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
      @Parameter(description = "Tenant ID") @PathVariable UUID tenantId,
      @Valid @RequestBody CreateTenantRequest request) {
    log.info("Updating tenant with ID: {}", tenantId);

    TenantResponse tenant = tenantService.updateTenant(tenantId, request);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant, "Tenant updated successfully");

    return ResponseEntity.ok(response);
  }

  /**
   * Updates tenant status.
   * 
   * @param tenantId the tenant ID
   * @param status   the new status
   * @return the updated tenant response
   */
  @PatchMapping("/{tenantId}/status")
  @Operation(summary = "Update tenant status", description = "Updates the status of a tenant")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> updateTenantStatus(
      @Parameter(description = "Tenant ID") @PathVariable UUID tenantId,
      @Parameter(description = "New status") @RequestParam TenantStatus status) {
    log.info("Updating tenant {} status to: {}", tenantId, status);

    TenantResponse tenant = tenantService.updateTenantStatus(tenantId, status);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant, "Status updated successfully");

    return ResponseEntity.ok(response);
  }

  /**
   * Converts tenant from trial to paid subscription.
   * 
   * @param tenantId the tenant ID
   * @param planType the new plan type
   * @return the updated tenant response
   */
  @PostMapping("/{tenantId}/convert-from-trial")
  @Operation(summary = "Convert from trial", description = "Converts a tenant from trial to paid subscription")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Converted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Tenant is not in trial mode")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> convertFromTrial(
      @Parameter(description = "Tenant ID") @PathVariable UUID tenantId,
      @Parameter(description = "New plan type") @RequestParam PlanType planType) {
    log.info("Converting tenant {} from trial to plan: {}", tenantId, planType);

    TenantResponse tenant = tenantService.convertFromTrial(tenantId, planType);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant, "Converted from trial successfully");

    return ResponseEntity.ok(response);
  }

  /**
   * Suspends a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the updated tenant response
   */
  @PostMapping("/{tenantId}/suspend")
  @Operation(summary = "Suspend tenant", description = "Suspends a tenant account")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant suspended successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> suspendTenant(
      @Parameter(description = "Tenant ID") @PathVariable UUID tenantId) {
    log.info("Suspending tenant: {}", tenantId);

    TenantResponse tenant = tenantService.suspendTenant(tenantId);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant, "Tenant suspended successfully");

    return ResponseEntity.ok(response);
  }

  /**
   * Activates a tenant.
   * 
   * @param tenantId the tenant ID
   * @return the updated tenant response
   */
  @PostMapping("/{tenantId}/activate")
  @Operation(summary = "Activate tenant", description = "Activates a tenant account")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant activated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found")
  })
  public ResponseEntity<ApiResponse<TenantResponse>> activateTenant(
      @Parameter(description = "Tenant ID") @PathVariable UUID tenantId) {
    log.info("Activating tenant: {}", tenantId);

    TenantResponse tenant = tenantService.activateTenant(tenantId);
    ApiResponse<TenantResponse> response = ApiResponse.success(tenant, "Tenant activated successfully");

    return ResponseEntity.ok(response);
  }

  /**
   * Deletes a tenant (soft delete).
   * 
   * @param tenantId the tenant ID
   * @return success response
   */
  @DeleteMapping("/{tenantId}")
  @Operation(summary = "Delete tenant", description = "Soft deletes a tenant account")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found")
  })
  public ResponseEntity<ApiResponse<Object>> deleteTenant(
      @Parameter(description = "Tenant ID") @PathVariable UUID tenantId) {
    log.info("Deleting tenant: {}", tenantId);

    tenantService.deleteTenant(tenantId);
    ApiResponse<Object> response = ApiResponse.noContent("Tenant deleted successfully");

    return ResponseEntity.ok(response);
  }

  /**
   * Checks if a subdomain is available.
   * 
   * @param subdomain the subdomain to check
   * @return availability response
   */
  @GetMapping("/check-subdomain/{subdomain}")
  @Operation(summary = "Check subdomain availability", description = "Checks if a subdomain is available for registration")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed successfully")
  })
  public ResponseEntity<ApiResponse<Boolean>> checkSubdomainAvailability(
      @Parameter(description = "Subdomain to check") @PathVariable String subdomain) {
    log.info("Checking subdomain availability: {}", subdomain);

    boolean available = tenantService.isSubdomainAvailable(subdomain);
    String message = available ? "Subdomain is available" : "Subdomain is not available";
    ApiResponse<Boolean> response = ApiResponse.success(available, message);

    return ResponseEntity.ok(response);
  }

  /**
   * Gets tenant statistics.
   * 
   * @return tenant statistics
   */
  @GetMapping("/statistics")
  @Operation(summary = "Get tenant statistics", description = "Retrieves tenant statistics and metrics")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
  })
  public ResponseEntity<ApiResponse<TenantService.TenantStatistics>> getTenantStatistics() {
    log.info("Retrieving tenant statistics");

    TenantService.TenantStatistics statistics = tenantService.getTenantStatistics();
    ApiResponse<TenantService.TenantStatistics> response = ApiResponse.success(statistics,
        "Statistics retrieved successfully");

    return ResponseEntity.ok(response);
  }
}
