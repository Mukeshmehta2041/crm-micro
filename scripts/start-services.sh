#!/bin/bash

# CRM Microservices Startup Script
# This script starts all services in the correct order for local development

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

# Function to check if a service is running
check_service() {
    local port=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service_name to start on port $port..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            print_success "$service_name is running on port $port"
            return 0
        fi
        
        echo -n "."
        sleep 2
        ((attempt++))
    done
    
    print_error "$service_name failed to start on port $port"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_path=$2
    local port=$3
    local profile=${4:-"local"}
    
    print_status "Starting $service_name..."
    
    cd "$service_path"
    
    # Find the JAR file
    local jar_file=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1)
    
    if [ -z "$jar_file" ]; then
        print_error "JAR file not found for $service_name. Please build the service first."
        return 1
    fi
    
    # Start the service in background
    nohup java -jar "$jar_file" --spring.profiles.active="$profile" > "../logs/${service_name}.log" 2>&1 &
    local pid=$!
    echo $pid > "../logs/${service_name}.pid"
    
    print_status "$service_name started with PID $pid"
    
    cd - > /dev/null
    
    # Wait for service to be ready
    if check_service "$port" "$service_name"; then
        return 0
    else
        return 1
    fi
}

# Main execution
main() {
    print_status "Starting CRM Microservices..."
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    
    # Create logs directory
    mkdir -p "$PROJECT_ROOT/logs"
    
    print_status "Project root: $PROJECT_ROOT"
    
    # Start services in order
    print_status "Step 1: Starting Service Registry..."
    if start_service "service-registry" "$PROJECT_ROOT/service-registry" "8761" "default"; then
        print_success "Service Registry started successfully"
    else
        print_error "Failed to start Service Registry"
        exit 1
    fi
    
    sleep 10  # Give Eureka time to fully initialize
    
    print_status "Step 2: Starting Users Service..."
    if start_service "users-service" "$PROJECT_ROOT/users-service" "8082" "local"; then
        print_success "Users Service started successfully"
    else
        print_error "Failed to start Users Service"
        exit 1
    fi
    
    print_status "Step 3: Starting Auth Service..."
    if start_service "auth-service" "$PROJECT_ROOT/auth-service" "8080" "local"; then
        print_success "Auth Service started successfully"
    else
        print_error "Failed to start Auth Service"
        exit 1
    fi
    
    print_success "All services started successfully! 🎉"
    print_status "Service Registry Dashboard: http://localhost:8761"
    print_status "Auth Service: http://localhost:8080"
    print_status "Users Service: http://localhost:8082"
    print_status ""
    print_status "To stop all services, run: ./scripts/stop-services.sh"
}

# Run main function
main "$@"