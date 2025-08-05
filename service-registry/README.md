# Service Registry (Eureka Server)

This is the service registry for the CRM microservices system using Netflix Eureka Server.

## Overview

The Service Registry acts as a central hub where all microservices register themselves and discover other services. This enables:

- **Service Discovery**: Services can find each other by name instead of hardcoded URLs
- **Load Balancing**: Automatic load balancing between multiple instances
- **Health Monitoring**: Automatic detection of unhealthy service instances
- **Dynamic Scaling**: Services can be added/removed without configuration changes

## Features

- **Eureka Server**: Netflix Eureka service registry
- **Health Checks**: Built-in health monitoring for registered services
- **Web Dashboard**: Visual interface to monitor registered services
- **Self-Preservation**: Protects against network partitions

## Configuration

### Application Properties
- **Port**: 8761 (default Eureka port)
- **Self Registration**: Disabled (registry doesn't register with itself)
- **Self Preservation**: Disabled for development

### Environment Variables
- `EUREKA_INSTANCE_HOSTNAME`: Hostname for the Eureka server (default: localhost)
- `SERVER_PORT`: Port for the Eureka server (default: 8761)

## Running the Service

### Local Development
```bash
# Build the service
./mvnw clean package

# Run the service
java -jar target/service-registry-*.jar
```

### Docker
```bash
# Build Docker image
docker build -t crm-service-registry .

# Run container
docker run -p 8761:8761 crm-service-registry
```

### Docker Compose
```bash
# Start with all services
docker-compose up service-registry
```

## Accessing the Dashboard

Once running, access the Eureka dashboard at:
- **URL**: http://localhost:8761
- **Dashboard**: Shows all registered services, their status, and metadata

## Service Registration

Services register with the registry by:

1. Adding Eureka Client dependency
2. Adding `@EnableEurekaClient` annotation
3. Configuring `eureka.client.service-url.defaultZone`

Example configuration for client services:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
```

## Health Checks

The service registry provides health endpoints:
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`

## Registered Services

The following services register with this registry:
- **auth-service**: Authentication and authorization service
- **users-service**: User management service

## Troubleshooting

### Common Issues

1. **Services not registering**
   - Check network connectivity to registry
   - Verify `defaultZone` URL is correct
   - Check service logs for registration errors

2. **Services showing as DOWN**
   - Verify service health endpoints are accessible
   - Check if services are actually running
   - Review lease renewal settings

3. **Registry not starting**
   - Check port 8761 is not in use
   - Verify Java version compatibility
   - Check application logs for errors

### Logs
Service logs are available at:
- Console output during startup
- Application logs in `/logs` directory (if configured)

## Development Notes

- Self-preservation is disabled for development to quickly remove unhealthy instances
- Lease renewal interval is set to 30 seconds for faster detection
- Registry UI is available for monitoring during development