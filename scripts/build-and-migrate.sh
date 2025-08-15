#!/bin/bash

# Build and migrate script for CRM microservices
# This script builds all services and runs database migrations

set -e

echo "🚀 Starting CRM Microservices Build and Migration Process..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if PostgreSQL is running
check_postgres() {
    print_status "Checking PostgreSQL connection..."
    if pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
        print_success "PostgreSQL is running"
    else
        print_error "PostgreSQL is not running. Please start PostgreSQL first."
        exit 1
    fi
}

# Function to build a service
build_service() {
    local service_name=$1
    print_status "Building $service_name..."
    
    cd "$service_name"
    
    # Clean and compile
    if mvn clean compile -q; then
        print_success "$service_name compiled successfully"
    else
        print_error "Failed to compile $service_name"
        cd ..
        exit 1
    fi
    
    cd ..
}

# Function to run Flyway migration for a service
migrate_service() {
    local service_name=$1
    print_status "Running Flyway migration for $service_name..."
    
    cd "$service_name"
    
    # Run Flyway migration
    if mvn flyway:migrate -q; then
        print_success "$service_name migration completed successfully"
    else
        print_error "Failed to migrate $service_name"
        cd ..
        exit 1
    fi
    
    cd ..
}

# Function to package a service
package_service() {
    local service_name=$1
    print_status "Packaging $service_name..."
    
    cd "$service_name"
    
    # Package the service
    if mvn package -DskipTests -q; then
        print_success "$service_name packaged successfully"
    else
        print_error "Failed to package $service_name"
        cd ..
        exit 1
    fi
    
    cd ..
}

# Main execution
main() {
    # Check prerequisites
    check_postgres
    
    # Services to build and migrate
    services=("service-registry" "auth-service" "tenant-service" "users-service" "api-gateway")
    
    print_status "Building all services..."
    
    # Build all services first
    for service in "${services[@]}"; do
        if [ -d "$service" ]; then
            build_service "$service"
        else
            print_warning "Directory $service not found, skipping..."
        fi
    done
    
    print_status "Running database migrations..."
    
    # Run migrations for services that have databases
    database_services=("auth-service" "tenant-service" "users-service")
    for service in "${database_services[@]}"; do
        if [ -d "$service" ]; then
            migrate_service "$service"
        else
            print_warning "Directory $service not found, skipping migration..."
        fi
    done
    
    print_status "Packaging all services..."
    
    # Package all services
    for service in "${services[@]}"; do
        if [ -d "$service" ]; then
            package_service "$service"
        else
            print_warning "Directory $service not found, skipping packaging..."
        fi
    done
    
    print_success "🎉 All services built and migrated successfully!"
    
    echo ""
    print_status "Next steps:"
    echo "1. Start service-registry: java -jar service-registry/target/service-registry-*.jar"
    echo "2. Start auth-service: java -jar auth-service/target/auth-service-*.jar"
    echo "3. Start tenant-service: java -jar tenant-service/target/tenant-service-*.jar"
    echo "4. Start users-service: java -jar users-service/target/users-service-*.jar"
    echo "5. Start api-gateway: java -jar api-gateway/target/api-gateway-*.jar"
    echo ""
    print_status "Or use Docker Compose: docker-compose up -d"
}

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    print_error "Please run this script from the project root directory"
    exit 1
fi

# Run main function
main