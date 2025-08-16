package com.programmingmukesh.users.service.users_service.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis Cache configuration for the User Service.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig implements CachingConfigurer {

  @Value("${spring.data.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.data.redis.port:6379}")
  private int redisPort;

  @Value("${spring.data.redis.timeout:2000}")
  private Duration redisTimeout;

  @Value("${app.cache.default-ttl:3600}")
  private long defaultTtlSeconds;

  @Value("${app.cache.user-ttl:1800}")
  private long userTtlSeconds;

  /**
   * Redis connection factory configuration.
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    LettuceConnectionFactory factory = new LettuceConnectionFactory(redisHost, redisPort);
    factory.setValidateConnection(true);
    return factory;
  }

  /**
   * Redis template configuration with proper serialization.
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Configure serializers - use separate serializer for cache
    StringRedisSerializer stringSerializer = new StringRedisSerializer();
    
    // Create a dedicated Jackson serializer for cache that won't affect HTTP responses
    Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    jsonSerializer.setObjectMapper(cacheObjectMapper());

    template.setKeySerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.setDefaultSerializer(jsonSerializer);
    template.afterPropertiesSet();

    return template;
  }

  /**
   * Object mapper for Redis cache serialization only.
   * Uses type information for proper deserialization from cache.
   */
  @Bean("cacheObjectMapper")
  public ObjectMapper cacheObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // Only use type information for cache serialization
    mapper.activateDefaultTyping(
        mapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);
    return mapper;
  }

  /**
   * Creates a dedicated JSON serializer for cache operations.
   */
  private Jackson2JsonRedisSerializer<Object> createCacheJsonSerializer() {
    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
    serializer.setObjectMapper(cacheObjectMapper());
    return serializer;
  }

  /**
   * Redis cache manager with custom configurations.
   */
  @Bean
  @Override
  public CacheManager cacheManager() {
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(defaultTtlSeconds))
        .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
            .fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
            .fromSerializer(createCacheJsonSerializer()))
        .disableCachingNullValues();

    // Custom cache configurations
    RedisCacheConfiguration userConfig = defaultConfig
        .entryTtl(Duration.ofSeconds(userTtlSeconds));

    return RedisCacheManager.builder(redisConnectionFactory())
        .cacheDefaults(defaultConfig)
        .withCacheConfiguration("users", userConfig)
        .withCacheConfiguration("user-search", defaultConfig.entryTtl(Duration.ofMinutes(5)))
        .withCacheConfiguration("user-stats", defaultConfig.entryTtl(Duration.ofMinutes(15)))
        .transactionAware()
        .build();
  }

  /**
   * Custom cache error handler to prevent cache failures from breaking the
   * application.
   */
  @Bean
  @Override
  public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
        log.warn("Cache GET error for cache '{}' and key '{}': {}",
            cache.getName(), key, exception.getMessage());
        // Don't throw exception, let the method execute normally
      }

      @Override
      public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key,
          Object value) {
        log.warn("Cache PUT error for cache '{}' and key '{}': {}",
            cache.getName(), key, exception.getMessage());
        // Don't throw exception, let the method execute normally
      }

      @Override
      public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
        log.warn("Cache EVICT error for cache '{}' and key '{}': {}",
            cache.getName(), key, exception.getMessage());
        // Don't throw exception, let the method execute normally
      }

      @Override
      public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
        log.warn("Cache CLEAR error for cache '{}': {}",
            cache.getName(), exception.getMessage());
        // Don't throw exception, let the method execute normally
      }
    };
  }
}