package com.programmingmukesh.auth.service.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;

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

  /**
   * Configures Feign request timeout.
   * 
   * @return Request.Options with appropriate timeouts
   */
  @Bean
  public Request.Options requestOptions() {
    return new Request.Options(
        10000, // connectTimeoutMillis - 10 seconds
        30000 // readTimeoutMillis - 30 seconds
    );
  }

  /**
   * Configures Feign retry logic.
   * 
   * @return Retryer with appropriate retry configuration
   */
  @Bean
  public Retryer retryer() {
    return new Retryer.Default(
        1000, // period - wait 1 second before first retry
        5000, // maxPeriod - wait max 5 seconds between retries
        3 // maxAttempts - retry up to 3 times
    );
  }
}