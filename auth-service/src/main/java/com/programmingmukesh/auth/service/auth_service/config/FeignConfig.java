package com.programmingmukesh.auth.service.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

/**
 * Feign client configuration for better error handling and timeouts.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
public class FeignConfig {

  /**
   * Configures Feign logging level.
   * 
   * @return Logger.Level.BASIC for production logging
   */
  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.BASIC;
  }
}