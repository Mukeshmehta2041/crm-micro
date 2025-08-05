#!/bin/bash

# CRM Microservices Docker Development Startup Script
# This script starts all services using docker-compose for development

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_success "Docker is running"
}

# Function to check if docker-compose is available
check_docker_compose() {
    if ! command -v docker-compose > /dev/null 2>&1; then
        print_error "docker-compose is not installed. Please install docker-compose and try again."
        exit 1
    fi
    print_success "docker-compose is available"
}

# Function to build and start services
start_services() {
    local compose_file=$1
    local environment=$2
    
    print_status "Building and starting services for $environment environment..."
    
    # Build images first
    print_status "Building Docker images..."
    docker-compose -f "$compose_file" build --no-cache
    
    if [ $? -ne 0 ]; then
        print_error "Failed to build Docker images"
        return 1
    fi
    
    # Start services
    print_status "Starting services..."
    docker-compose -f "$compose_file" up -d
    
    if [ $? -ne 0 ]; then
        print_error "Failed to start services"
        return 1
    fi
    
    print_success "Services started successfully"
    return 0
}

# Function to wait for services to be healthy
wait_for_services() {
    local compose_file=$1
    
    print_status "Waiting for services to be healthy..."
    
    local services=("service-registry" "postgres" "auth-service" "users-service")
    local max_attempts=60
    local attempt=1
    
    for service in "${services[@]}"; do
        print_status "Waiting for $service to be healthy..."
        
        while [ $attempt -le $max_attempts ]; do
            local health_status=$(docker-compose -f "$compose_file" ps -q "$service" | xargs docker inspect --format='{{.State.Health.Status}}' 2>/dev/null || echo "starting")
            
            if [ "$health_status" = "healthy" ]; then
                print_success "$service is healthy"
                break
            elif [ "$health_status" = "unhealthy" ]; then
                print_error "$service is unhealthy"
                docker-compose -f "$compose_file" logs "$service"
                return 1
            fi
            
            echo -n "."
            sleep 5
            ((attempt++))
        done
        
        if [ $attempt -gt $max_attempts ]; then
            print_error "$service failed to become healthy within timeout"
            docker-compose -f "$compose_file" logs "$service"
            return 1
        fi
        
        attempt=1
    done
    
    print_success "All services are healthy"
}

# Function to show service status
show_status() {
    local compose_file=$1
    
    print_status "Service Status:"
    docker-compose -f "$compose_file" ps
    
    echo ""
    print_status "Service URLs:"
    echo "  🔍 Service Registry Dashboard: http://localhost:8761"
    echo "  🔐 Auth Service: http://localhost:8081"
    echo "  👥 Users Service: http://localhost:8082"
    echo "  🗄️  PostgreSQL: localhost:5432"
    echo "  🔴 Redis: localhost:6379"
    echo ""
    print_status "Health Check URLs:"
    echo "  Service Registry: http://localhost:8761/actuator/health"
    echo "  Auth Service: http://localhost:8081/actuator/health"
    echo "  Users Service: http://localhost:8082/actuator/health"
}

# Main execution
main() {
    print_status "Starting CRM Microservices Development Environment..."
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    COMPOSE_FILE="$PROJECT_ROOT/docker/docker-compose.dev.yaml"
    
    print_status "Project root: $PROJECT_ROOT"
    print_status "Using compose file: $COMPOSE_FILE"
    
    # Check prerequisites
    check_docker
    check_docker_compose
    
    # Check if compose file exists
    if [ ! -f "$COMPOSE_FILE" ]; then
        print_error "Docker compose file not found: $COMPOSE_FILE"
        exit 1
    fi
    
    # Change to docker directory
    cd "$PROJECT_ROOT/docker"
    
    # Start services
    if start_services "docker-compose.dev.yaml" "development"; then
        print_success "Services build and startup completed"
    else
        print_error "Failed to start services"
        exit 1
    fi
    
    # Wait for services to be healthy
    if wait_for_services "docker-compose.dev.yaml"; then
        print_success "All services are ready"
    else
        print_error "Some services failed to start properly"
        print_status "Showing logs for debugging..."
        docker-compose -f "docker-compose.dev.yaml" logs
        exit 1
    fi
    
    # Show status
    show_status "docker-compose.dev.yaml"
    
    print_success "CRM Microservices Development Environment is ready! 🎉"
    print_status ""
    print_status "To stop all services, run: ./scripts/docker-dev-down.sh"
    print_status "To view logs, run: docker-compose -f docker/docker-compose.dev.yaml logs -f [service-name]"
}

# Run main function
main "$@"