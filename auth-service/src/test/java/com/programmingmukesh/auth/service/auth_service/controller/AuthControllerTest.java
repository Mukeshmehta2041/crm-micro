package com.programmingmukesh.auth.service.auth_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmingmukesh.auth.service.auth_service.dto.request.CreateUserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AuthController.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @Test
  void testRegisterEndpoint() throws Exception {
    // Setup MockMvc
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // Create test request
    CreateUserRequest request = CreateUserRequest.builder()
        .username("testuser")
        .email("test@example.com")
        .password("TestPass123!")
        .firstName("Test")
        .lastName("User")
        .displayName("Test User")
        .build();

    // Perform test
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }
} 