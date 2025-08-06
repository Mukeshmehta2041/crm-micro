# Configuration Management

This document explains how to configure the microservices in this CRM system. Each service now uses a single `application.yml` file with environment variable support for different deployment scenarios.

## Overview

All services have been consolidated to use a single `application.yml` configuration file instead of multiple profile-specific files. Configuration is managed through environment variables with sensible defaults.

## Services Configuration

### 1. Service Registry (Port: 8761)
- **Location**: `service-registry/src/main/resources/application.yml`
- **Purpose**: Eureka service discovery server
- **Configuration**: Static configuration, no environment variables needed

### 2. API Gateway (Port: 8080)
- **Location**: `api-gateway/src/main/resources/application.yml`
- **Purpose**: Routes requests to microservices, handles authentication
- **Configuration**: Static configuration with JWT settings

### 3. Auth Service (Port: 8081)
- **Location**: `auth-service/src/main/resources/application.yml`
- **Environment File**: `auth-service/.env.example`
- **Purpose**: Handles authentication and authorization

### 4. Users Service (Port: 8082)
- **Location**: `users-service/src/main/resources/application.yml`
- **Environment File**: `users-service/.env.example`
- **Purpose**: Manages user data and operations

## Environment Configuration

### Setting Up Environment Variables

1. **For Local Development**:
   ```bash
   # Copy example files
   cp auth-service/.env.example auth-service/.env
   cp users-service/.env.example users-service/.env
   
   # Edit the .env files with your local settings
   ```

2. **For Docker Deployment**:
   ```bash
   # Copy docker environment file
   cp docker/.env.example docker/.env
   
   # Edit docker/.env with your docker settings
   ```

### Key Environment Variables

#### Database Configuration
```bash
DATABASE_URL=jdbc:postgresql://postgres:5432/db_crm
DATABASE_USERNAME=user_crm
DATABASE_PASSWORD=password_crm
DATABASE_DRIVER=org.postgresql.Driver
```

#### JPA Configuration
```bash
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
JPA_DDL_AUTO=validate  # Options: validate, update, create, create-drop
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false
```

#### Logging Configuration
```bash
LOG_LEVEL_APP=INFO     # DEBUG, INFO, WARN, ERROR
LOG_LEVEL_SQL=WARN
LOG_LEVEL_HIKARI=INFO
```

#### Service Discovery
```bash
EUREKA_URL=http://localhost:8761/eureka/
```

## Environment-Specific Configurations

### Development Environment
For local development with H2 database:
```bash
DATABASE_URL=jdbc:h2:mem:testdb
DATABASE_USERNAME=sa
DATABASE_PASSWORD=
DATABASE_DRIVER=org.h2.Driver
JPA_DIALECT=org.hibernate.dialect.H2Dialect
JPA_DDL_AUTO=create-drop
JPA_SHOW_SQL=true
JPA_FORMAT_SQL=true
LOG_LEVEL_APP=DEBUG
LOG_LEVEL_SQL=DEBUG
```

### Production Environment
For production deployment:
```bash
DATABASE_URL=jdbc:postgresql://prod-db:5432/crm_prod
DATABASE_USERNAME=prod_user
DATABASE_PASSWORD=secure_password
JPA_DDL_AUTO=validate
JPA_SHOW_SQL=false
LOG_LEVEL_APP=INFO
LOG_LEVEL_SQL=WARN
MANAGEMENT_ENDPOINTS=health,info,metrics,prometheus
MANAGEMENT_HEALTH_DETAILS=never
```

### Docker Environment
For Docker Compose deployment:
```bash
DATABASE_URL=jdbc:postgresql://postgres:5432/db_crm
EUREKA_URL=http://service-registry:8761/eureka/
APP_ENVIRONMENT=docker
```

## Configuration Precedence

Spring Boot follows this configuration precedence (highest to lowest):
1. Command line arguments
2. Environment variables
3. System properties
4. `application.yml` default values

## Running Services

### Local Development
```bash
# Set environment variables or use .env files
export DATABASE_URL=jdbc:h2:mem:testdb
export JPA_DDL_AUTO=create-drop

# Run services
./mvnw spring-boot:run
```

### Docker Deployment
```bash
# Use docker-compose with environment file
cd docker
docker-compose --env-file .env up
```

### Production Deployment
```bash
# Set production environment variables
export DATABASE_URL=jdbc:postgresql://prod-db:5432/crm_prod
export JPA_DDL_AUTO=validate
export LOG_LEVEL_APP=INFO

# Run with production profile
java -jar target/service-name.jar
```

## Monitoring and Health Checks

All services expose actuator endpoints:
- Health: `http://localhost:port/actuator/health`
- Info: `http://localhost:port/actuator/info`
- Metrics: `http://localhost:port/actuator/metrics`

## Security Considerations

1. **Never commit `.env` files** - they are in `.gitignore`
2. **Use strong passwords** in production
3. **Limit management endpoints** in production
4. **Use HTTPS** in production
5. **Rotate JWT secrets** regularly

## Troubleshooting

### Common Issues

1. **Database Connection Issues**:
   - Check `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
   - Ensure database is running and accessible

2. **Service Discovery Issues**:
   - Check `EUREKA_URL` configuration
   - Ensure service-registry is running first

3. **Port Conflicts**:
   - Check `SERVER_PORT` environment variables
   - Ensure ports are not already in use

### Debugging

Enable debug logging:
```bash
LOG_LEVEL_APP=DEBUG
LOG_LEVEL_SQL=DEBUG
JPA_SHOW_SQL=true
JPA_FORMAT_SQL=true
```

## Migration from Profile-Based Configuration

The previous profile-based configuration files (`application-dev.yml`, `application-prod.yml`, etc.) have been removed and consolidated into single `application.yml` files with environment variable support.

### Benefits of New Approach
- **Simplified deployment**: One configuration file per service
- **Environment flexibility**: Easy to configure for different environments
- **Container-friendly**: Works well with Docker and Kubernetes
- **Security**: Sensitive data in environment variables, not in code
- **Maintainability**: Less configuration duplication