package com.programmingmukesh.users.service.users_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson configuration for clean JSON responses without type information.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
public class JacksonConfig {

    /**
     * Primary ObjectMapper for HTTP responses.
     * This will be used for all REST API responses and will NOT include @class information.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        
        // Register Java Time module for LocalDateTime, LocalDate, etc.
        mapper.registerModule(new JavaTimeModule());
        
        // Disable writing dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Don't fail on unknown properties during deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Don't include null values in JSON
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        
        // Don't include empty collections
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        
        // CRITICAL: Explicitly deactivate default typing to prevent @class in JSON
        mapper.deactivateDefaultTyping();
        
        // Ensure no type information is included
        mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
        
        return mapper;
    }
}