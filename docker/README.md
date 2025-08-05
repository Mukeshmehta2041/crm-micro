# CRM Microservices Docker Setup

This directory contains Docker configurations for running the CRM microservices system in different environments.

## 📁 Files Overview

- `docker-compose.yml` - Production/staging environment configuration
- `docker-compose.dev.yaml` - Development environment configuration
- `init-scripts/` - Database initialization scripts

## 🚀 Quick Start (Development)

### Prerequisites
- Docker and Docker Compose installed
- At least 4GB RAM available for containers
- Ports 5432, 6379, 8080, 8081, 8082, 8761 available

### Start Development Environment
```bash
# From project root
./scripts/docker-dev-up.sh
```

### Stop Development Environment
```bash
# Stop services only
./scripts/docker-dev-down.sh

# Stop and remove volumes (WARNING: deletes all data)
./scripts/docker-dev-down.sh -v

# Stop and clean up Docker resources
./scripts/docker-dev-down.sh -c
```

### View Logs
```bash
# All services
./scripts/docker-dev-logs.sh

# Specific service
./scripts/docker-dev-logs.sh auth-service

# Follow logs
./scripts/docker-dev-logs.sh -f users-service
```

### Check Status
```bash
./scripts/docker-dev-status.sh
```

## 🏗️ Architecture

### Services

#### Service Registry (Port 8761)
- **Purpose**: Netflix Eureka server for service discovery
- **URL**: http://localhost:8761
- **Health**: http://localhost:8761/actuator/health

#### Auth Service (Port 8081)
- **Purpose**: Authentication and authorization
- **URL**: http://localhost:8081
- **Health**: http://localhost:8081/actuator/health

#### Users Service (Port 8082)
- **Purpose**: User management
- **URL**: http://localhost:8082
- **Health**: http://localhost:8082/actuator/health
- **API Docs**: http://localhost:8082/swagger-ui.html

#### PostgreSQL (Port 5432)
- **Purpose**: Primary database
- **Connection**: localhost:5432
- **Database**: db_crm
- **Username**: user_crm
- **Password**: password_crm

#### Redis (Port 6379)
- **Purpose**: Caching and session storage
- **Connection**: localhost:6379

### Network
- **Name**: crm-network
- **Type**: Bridge network
- **Subnet**: 172.20.0.0/16 (dev environment)

## 🔧 Configuration

### Environment Variables

#### Development Environment
Services in development use the following key environment variables:

**Common:**
- `SPRING_PROFILES_ACTIVE=dev`
- `JAVA_OPTS=-Xms256m -Xmx512m -XX:+UseG1GC`

**Database:**
- `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/db_crm`
- `SPRING_DATASOURCE_USERNAME=user_crm`
- `SPRING_DATASOURCE_PASSWORD=password_crm`

**Service Discovery:**
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka/`

### Volumes

#### Development Environment
- `postgres_data` - PostgreSQL data persistence
- `registry_logs` - Service registry logs
- `auth_logs` - Auth service logs  
- `users_logs` - Users service logs

## 🐛 Troubleshooting

### Common Issues

#### Services Not Starting
```bash
# Check service status
./scripts/docker-dev-status.sh

# View logs for specific service
./scripts/docker-dev-logs.sh service-name

# Restart specific service
docker-compose -f docker/docker-compose.dev.yaml restart service-name
```

#### Port Conflicts
If ports are already in use, you can modify the port mappings in the compose files:
```yaml
ports:
  - "NEW_PORT:CONTAINER_PORT"
```

#### Memory Issues
If containers are killed due to memory constraints:
```bash
# Check resource usage
./scripts/docker-dev-status.sh

# Reduce memory allocation in compose file
environment:
  - JAVA_OPTS=-Xms128m -Xmx256m
```

#### Database Connection Issues
```bash
# Check PostgreSQL logs
./scripts/docker-dev-logs.sh postgres

# Connect to database directly
docker exec -it crm-postgres-dev psql -U user_crm -d db_crm
```

#### Service Discovery Issues
```bash
# Check Eureka dashboard
open http://localhost:8761

# Verify service registration
curl http://localhost:8761/eureka/apps
```

### Health Checks

All services include health checks that can be monitored:

```bash
# Service Registry
curl http://localhost:8761/actuator/health

# Auth Service  
curl http://localhost:8081/actuator/health

# Users Service
curl http://localhost:8082/actuator/health
```

### Log Locations

Logs are available through Docker commands:
```bash
# View logs
docker-compose -f docker/docker-compose.dev.yaml logs service-name

# Follow logs
docker-compose -f docker/docker-compose.dev.yaml logs -f service-name

# View last N lines
docker-compose -f docker/docker-compose.dev.yaml logs --tail=100 service-name
```

## 🔄 Development Workflow

### Typical Development Flow

1. **Start Environment**
   ```bash
   ./scripts/docker-dev-up.sh
   ```

2. **Verify Services**
   ```bash
   ./scripts/docker-dev-status.sh
   ```

3. **Make Code Changes**
   - Edit source code in your IDE
   - Services will need to be rebuilt for changes

4. **Rebuild and Restart Service**
   ```bash
   # Rebuild specific service
   docker-compose -f docker/docker-compose.dev.yaml build auth-service
   
   # Restart service
   docker-compose -f docker/docker-compose.dev.yaml restart auth-service
   ```

5. **View Logs**
   ```bash
   ./scripts/docker-dev-logs.sh -f auth-service
   ```

6. **Stop Environment**
   ```bash
   ./scripts/docker-dev-down.sh
   ```

### Database Management

#### Reset Database
```bash
# Stop services and remove volumes
./scripts/docker-dev-down.sh -v

# Start fresh
./scripts/docker-dev-up.sh
```

#### Access Database
```bash
# Connect to PostgreSQL
docker exec -it crm-postgres-dev psql -U user_crm -d db_crm

# Run SQL commands
\dt  # List tables
\q   # Quit
```

#### Backup Database
```bash
# Create backup
docker exec crm-postgres-dev pg_dump -U user_crm db_crm > backup.sql

# Restore backup
docker exec -i crm-postgres-dev psql -U user_crm -d db_crm < backup.sql
```

## 📊 Monitoring

### Service Metrics
All services expose Actuator endpoints for monitoring:

- `/actuator/health` - Health status
- `/actuator/info` - Application information  
- `/actuator/metrics` - Application metrics
- `/actuator/env` - Environment properties

### Resource Monitoring
```bash
# View resource usage
./scripts/docker-dev-status.sh

# Docker stats
docker stats

# Container inspection
docker inspect container-name
```

## 🔒 Security Notes

### Development Environment
- Default passwords are used (change for production)
- All services are accessible without authentication
- Debug logging is enabled

### Production Considerations
- Use environment-specific secrets
- Enable authentication and authorization
- Configure proper logging levels
- Set up monitoring and alerting
- Use HTTPS/TLS encryption

## 📚 Additional Resources

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Netflix Eureka Documentation](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [Redis Docker Hub](https://hub.docker.com/_/redis)