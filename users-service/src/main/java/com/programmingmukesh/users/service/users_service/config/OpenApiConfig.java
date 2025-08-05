package com.programmingmukesh.users.service.users_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.examples.Example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 configuration for Users Service API documentation.
 * 
 * <p>
 * This configuration provides comprehensive API documentation including:
 * </p>
 * <ul>
 * <li>API metadata and contact information</li>
 * <li>Server configurations for different environments</li>
 * <li>Security schemes and requirements</li>
 * <li>Common response schemas and examples</li>
 * <li>API tags and grouping</li>
 * </ul>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.name:Users Service}")
    private String appName;

    @Value("${app.description:Comprehensive user management service for CRM microservices}")
    private String appDescription;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configures the OpenAPI specification for the Users Service.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI usersServiceOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers())
                .tags(createTags())
                .components(createComponents())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    /**
     * Creates API information metadata.
     * 
     * @return API info
     */
    private Info createApiInfo() {
        return new Info()
                .title(appName + " API")
                .description(appDescription + "\n\n" +
                        "## Features\n" +
                        "- **User Management**: Complete CRUD operations for user profiles\n" +
                        "- **Advanced Search**: Search users by various criteria\n" +
                        "- **Pagination**: Efficient data retrieval with pagination support\n" +
                        "- **Status Management**: User activation/deactivation capabilities\n" +
                        "- **Data Validation**: Comprehensive input validation\n" +
                        "- **Error Handling**: Standardized error responses\n" +
                        "- **Security**: JWT-based authentication and authorization\n\n" +
                        "## Authentication\n" +
                        "This API uses JWT Bearer token authentication. Include the token in the Authorization header:\n" +
                        "```\n" +
                        "Authorization: Bearer <your-jwt-token>\n" +
                        "```\n\n" +
                        "## Rate Limiting\n" +
                        "API requests are rate-limited to ensure fair usage and system stability.\n\n" +
                        "## Response Format\n" +
                        "All API responses follow a consistent format with success/error indicators, data, and metadata.")
                .version(appVersion)
                .contact(new Contact()
                        .name("Programming Mukesh")
                        .email("[email]")
                        .url("https://programmingmukesh.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * Creates server configurations for different environments.
     * 
     * @return list of servers
     */
    private List<Server> createServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local Development Server"),
                new Server()
                        .url("https://api-dev.example.com")
                        .description("Development Environment"),
                new Server()
                        .url("https://api-staging.example.com")
                        .description("Staging Environment"),
                new Server()
                        .url("https://api.example.com")
                        .description("Production Environment")
        );
    }

    /**
     * Creates API tags for endpoint grouping.
     * 
     * @return list of tags
     */
    private List<Tag> createTags() {
        return List.of(
                new Tag()
                        .name("User Management")
                        .description("Operations for managing user profiles and accounts"),
                new Tag()
                        .name("User Search")
                        .description("Search and filter operations for finding users"),
                new Tag()
                        .name("User Status")
                        .description("Operations for managing user account status"),
                new Tag()
                        .name("System")
                        .description("System health and utility endpoints")
        );
    }

    /**
     * Creates common components including security schemes and response schemas.
     * 
     * @return components configuration
     */
    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", createBearerAuthScheme())
                .addSchemas("ApiResponse", createApiResponseSchema())
                .addSchemas("ApiError", createApiErrorSchema())
                .addSchemas("ValidationError", createValidationErrorSchema())
                .addResponses("BadRequest", createBadRequestResponse())
                .addResponses("Unauthorized", createUnauthorizedResponse())
                .addResponses("Forbidden", createForbiddenResponse())
                .addResponses("NotFound", createNotFoundResponse())
                .addResponses("Conflict", createConflictResponse())
                .addResponses("InternalServerError", createInternalServerErrorResponse())
                .addExamples("UserCreateExample", createUserCreateExample())
                .addExamples("UserResponseExample", createUserResponseExample())
                .addExamples("ErrorExample", createErrorExample());
    }

    /**
     * Creates JWT Bearer authentication scheme.
     * 
     * @return security scheme
     */
    private SecurityScheme createBearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer token authentication. Format: Bearer <token>");
    }

    /**
     * Creates API response schema.
     * 
     * @return schema
     */
    private Schema<?> createApiResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Standard API response wrapper")
                .addProperty("success", new Schema<>().type("boolean").description("Indicates if the request was successful"))
                .addProperty("data", new Schema<>().description("The response data"))
                .addProperty("message", new Schema<>().type("string").description("Success or informational message"))
                .addProperty("error", new Schema<>().type("string").description("Error message (if any)"))
                .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("Response timestamp"));
    }

    /**
     * Creates API error schema.
     * 
     * @return schema
     */
    private Schema<?> createApiErrorSchema() {
        return new Schema<>()
                .type("object")
                .description("Error response details")
                .addProperty("code", new Schema<>().type("string").description("Error code"))
                .addProperty("message", new Schema<>().type("string").description("Error message"))
                .addProperty("details", new Schema<>().type("array").items(new Schema<>().type("string")).description("Error details"));
    }

    /**
     * Creates validation error schema.
     * 
     * @return schema
     */
    private Schema<?> createValidationErrorSchema() {
        return new Schema<>()
                .type("object")
                .description("Validation error details")
                .addProperty("field", new Schema<>().type("string").description("Field name"))
                .addProperty("message", new Schema<>().type("string").description("Validation error message"))
                .addProperty("rejectedValue", new Schema<>().description("The rejected value"));
    }

    // Common response definitions
    private ApiResponse createBadRequestResponse() {
        return new ApiResponse()
                .description("Bad Request - Invalid input parameters")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponse"))));
    }

    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
                .description("Unauthorized - Authentication required")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponse"))));
    }

    private ApiResponse createForbiddenResponse() {
        return new ApiResponse()
                .description("Forbidden - Insufficient permissions")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponse"))));
    }

    private ApiResponse createNotFoundResponse() {
        return new ApiResponse()
                .description("Not Found - Resource not found")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponse"))));
    }

    private ApiResponse createConflictResponse() {
        return new ApiResponse()
                .description("Conflict - Resource already exists")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponse"))));
    }

    private ApiResponse createInternalServerErrorResponse() {
        return new ApiResponse()
                .description("Internal Server Error")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponse"))));
    }

    // Example definitions
    private Example createUserCreateExample() {
        return new Example()
                .summary("Create User Request")
                .description("Example request for creating a new user")
                .value("""
                        {
                          "username": "john.doe",
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "phoneNumber": "+1-555-123-4567",
                          "jobTitle": "Software Engineer",
                          "department": "Engineering",
                          "company": "Tech Corp"
                        }
                        """);
    }

    private Example createUserResponseExample() {
        return new Example()
                .summary("User Response")
                .description("Example user response")
                .value("""
                        {
                          "success": true,
                          "data": {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "username": "john.doe",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@example.com",
                            "phoneNumber": "+1-555-123-4567",
                            "jobTitle": "Software Engineer",
                            "department": "Engineering",
                            "company": "Tech Corp",
                            "status": "ACTIVE",
                            "createdAt": "2024-01-15T10:30:00",
                            "updatedAt": "2024-01-15T10:30:00"
                          },
                          "message": "User retrieved successfully",
                          "timestamp": "2024-01-15T10:30:00"
                        }
                        """);
    }

    private Example createErrorExample() {
        return new Example()
                .summary("Error Response")
                .description("Example error response")
                .value("""
                        {
                          "success": false,
                          "error": "User not found",
                          "message": "User with ID 123e4567-e89b-12d3-a456-426614174000 does not exist",
                          "timestamp": "2024-01-15T10:30:00"
                        }
                        """);
    }
}