#!/bin/bash

# Setup CRM Microservices Environment
# This script sets up the complete environment for CRM microservices

set -e

echo "🚀 Setting up CRM Microservices Environment..."
echo "=============================================="

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

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker compose &> /dev/null; then
    print_error "docker-compose is not available. Please install it and try again."
    exit 1
fi

print_status "Starting PostgreSQL database..."
docker compose up postgres -d

print_status "Waiting for PostgreSQL to be ready..."
until docker exec crm-postgres pg_isready -U user_crm -d db_crm > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo ""
print_success "PostgreSQL is ready!"

print_status "Setting up database users and permissions..."
./scripts/setup-database.sh

print_success "Database setup completed!"

print_status "Starting Service Registry (Eureka)..."
docker compose up service-registry -d

print_status "Waiting for Service Registry to be ready..."
until curl -s http://localhost:8761/actuator/health > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo ""
print_success "Service Registry is ready!"

print_status "Starting Auth Service..."
docker compose up auth-service -d

print_status "Waiting for Auth Service to be ready..."
until curl -s http://localhost:8081/auth/actuator/health > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo ""
print_success "Auth Service is ready!"

print_status "Starting Users Service..."
docker compose up users-service -d

print_status "Waiting for Users Service to be ready..."
until curl -s http://localhost:8082/users/actuator/health > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo ""
print_success "Users Service is ready!"

print_status "Starting Tenant Service..."
docker compose up tenant-service -d

print_status "Waiting for Tenant Service to be ready..."
until curl -s http://localhost:8083/tenant/actuator/health > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo ""
print_success "Tenant Service is ready!"

print_status "Starting API Gateway..."
docker compose up api-gateway -d

print_status "Waiting for API Gateway to be ready..."
until curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo ""
print_success "API Gateway is ready!"

echo ""
echo "🎉 CRM Microservices Environment Setup Complete!"
echo "=============================================="
echo ""
echo "Services Status:"
echo "  ✅ PostgreSQL Database: localhost:5432"
echo "  ✅ Service Registry: http://localhost:8761"
echo "  ✅ Auth Service: http://localhost:8081"
echo "  ✅ Users Service: http://localhost:8082"
echo "  ✅ Tenant Service: http://localhost:8083"
echo "  ✅ API Gateway: http://localhost:8080"
echo ""
echo "Useful URLs:"
echo "  📊 Eureka Dashboard: http://localhost:8761"
echo "  📚 Auth Service API: http://localhost:8081/auth/swagger-ui.html"
echo "  👥 Users Service API: http://localhost:8082/users/swagger-ui.html"
echo "  🏢 Tenant Service API: http://localhost:8083/tenant/swagger-ui.html"
echo "  🌐 API Gateway: http://localhost:8080"
echo ""
echo "To stop all services: docker compose down"
echo "To view logs: docker compose logs -f [service-name]"
echo "To restart a service: docker compose restart [service-name]"
