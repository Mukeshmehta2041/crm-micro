package com.programmingmukesh.auth.service.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for the Auth Service.
 * 
 * <p>
 * This configuration provides:
 * </p>
 * <ul>
 * <li>Password encoder for secure password hashing</li>
 * <li>Security filter chain configuration</li>
 * <li>CSRF and session management settings</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Creates and configures a BCrypt password encoder.
   * 
   * @return configured BCryptPasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the security filter chain.
   * 
   * @param http the HttpSecurity object
   * @return configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/swagger-ui.html").permitAll()
            .requestMatchers("/api-docs/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .anyRequest().authenticated())
        .sessionManagement(session -> session
            .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

    return http.build();
  }
}