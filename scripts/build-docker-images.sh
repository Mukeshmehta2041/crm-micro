#!/bin/bash

# CRM Microservices Docker Build Script
# This script builds Docker images for all services in the project

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

# Default values
DOCKER_REGISTRY="docker.io/mukeshkr1234"
IMAGE_TAG="latest"
BUILD_ARGS=""
PUSH_AFTER_BUILD=false

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -r, --registry REGISTRY    Docker registry (e.g., docker.io/username)"
    echo "  -t, --tag TAG             Image tag (default: latest)"
    echo "  -a, --args ARGS           Additional Docker build arguments"
    echo "  -p, --push                Push images after building"
    echo "  -h, --help                Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Build with default settings (docker.io/mukeshkr1234)"
    echo "  $0 -r docker.io/mukeshkr1234 -t v1.0.0       # Build with custom registry and tag"
    echo "  $0 -r docker.io/mukeshkr1234 -t v1.0.0 -p    # Build and push images"
}

# Function to parse command line arguments
parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -r|--registry)
                DOCKER_REGISTRY="$2"
                shift 2
                ;;
            -t|--tag)
                IMAGE_TAG="$2"
                shift 2
                ;;
            -a|--args)
                BUILD_ARGS="$2"
                shift 2
                ;;
            -p|--push)
                PUSH_AFTER_BUILD=true
                shift
                ;;
            -h|--help)
                show_usage
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
}

# Function to build JAR file for a service
build_jar() {
    local service_name=$1
    local service_path=$2
    
    print_status "Building JAR file for $service_name..."
    
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
        print_success "JAR file built successfully for $service_name!"
        
        # Find the JAR file
        local jar_file=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1)
        if [ -n "$jar_file" ]; then
            print_success "JAR file created: $service_path/$jar_file"
        fi
    else
        print_error "Failed to build JAR file for $service_name!"
        return 1
    fi
    
    cd - > /dev/null
}

# Function to build a Docker image
build_docker_image() {
    local service_name=$1
    local service_path=$2
    
    print_status "Building Docker image for $service_name..."
    
    if [ ! -d "$service_path" ]; then
        print_error "Service directory $service_path does not exist!"
        return 1
    fi
    
    if [ ! -f "$service_path/Dockerfile" ]; then
        print_warning "No Dockerfile found in $service_path, skipping..."
        return 0
    fi
    
    cd "$service_path"
    
    # Determine image name
    local image_name
    if [ -n "$DOCKER_REGISTRY" ]; then
        image_name="${DOCKER_REGISTRY}/${service_name}:${IMAGE_TAG}"
    else
        image_name="${service_name}:${IMAGE_TAG}"
    fi
    
    print_status "Building image: $image_name"
    
    # Build the Docker image
    local build_cmd="docker build"
    if [ -n "$BUILD_ARGS" ]; then
        build_cmd="$build_cmd $BUILD_ARGS"
    fi
    build_cmd="$build_cmd -t $image_name ."
    
    print_status "Executing: $build_cmd"
    
    if eval $build_cmd; then
        print_success "Docker image built successfully: $image_name"
        
        # Push image if requested
        if [ "$PUSH_AFTER_BUILD" = true ]; then
            print_status "Pushing image: $image_name"
            if docker push "$image_name"; then
                print_success "Image pushed successfully: $image_name"
            else
                print_error "Failed to push image: $image_name"
                return 1
            fi
        fi
    else
        print_error "Failed to build Docker image for $service_name!"
        return 1
    fi
    
    cd - > /dev/null
}

# Function to check Docker availability
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running or you don't have permission to access it"
        exit 1
    fi
    
    print_success "Docker is available and accessible"
}

# Main execution
main() {
    print_status "Starting CRM Microservices Docker build process..."
    
    # Parse command line arguments
    parse_arguments "$@"
    
    # Check Docker availability
    check_docker
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    
    print_status "Project root: $PROJECT_ROOT"
    print_status "Docker registry: ${DOCKER_REGISTRY:-'none (local images)'}"
    print_status "Image tag: $IMAGE_TAG"
    print_status "Push after build: $PUSH_AFTER_BUILD"
    
    # List of services to build
    services=(
        "service-registry:service-registry"
        "auth-service:auth-service"
        "users-service:users-service"
        "tenant-service:tenant-service"
        "api-gateway:api-gateway"
    )
    
    local build_errors=0
    
    # Build JAR files first
    print_status "Step 1: Building JAR files for all services..."
    local jar_build_errors=0
    
    for service in "${services[@]}"; do
        IFS=':' read -r service_name service_path <<< "$service"
        service_path="$PROJECT_ROOT/$service_path"
        
        if build_jar "$service_name" "$service_path"; then
            print_success "$service_name JAR build completed successfully"
        else
            print_error "$service_name JAR build failed"
            ((jar_build_errors++))
        fi
        
        echo ""
    done
    
    # Check if JAR builds were successful
    if [ $jar_build_errors -gt 0 ]; then
        print_error "$jar_build_errors service(s) failed to build JAR files. Cannot proceed with Docker builds."
        exit 1
    fi
    
    print_success "All JAR files built successfully! Proceeding to Docker image builds..."
    echo ""
    
    # Build Docker images
    print_status "Step 2: Building Docker images for all services..."
    
    for service in "${services[@]}"; do
        IFS=':' read -r service_name service_path <<< "$service"
        service_path="$PROJECT_ROOT/$service_path"
        
        if build_docker_image "$service_name" "$service_path"; then
            print_success "$service_name Docker build completed successfully"
        else
            print_error "$service_name Docker build failed"
            ((build_errors++))
        fi
        
        echo ""
    done
    
    # Summary
    if [ $build_errors -eq 0 ]; then
        print_success "All JAR files and Docker images built successfully! 🎉"
        if [ "$PUSH_AFTER_BUILD" = true ]; then
            print_success "All images have been pushed to the registry"
        else
            print_status "Images are available locally. Use the push script to push them to a registry."
        fi
    else
        print_error "$build_errors service(s) failed to build Docker images"
        exit 1
    fi
}

# Run main function
main "$@"
