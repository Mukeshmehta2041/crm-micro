package com.programmingmukesh.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Auth Service is currently unavailable");
        response.put("message", "Please try again later");
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("timestamp", Instant.now().toString());
        response.put("service", "auth-service");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/users")
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> usersServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Users Service is currently unavailable");
        response.put("message", "Please try again later");
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("timestamp", Instant.now().toString());
        response.put("service", "users-service");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/generic")
    public ResponseEntity<Map<String, Object>> genericFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service is currently unavailable");
        response.put("message", "Please try again later");
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}