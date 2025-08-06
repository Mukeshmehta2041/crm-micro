package com.programmingmukesh.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class ResponseTransformationFilter extends AbstractGatewayFilterFactory<ResponseTransformationFilter.Config> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseTransformationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            // Join all data buffers
                            DataBuffer joinedBuffer = bufferFactory.join(dataBuffers);
                            byte[] content = new byte[joinedBuffer.readableByteCount()];
                            joinedBuffer.read(content);
                            DataBufferUtils.release(joinedBuffer);

                            String responseBody = new String(content, StandardCharsets.UTF_8);
                            String transformedBody = transformResponse(responseBody, exchange);

                            return bufferFactory.wrap(transformedBody.getBytes(StandardCharsets.UTF_8));
                        }));
                    }
                    return super.writeWith(body);
                }
            };

            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        };
    }

    private String transformResponse(String originalBody, ServerWebExchange exchange) {
        try {
            // Add standard response metadata
            JsonNode jsonNode = objectMapper.readTree(originalBody);
            
            if (jsonNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                
                // Add metadata
                ObjectNode metadata = objectMapper.createObjectNode();
                metadata.put("timestamp", Instant.now().toString());
                metadata.put("path", exchange.getRequest().getURI().getPath());
                metadata.put("method", exchange.getRequest().getMethod().name());
                metadata.put("status", exchange.getResponse().getStatusCode().value());
                
                // Add request ID if present
                String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");
                if (requestId != null) {
                    metadata.put("requestId", requestId);
                }
                
                objectNode.set("_metadata", metadata);
                
                // Transform specific response structures
                transformUserResponse(objectNode);
                transformErrorResponse(objectNode, exchange);
                
                return objectMapper.writeValueAsString(objectNode);
            }
            
            return originalBody;
        } catch (Exception e) {
            // If transformation fails, return original body
            return originalBody;
        }
    }

    private void transformUserResponse(ObjectNode response) {
        // Transform user data - remove sensitive information
        if (response.has("password")) {
            response.remove("password");
        }
        
        if (response.has("data") && response.get("data").isArray()) {
            response.get("data").forEach(user -> {
                if (user.isObject()) {
                    ((ObjectNode) user).remove("password");
                }
            });
        }
        
        // Add computed fields
        if (response.has("firstName") && response.has("lastName")) {
            String fullName = response.get("firstName").asText() + " " + response.get("lastName").asText();
            response.put("fullName", fullName);
        }
    }

    private void transformErrorResponse(ObjectNode response, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.resolve(exchange.getResponse().getStatusCode().value());
        
        if (status != null && status.isError()) {
            // Standardize error response format
            if (!response.has("error")) {
                ObjectNode error = objectMapper.createObjectNode();
                error.put("code", status.value());
                error.put("message", status.getReasonPhrase());
                error.put("timestamp", Instant.now().toString());
                error.put("path", exchange.getRequest().getURI().getPath());
                
                response.set("error", error);
            }
            
            // Add correlation ID for error tracking
            String correlationId = java.util.UUID.randomUUID().toString();
            response.put("correlationId", correlationId);
            
            // Add to response headers for logging
            exchange.getResponse().getHeaders().add("X-Correlation-ID", correlationId);
        }
    }

    public static class Config {
        private boolean addMetadata = true;
        private boolean removePasswords = true;
        private boolean standardizeErrors = true;

        public boolean isAddMetadata() {
            return addMetadata;
        }

        public void setAddMetadata(boolean addMetadata) {
            this.addMetadata = addMetadata;
        }

        public boolean isRemovePasswords() {
            return removePasswords;
        }

        public void setRemovePasswords(boolean removePasswords) {
            this.removePasswords = removePasswords;
        }

        public boolean isStandardizeErrors() {
            return standardizeErrors;
        }

        public void setStandardizeErrors(boolean standardizeErrors) {
            this.standardizeErrors = standardizeErrors;
        }
    }
}