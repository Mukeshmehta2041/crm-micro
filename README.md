# CRM Microservices System

A comprehensive Customer Relationship Management (CRM) system built with Spring Boot microservices architecture, featuring service discovery, authentication, and user management.

## 🏗️ Architecture Overview

### Microservices
- **Service Registry** - Netflix Eureka server for service discovery
- **Auth Service** - Authentication and authorization service
- **Users Service** - User management and profile service

### Infrastructure
- **PostgreSQL** - Primary database for data persistence
- **Redis** - Caching and session storage
- **Docker** - Containerization and orchestration

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL (for local development)

### Development Environment (Docker)
```bash
# Start all services with Docker
./scripts/docker-dev-up.sh

# Check status
./scripts/docker-dev-status.sh

# View logs
./scripts/docker-dev-logs.sh

# Stop services
./scripts/docker-dev-down.sh
```

### Local Development
```bash
# Build all services
./scripts/build-all.sh

# Start services locally
./scripts/start-services.sh

# Stop services
./scripts/stop-services.sh
```

## 📋 Service Details

### Service Registry (Port 8761)
Netflix Eureka server providing service discovery and registration.

**Features:**
- Service registration and discovery
- Health monitoring
- Load balancing support
- Web dashboard

**URLs:**
- Dashboard: http://localhost:8761
- Health: http://localhost:8761/actuator/health

### Auth Service (Port 8080/8081)
Handles authentication, authorization, and security.

**Features:**
- User registration and login
- JWT token management
- Password encryption
- Rate limiting
- Security validation

**Key Endpoints:**
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Token refresh
- `GET /api/v1/auth/validate` - Token validation

### Users Service (Port 8082)
Manages user profiles and user-related operations.

**Features:**
- User profile management
- CRUD operations
- Data validation
- OpenAPI documentation
- Comprehensive error handling

**Key Endpoints:**
- `GET /api/v1/users` - List users
- `GET /api/v1/users/{id}` - Get user by ID
- `POST /api/v1/users` - Create user
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

**API Documentation:**
- Swagger UI: http://localhost:8082/swagger-ui.html
- OpenAPI JSON: http://localhost:8082/api-docs

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.5.4** - Application framework
- **Spring Cloud 2024.0.0** - Microservices framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Security framework
- **Netflix Eureka** - Service discovery
- **PostgreSQL** - Primary database
- **Redis** - Caching layer
- **Maven** - Build tool

### DevOps & Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Actuator** - Health checks and monitoring
- **Lombok** - Code generation
- **OpenAPI 3** - API documentation

## 📁 Project Structure

```
crm-microsverices/
├── service-registry/          # Eureka server
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
├── auth-service/              # Authentication service
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
├── users-service/             # User management service
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
├── docker/                    # Docker configurations
│   ├── docker-compose.yml
│   ├── docker-compose.dev.yaml
│   ├── init-scripts/
│   └── README.md
├── scripts/                   # Build and deployment scripts
│   ├── build-all.sh
│   ├── start-services.sh
│   ├── stop-services.sh
│   ├── docker-dev-up.sh
│   ├── docker-dev-down.sh
│   ├── docker-dev-logs.sh
│   └── docker-dev-status.sh
└── README.md
```

## 🔧 Configuration

### Environment Profiles
Each service supports multiple profiles:
- `local` - Local development
- `dev` - Development environment
- `test` - Testing environment
- `prod` - Production environment

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_crm
    username: user_crm
    password: password_crm
```

### Service Discovery Configuration
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 🐳 Docker Deployment

### Development Environment
```bash
# Start development environment
./scripts/docker-dev-up.sh

# Services will be available at:
# - Service Registry: http://localhost:8761
# - Auth Service: http://localhost:8081
# - Users Service: http://localhost:8082
# - PostgreSQL: localhost:5432
# - Redis: localhost:6379
```

### Production Environment
```bash
# Start production environment
docker-compose -f docker/docker-compose.yml up -d
```

## 🧪 Testing

### API Testing
```bash
# Test user registration
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User"
  }'

# Test user login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "SecurePass123!"
  }'

# Test users service
curl http://localhost:8082/api/v1/users
```

### Health Checks
```bash
# Check all services
curl http://localhost:8761/actuator/health  # Service Registry
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # Users Service
```

## 📊 Monitoring & Observability

### Health Endpoints
All services expose Actuator endpoints:
- `/actuator/health` - Health status
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics
- `/actuator/env` - Environment properties

### Service Discovery Dashboard
Monitor registered services at: http://localhost:8761

### Logging
- Structured logging with JSON format
- Centralized log aggregation ready
- Different log levels per environment

## 🔒 Security

### Authentication Flow
1. User registers via Auth Service
2. User logs in and receives JWT token
3. Token is used for subsequent API calls
4. Services validate tokens via Auth Service

### Security Features
- Password encryption with BCrypt
- JWT token-based authentication
- Rate limiting on authentication endpoints
- Input validation and sanitization
- CORS configuration
- Security headers

## 🚀 Deployment

### Local Development
1. Clone the repository
2. Run `./scripts/build-all.sh`
3. Run `./scripts/start-services.sh`
4. Access services via localhost

### Docker Development
1. Clone the repository
2. Run `./scripts/docker-dev-up.sh`
3. Access services via localhost

### Production
1. Build Docker images
2. Configure environment variables
3. Deploy using docker-compose or Kubernetes
4. Set up monitoring and logging

## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Make changes and test locally
4. Run tests and ensure all services work
5. Submit a pull request

### Code Standards
- Follow Spring Boot best practices
- Use proper error handling
- Include comprehensive logging
- Write unit and integration tests
- Document API endpoints

## 📚 API Documentation

### OpenAPI/Swagger
- Users Service: http://localhost:8082/swagger-ui.html
- API Specification: http://localhost:8082/api-docs

### Postman Collection
Import the API endpoints into Postman for testing:
- Base URLs configured for local development
- Authentication examples included
- Error response examples

## 🐛 Troubleshooting

### Common Issues

#### Port Conflicts
```bash
# Check what's using the port
lsof -i :8080

# Kill process using port
kill -9 <PID>
```

#### Database Connection
```bash
# Check PostgreSQL status
docker-compose -f docker/docker-compose.dev.yaml logs postgres

# Connect to database
docker exec -it crm-postgres-dev psql -U user_crm -d db_crm
```

#### Service Discovery
```bash
# Check Eureka dashboard
open http://localhost:8761

# Verify service registration
curl http://localhost:8761/eureka/apps
```

### Logs
```bash
# View all service logs
./scripts/docker-dev-logs.sh

# View specific service logs
./scripts/docker-dev-logs.sh auth-service

# Follow logs in real-time
./scripts/docker-dev-logs.sh -f users-service
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

- **Development Team** - Microservices architecture and implementation
- **DevOps Team** - Docker containerization and deployment
- **QA Team** - Testing and quality assurance

## 🔗 Related Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [Docker Documentation](https://docs.docker.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)