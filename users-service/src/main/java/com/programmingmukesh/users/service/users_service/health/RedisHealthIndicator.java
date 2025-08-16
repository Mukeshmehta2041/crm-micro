package com.programmingmukesh.users.service.users_service.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom health indicator for Redis connectivity.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthIndicator implements HealthIndicator {

  private final RedisConnectionFactory redisConnectionFactory;

  @Override
  public Health health() {
    try {
      RedisConnection connection = redisConnectionFactory.getConnection();

      if (connection != null) {
        // Test the connection with a ping
        String pong = connection.ping();
        connection.close();

        if ("PONG".equals(pong)) {
          return Health.up()
              .withDetail("redis", "Available")
              .withDetail("ping", pong)
              .build();
        }
      }

      return Health.down()
          .withDetail("redis", "Connection failed")
          .build();

    } catch (Exception e) {
      log.error("Redis health check failed: {}", e.getMessage());
      return Health.down()
          .withDetail("redis", "Connection failed")
          .withDetail("error", e.getMessage())
          .build();
    }
  }
}