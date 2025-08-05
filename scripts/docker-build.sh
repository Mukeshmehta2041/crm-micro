#!/bin/bash

# CRM Microservices Docker Build Script
# This script builds JAR files and Docker images for all services

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
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --build-jars     Build JAR files first (default)"
    echo "  --skip-jars      Skip JAR building, use existing JARs"
    echo "  --push           Push images to registry after building"
    echo "  --help           Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                    # Build JARs and Docker images"
    echo "  $0 --skip-jars        # Build Docker images only"
    echo "  $0 --push             # Build and push images"
}

# Function to build JAR files
build_jars() {
    print_status "Building JAR files..."
    ./scripts/build-all.sh
    print_success "JAR files built successfully!"
}

# Function to build Docker image
build_docker_image() {
    local service_name=$1
    local service_path=$2
    
    print_status "Building Docker image for $service_name..."
    
    if [ ! -d "$service_path" ]; then
        print_error "Service directory $service_path does not exist!"
        return 1
    fi
    
    # Check if JAR file exists
    local jar_file=$(find "$service_path/target" -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1)
    if [ -z "$jar_file" ]; then
        print_error "No JAR file found in $service_path/target/"
        return 1
    fi
    
    cd "$service_path"
    
    # Build Docker image
    docker build -t "crm-$service_name:latest" .
    
    if [ $? -eq 0 ]; then
        print_success "Docker image built successfully: crm-$service_name:latest"
    else
        print_error "Failed to build Docker image for $service_name!"
        return 1
    fi
    
    cd - > /dev/null
}

# Function to push Docker images
push_images() {
    print_status "Pushing Docker images..."
    
    services=("auth-service" "users-service")
    
    for service in "${services[@]}"; do
        print_status "Pushing crm-$service:latest..."
        docker push "crm-$service:latest"
        
        if [ $? -eq 0 ]; then
            print_success "Successfully pushed crm-$service:latest"
        else
            print_error "Failed to push crm-$service:latest"
        fi
    done
}

# Main execution
main() {
    local build_jars=true
    local push_images=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --build-jars)
                build_jars=true
                shift
                ;;
            --skip-jars)
                build_jars=false
                shift
                ;;
            --push)
                push_images=true
                shift
                ;;
            --help)
                print_usage
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                print_usage
                exit 1
                ;;
        esac
    done
    
    print_status "Starting CRM Microservices Docker build process..."
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    
    print_status "Project root: $PROJECT_ROOT"
    
    # Build JAR files if requested
    if [ "$build_jars" = true ]; then
        build_jars
    else
        print_status "Skipping JAR build, using existing JAR files"
    fi
    
    # List of services to build
    services=(
        "auth-service:auth-service"
        "users-service:users-service"
    )
    
    local build_errors=0
    
    # Build Docker images for each service
    for service in "${services[@]}"; do
        IFS=':' read -r service_name service_path <<< "$service"
        service_path="$PROJECT_ROOT/$service_path"
        
        if build_docker_image "$service_name" "$service_path"; then
            print_success "$service_name Docker image built successfully"
        else
            print_error "$service_name Docker image build failed"
            ((build_errors++))
        fi
        
        echo ""
    done
    
    # Push images if requested
    if [ "$push_images" = true ]; then
        push_images
    fi
    
    # Summary
    if [ $build_errors -eq 0 ]; then
        print_success "All Docker images built successfully! 🎉"
        print_status "You can now run: docker-compose -f docker/docker-compose.dev.yaml up"
    else
        print_error "$build_errors Docker image(s) failed to build"
        exit 1
    fi
}

# Run main function
main "$@" 