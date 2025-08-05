#!/bin/bash

# CRM Microservices Build Script
# This script builds JAR files for all services in the project

set -e  # Exit on any error

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

# Function to build a service
build_service() {
    local service_name=$1
    local service_path=$2
    
    print_status "Building $service_name..."
    
    if [ ! -d "$service_path" ]; then
        print_error "Service directory $service_path does not exist!"
        return 1
    fi
    
    cd "$service_path"
    
    # Check if Maven wrapper exists
    if [ -f "./mvnw" ]; then
        print_status "Using Maven wrapper for $service_name"
        ./mvnw clean package -DskipTests
    elif [ -f "./mvnw.cmd" ]; then
        print_status "Using Maven wrapper for $service_name"
        ./mvnw clean package -DskipTests
    else
        print_status "Using system Maven for $service_name"
        mvn clean package -DskipTests
    fi
    
    if [ $? -eq 0 ]; then
        print_success "$service_name built successfully!"
        
        # Find the JAR file
        local jar_file=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1)
        if [ -n "$jar_file" ]; then
            print_success "JAR file created: $service_path/$jar_file"
        fi
    else
        print_error "Failed to build $service_name!"
        return 1
    fi
    
    cd - > /dev/null
}

# Main execution
main() {
    print_status "Starting CRM Microservices build process..."
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    
    print_status "Project root: $PROJECT_ROOT"
    
    # List of services to build
    services=(
        "auth-service:auth-service"
        "users-service:users-service"
    )
    
    local build_errors=0
    
    # Build each service
    for service in "${services[@]}"; do
        IFS=':' read -r service_name service_path <<< "$service"
        service_path="$PROJECT_ROOT/$service_path"
        
        if build_service "$service_name" "$service_path"; then
            print_success "$service_name build completed successfully"
        else
            print_error "$service_name build failed"
            ((build_errors++))
        fi
        
        echo ""
    done
    
    # Summary
    if [ $build_errors -eq 0 ]; then
        print_success "All services built successfully! 🎉"
        print_status "JAR files are located in each service's target/ directory"
    else
        print_error "$build_errors service(s) failed to build"
        exit 1
    fi
}

# Run main function
main "$@"