package com.programmingmukesh.tenant.service.tenant_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for the Tenant Service.
 * 
 * <p>
 * This configuration class sets up Swagger/OpenAPI documentation
 * for the tenant service API endpoints.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
public class OpenApiConfig {

  @Value("${app.name:Tenant Service}")
  private String appName;

  @Value("${app.version:1.0.0}")
  private String appVersion;

  @Value("${app.description:Tenant Management Service for CRM System}")
  private String appDescription;

  /**
   * Configures OpenAPI documentation.
   * 
   * @return OpenAPI configuration
   */
  @Bean
  public OpenAPI tenantServiceOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title(appName)
            .version(appVersion)
            .description(appDescription)
            .contact(new Contact()
                .name("Programming Mukesh")
                .email("contact@programmingmukesh.com")
                .url("https://programmingmukesh.com"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(List.of(
            new Server()
                .url("http://localhost:8083")
                .description("Development Server"),
            new Server()
                .url("https://api.mycrm.com")
                .description("Production Server")));
  }
}
