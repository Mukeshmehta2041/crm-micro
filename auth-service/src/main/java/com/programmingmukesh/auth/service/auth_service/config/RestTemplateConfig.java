package com.programmingmukesh.auth.service.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for RestTemplate and other HTTP client configurations.
 * 
 * <p>
 * This configuration provides:
 * </p>
 * <ul>
 * <li>RestTemplate bean for HTTP calls to other services</li>
 * <li>HTTP client configurations</li>
 * <li>Timeout and connection settings</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
public class RestTemplateConfig {

  /**
   * Creates and configures a RestTemplate bean for HTTP operations.
   * 
   * @return configured RestTemplate instance
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
} 