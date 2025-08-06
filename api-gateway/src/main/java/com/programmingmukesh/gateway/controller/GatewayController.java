package com.programmingmukesh.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    @Autowired
    private RouteLocator routeLocator;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("service", "api-gateway");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/routes")
    public Mono<ResponseEntity<Map<String, Object>>> getRoutes() {
        return routeLocator.getRoutes()
            .map(route -> {
                Map<String, Object> routeInfo = new HashMap<>();
                routeInfo.put("id", route.getId());
                routeInfo.put("uri", route.getUri().toString());
                routeInfo.put("predicates", route.getPredicate().toString());
                routeInfo.put("filters", route.getFilters().stream()
                    .map(filter -> filter.getClass().getSimpleName())
                    .collect(Collectors.toList()));
                return routeInfo;
            })
            .collectList()
            .map(routes -> {
                Map<String, Object> response = new HashMap<>();
                response.put("routes", routes);
                response.put("timestamp", Instant.now().toString());
                response.put("totalRoutes", routes.size());
                return ResponseEntity.ok(response);
            });
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "CRM API Gateway");
        response.put("description", "API Gateway with Circuit Breaker, Rate Limiting, Load Balancing, and Caching");
        response.put("version", "1.0.0");
        response.put("timestamp", Instant.now().toString());
        
        Map<String, Object> features = new HashMap<>();
        features.put("circuitBreaker", "Resilience4j");
        features.put("rateLimiting", "Redis-based");
        features.put("loadBalancing", "Spring Cloud LoadBalancer");
        features.put("caching", "Redis");
        features.put("authentication", "JWT-based");
        features.put("serviceDiscovery", "Eureka");
        
        response.put("features", features);
        
        return ResponseEntity.ok(response);
    }
}