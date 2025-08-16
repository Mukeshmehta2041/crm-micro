package com.programmingmukesh.users.service.users_service.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 * Resilience4j configuration for fault tolerance.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
public class ResilienceConfig {

    /**
     * Circuit breaker configuration for database operations.
     */
    @Bean
    public CircuitBreaker databaseCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .slowCallRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .build();

        return CircuitBreaker.of("database", config);
    }

    /**
     * Circuit breaker configuration for cache operations.
     */
    @Bean
    public CircuitBreaker cacheCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(60)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(5)
                .minimumNumberOfCalls(3)
                .build();

        return CircuitBreaker.of("cache", config);
    }

    /**
     * Retry configuration for transient failures.
     */
    @Bean
    public Retry defaultRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(Exception.class)
                .build();

        return Retry.of("default", config);
    }

    /**
     * Time limiter configuration for preventing long-running operations.
     */
    @Bean
    public TimeLimiter defaultTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        return TimeLimiter.of("default", config);
    }
}