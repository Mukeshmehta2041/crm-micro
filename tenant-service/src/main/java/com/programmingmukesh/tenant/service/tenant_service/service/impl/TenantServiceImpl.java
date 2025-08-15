package com.programmingmukesh.tenant.service.tenant_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmingmukesh.tenant.service.tenant_service.dto.request.CreateTenantRequest;
import com.programmingmukesh.tenant.service.tenant_service.dto.response.TenantResponse;
import com.programmingmukesh.tenant.service.tenant_service.entity.Tenant;
import com.programmingmukesh.tenant.service.tenant_service.entity.PlanType;
import com.programmingmukesh.tenant.service.tenant_service.entity.TenantStatus;
import com.programmingmukesh.tenant.service.tenant_service.exception.TenantAlreadyExistsException;
import com.programmingmukesh.tenant.service.tenant_service.exception.TenantNotFoundException;
import com.programmingmukesh.tenant.service.tenant_service.exception.TenantValidationException;
import com.programmingmukesh.tenant.service.tenant_service.mapper.TenantMapper;
import com.programmingmukesh.tenant.service.tenant_service.repository.TenantRepository;
import com.programmingmukesh.tenant.service.tenant_service.service.TenantService;

/**
 * Implementation of TenantService interface.
 * 
 * <p>
 * This service provides business logic for tenant management operations
 * including:
 * </p>
 * <ul>
 * <li>Tenant creation and validation</li>
 * <li>Tenant retrieval and searching</li>
 * <li>Tenant updates and status management</li>
 * <li>Plan and subscription management</li>
 * <li>Trial and expiration handling</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Service
@Transactional
public class TenantServiceImpl implements TenantService {

  private final TenantRepository tenantRepository;
  private final TenantMapper tenantMapper;

  @Autowired
  public TenantServiceImpl(TenantRepository tenantRepository, TenantMapper tenantMapper) {
    this.tenantRepository = tenantRepository;
    this.tenantMapper = tenantMapper;
  }

  @Override
  public TenantResponse createTenant(CreateTenantRequest request) {
    validateCreateTenantRequest(request);

    // Check if subdomain is already taken
    if (tenantRepository.findBySubdomain(request.getSubdomain()).isPresent()) {
      throw new TenantAlreadyExistsException("Subdomain '" + request.getSubdomain() + "' is already taken");
    }

    // Check if custom domain is already taken (if provided)
    if (request.getCustomDomain() != null &&
        tenantRepository.findByCustomDomain(request.getCustomDomain()).isPresent()) {
      throw new TenantAlreadyExistsException("Custom domain '" + request.getCustomDomain() + "' is already taken");
    }

    // Set trial defaults if needed
    if (Boolean.TRUE.equals(request.getIsTrial())) {
      request.setTrialDefaults();
    }

    Tenant tenant = tenantMapper.toEntity(request);
    tenant = tenantRepository.save(tenant);

    return tenantMapper.toResponse(tenant);
  }

  @Override
  @Transactional(readOnly = true)
  public TenantResponse getTenantById(UUID tenantId) {
    Tenant tenant = findTenantById(tenantId);
    return tenantMapper.toResponse(tenant);
  }

  @Override
  @Transactional(readOnly = true)
  public TenantResponse getTenantBySubdomain(String subdomain) {
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
        .orElseThrow(() -> new TenantNotFoundException("Tenant not found with subdomain: " + subdomain));
    return tenantMapper.toResponse(tenant);
  }

  @Override
  @Transactional(readOnly = true)
  public TenantResponse getTenantByCustomDomain(String customDomain) {
    Tenant tenant = tenantRepository.findByCustomDomain(customDomain)
        .orElseThrow(() -> new TenantNotFoundException("Tenant not found with custom domain: " + customDomain));
    return tenantMapper.toResponse(tenant);
  }

  @Override
  @Transactional(readOnly = true)
  public TenantResponse getTenantByDomain(String domain) {
    Tenant tenant = tenantRepository.findBySubdomainOrCustomDomain(domain, domain)
        .orElseThrow(() -> new TenantNotFoundException("Tenant not found with domain: " + domain));
    return tenantMapper.toResponse(tenant);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TenantResponse> getAllTenants(Pageable pageable) {
    Page<Tenant> tenants = tenantRepository.findAll(pageable);
    return tenants.map(tenantMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TenantResponse> getTenantsByStatus(TenantStatus status, Pageable pageable) {
    Page<Tenant> tenants = tenantRepository.findByStatus(status, pageable);
    return tenants.map(tenantMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TenantResponse> getTenantsByPlanType(PlanType planType, Pageable pageable) {
    Page<Tenant> tenants = tenantRepository.findByPlanType(planType, pageable);
    return tenants.map(tenantMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TenantResponse> searchTenantsByName(String name, Pageable pageable) {
    Page<Tenant> tenants = tenantRepository.findByNameContainingIgnoreCase(name, pageable);
    return tenants.map(tenantMapper::toResponse);
  }

  @Override
  public TenantResponse updateTenant(UUID tenantId, CreateTenantRequest request) {
    Tenant tenant = findTenantById(tenantId);

    validateUpdateTenantRequest(tenant, request);

    tenant = tenantMapper.updateEntity(tenant, request);
    tenant = tenantRepository.save(tenant);

    return tenantMapper.toResponse(tenant);
  }

  @Override
  public TenantResponse updateTenantStatus(UUID tenantId, TenantStatus status) {
    Tenant tenant = findTenantById(tenantId);
    tenant.setStatus(status);
    tenant = tenantRepository.save(tenant);

    return tenantMapper.toResponse(tenant);
  }

  @Override
  public TenantResponse updateTenantPlan(UUID tenantId, PlanType planType, Integer maxUsers, Integer maxStorageGb) {
    Tenant tenant = findTenantById(tenantId);
    tenant.updatePlan(planType, maxUsers, maxStorageGb);
    tenant = tenantRepository.save(tenant);

    return tenantMapper.toResponse(tenant);
  }

  @Override
  public TenantResponse convertFromTrial(UUID tenantId, PlanType planType) {
    Tenant tenant = findTenantById(tenantId);

    if (!tenant.getIsTrial()) {
      throw new TenantValidationException("Tenant is not in trial mode");
    }

    LocalDateTime subscriptionExpiry = LocalDateTime.now().plusMonths(1); // Default 1 month subscription
    tenant.convertFromTrial(planType, subscriptionExpiry);

    // Update limits based on plan type
    switch (planType) {
      case BASIC:
        tenant.setMaxUsers(10);
        tenant.setMaxStorageGb(100);
        break;
      case STANDARD:
        tenant.setMaxUsers(50);
        tenant.setMaxStorageGb(500);
        break;
      case PREMIUM:
        tenant.setMaxUsers(200);
        tenant.setMaxStorageGb(1000);
        break;
      case ENTERPRISE:
        tenant.setMaxUsers(1000);
        tenant.setMaxStorageGb(5000);
        break;
      default:
        // Keep existing limits
        break;
    }

    tenant = tenantRepository.save(tenant);

    return tenantMapper.toResponse(tenant);
  }

  @Override
  public TenantResponse suspendTenant(UUID tenantId) {
    Tenant tenant = findTenantById(tenantId);
    tenant.suspend();
    tenant = tenantRepository.save(tenant);

    return tenantMapper.toResponse(tenant);
  }

  @Override
  public TenantResponse activateTenant(UUID tenantId) {
    Tenant tenant = findTenantById(tenantId);
    tenant.activate();
    tenant = tenantRepository.save(tenant);

    return tenantMapper.toResponse(tenant);
  }

  @Override
  public void deleteTenant(UUID tenantId) {
    Tenant tenant = findTenantById(tenantId);
    tenant.setStatus(TenantStatus.DELETED);
    tenantRepository.save(tenant);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isSubdomainAvailable(String subdomain) {
    return tenantRepository.isSubdomainAvailable(subdomain);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isCustomDomainAvailable(String customDomain) {
    return tenantRepository.isCustomDomainAvailable(customDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TenantResponse> getTenantsWithExpiredTrials() {
    List<Tenant> tenants = tenantRepository.findTenantsWithExpiredTrials(LocalDateTime.now());
    return tenants.stream()
        .map(tenantMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TenantResponse> getTenantsWithExpiringTrials() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime sevenDaysFromNow = now.plusDays(7);
    List<Tenant> tenants = tenantRepository.findTenantsWithExpiringTrials(now, sevenDaysFromNow);
    return tenants.stream()
        .map(tenantMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TenantResponse> getTenantsWithExpiredSubscriptions() {
    List<Tenant> tenants = tenantRepository.findTenantsWithExpiredSubscriptions(LocalDateTime.now());
    return tenants.stream()
        .map(tenantMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TenantResponse> getTenantsWithExpiringSubscriptions() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime thirtyDaysFromNow = now.plusDays(30);
    List<Tenant> tenants = tenantRepository.findTenantsWithExpiringSubscriptions(now, thirtyDaysFromNow);
    return tenants.stream()
        .map(tenantMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public TenantStatistics getTenantStatistics() {
    long totalTenants = tenantRepository.count();
    long activeTenants = tenantRepository.countByStatus(TenantStatus.ACTIVE);
    long trialTenants = tenantRepository.countTrialTenants();
    long suspendedTenants = tenantRepository.countByStatus(TenantStatus.SUSPENDED);
    long basicPlanTenants = tenantRepository.countByPlanType(PlanType.BASIC);
    long standardPlanTenants = tenantRepository.countByPlanType(PlanType.STANDARD);
    long premiumPlanTenants = tenantRepository.countByPlanType(PlanType.PREMIUM);
    long enterprisePlanTenants = tenantRepository.countByPlanType(PlanType.ENTERPRISE);

    return new TenantStatistics(totalTenants, activeTenants, trialTenants, suspendedTenants,
        basicPlanTenants, standardPlanTenants, premiumPlanTenants, enterprisePlanTenants);
  }

  /**
   * Finds a tenant by ID or throws TenantNotFoundException.
   * 
   * @param tenantId the tenant ID
   * @return the tenant entity
   * @throws TenantNotFoundException if tenant not found
   */
  private Tenant findTenantById(UUID tenantId) {
    return tenantRepository.findById(tenantId)
        .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + tenantId));
  }

  /**
   * Validates create tenant request.
   * 
   * @param request the create tenant request
   * @throws TenantValidationException if validation fails
   */
  private void validateCreateTenantRequest(CreateTenantRequest request) {
    if (request == null) {
      throw new TenantValidationException("Create tenant request cannot be null");
    }

    if (!request.hasMinimumRequiredInfo()) {
      throw new TenantValidationException("Tenant name and subdomain are required");
    }

    if (!request.isSubdomainValid()) {
      throw new TenantValidationException("Invalid subdomain format");
    }
  }

  /**
   * Validates update tenant request.
   * 
   * @param tenant  the existing tenant
   * @param request the update request
   * @throws TenantValidationException if validation fails
   */
  private void validateUpdateTenantRequest(Tenant tenant, CreateTenantRequest request) {
    if (request == null) {
      throw new TenantValidationException("Update tenant request cannot be null");
    }

    // Check if subdomain is changing and if new subdomain is available
    if (request.getSubdomain() != null &&
        !request.getSubdomain().equals(tenant.getSubdomain()) &&
        tenantRepository.findBySubdomain(request.getSubdomain()).isPresent()) {
      throw new TenantAlreadyExistsException("Subdomain '" + request.getSubdomain() + "' is already taken");
    }

    // Check if custom domain is changing and if new custom domain is available
    if (request.getCustomDomain() != null &&
        !request.getCustomDomain().equals(tenant.getCustomDomain()) &&
        tenantRepository.findByCustomDomain(request.getCustomDomain()).isPresent()) {
      throw new TenantAlreadyExistsException("Custom domain '" + request.getCustomDomain() + "' is already taken");
    }
  }
}
