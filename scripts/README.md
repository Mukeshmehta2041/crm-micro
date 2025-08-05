# CRM Microservices Build Scripts

This directory contains shell scripts for building JAR files and Docker images for the CRM microservices project.

## Available Scripts

### 1. `build-all.sh`
Builds JAR files for all services in the project.

**Usage:**
```bash
./build-all.sh
```

**Features:**
- Builds all services (auth-service, users-service)
- Uses Maven wrapper if available, falls back to system Maven
- Colored output for better readability
- Error handling and build status reporting
- Skips tests for faster builds

### 2. `build-individual.sh`
Builds JAR files for individual services.

**Usage:**
```bash
./build-individual.sh <service-name>
```

**Available Services:**
- `auth-service`
- `users-service`

**Examples:**
```bash
./build-individual.sh auth-service
./build-individual.sh users-service
```

**Features:**
- Builds a single specified service
- Validates service name
- Shows JAR file size after successful build
- Uses Maven wrapper if available, falls back to system Maven
- Colored output for better readability

### 3. `docker-build.sh`
Builds JAR files and Docker images for all services.

**Usage:**
```bash
./docker-build.sh [OPTIONS]
```

**Options:**
- `--build-jars` - Build JAR files first (default)
- `--skip-jars` - Skip JAR building, use existing JARs
- `--push` - Push images to registry after building
- `--help` - Show help message

**Examples:**
```bash
./docker-build.sh                    # Build JARs and Docker images
./docker-build.sh --skip-jars        # Build Docker images only
./docker-build.sh --push             # Build and push images
```

**Features:**
- Builds JAR files and Docker images in one command
- Uses pre-built JAR files for Docker images
- Optimized Docker images with security best practices
- Colored output and comprehensive error handling
- Option to push images to registry

## Prerequisites

- Java 21 or higher
- Maven 3.6+ (or Maven wrapper will be used if available)
- Docker and Docker Compose
- Unix-like operating system (Linux, macOS)

## Making Scripts Executable

Before running the scripts, make them executable:

```bash
chmod +x scripts/build-all.sh
chmod +x scripts/build-individual.sh
chmod +x scripts/docker-build.sh
```

## Docker Images

The Docker images are optimized with the following features:

- **Base Image**: OpenJDK 21 slim for smaller size
- **Security**: Non-root user for running applications
- **Performance**: Optimized JVM settings for containers
- **Health Checks**: Built-in health check endpoints
- **Resource Management**: Memory limits and GC optimization

## Output

After successful builds:

**JAR Files:**
- `auth-service/target/auth-service-0.0.1-SNAPSHOT.jar`
- `users-service/target/users-service-0.0.1-SNAPSHOT.jar`

**Docker Images:**
- `crm-auth-service:latest`
- `crm-users-service:latest`

## Running the Services

After building, you can run the services using Docker Compose:

```bash
# Development environment
docker-compose -f docker/docker-compose.dev.yaml up

# Or run in background
docker-compose -f docker/docker-compose.dev.yaml up -d
```

**Service Ports:**
- Auth Service: `http://localhost:8081`
- Users Service: `http://localhost:8082`
- PostgreSQL: `localhost:5432`

## Error Handling

The scripts include comprehensive error handling:
- Validates service directories exist
- Reports build failures with clear error messages
- Exits with appropriate error codes
- Continues building other services even if one fails (in build-all.sh)
- Validates JAR files exist before Docker builds

## Notes

- Tests are skipped during build (`-DskipTests`) for faster builds
- The scripts automatically detect and use Maven wrapper if available
- All output is color-coded for better readability
- Scripts are designed to work from the project root directory
- Docker images use the pre-built JAR files (no build inside Docker)
- Health checks are configured for all services 