#!/bin/bash

# CRM Microservices Individual Build Script
# This script builds JAR files for individual services

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

print_usage() {
    echo "Usage: $0 <service-name>"
    echo "Available services:"
    echo "  auth-service"
    echo "  users-service"
    echo ""
    echo "Examples:"
    echo "  $0 auth-service"
    echo "  $0 users-service"
}

# Function to build a service
build_service() {
    local service_name=$1
    
    print_status "Building $service_name..."
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    SERVICE_PATH="$PROJECT_ROOT/$service_name"
    
    if [ ! -d "$SERVICE_PATH" ]; then
        print_error "Service directory $SERVICE_PATH does not exist!"
        return 1
    fi
    
    cd "$SERVICE_PATH"
    
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
            print_success "JAR file created: $SERVICE_PATH/$jar_file"
            print_status "JAR file size: $(du -h "$jar_file" | cut -f1)"
        fi
    else
        print_error "Failed to build $service_name!"
        return 1
    fi
    
    cd - > /dev/null
}

# Main execution
main() {
    if [ $# -eq 0 ]; then
        print_error "No service specified!"
        print_usage
        exit 1
    fi
    
    local service_name=$1
    
    # Validate service name
    case $service_name in
        "auth-service"|"users-service")
            build_service "$service_name"
            ;;
        *)
            print_error "Unknown service: $service_name"
            print_usage
            exit 1
            ;;
    esac
}

# Run main function
main "$@" 