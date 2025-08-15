#!/bin/bash

# CRM Microservices Docker Push Script
# This script pushes Docker images for all services to a Docker registry

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
FORCE_PUSH=false
DRY_RUN=false

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -r, --registry REGISTRY    Docker registry (e.g., docker.io/username)"
    echo "  -t, --tag TAG             Image tag to push (default: latest)"
    echo "  -f, --force               Force push even if image already exists"
    echo "  -d, --dry-run             Show what would be pushed without actually pushing"
    echo "  -h, --help                Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Push latest tag to docker.io/mukeshkr1234 (default)"
    echo "  $0 -r docker.io/mukeshkr1234 -t v1.0.0         # Push specific tag to registry"
    echo "  $0 -r docker.io/mukeshkr1234 -t v1.0.0 -f      # Force push specific tag"
    echo "  $0 -r docker.io/mukeshkr1234 -d                 # Dry run to see what would be pushed"
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
            -f|--force)
                FORCE_PUSH=true
                shift
                ;;
            -d|--dry-run)
                DRY_RUN=true
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

# Function to validate arguments
validate_arguments() {
    # Remove trailing slash if present
    DOCKER_REGISTRY="${DOCKER_REGISTRY%/}"
}

# Function to check if image exists locally
check_image_exists() {
    local service_name=$1
    local image_name="${service_name}:${IMAGE_TAG}"
    
    if docker images --format "{{.Repository}}:{{.Tag}}" | grep -q "^${image_name}$"; then
        return 0
    else
        return 1
    fi
}

# Function to check if image exists in registry
check_registry_image() {
    local service_name=$1
    local image_name="${DOCKER_REGISTRY}/${service_name}:${IMAGE_TAG}"
    
    # Try to pull the image info (this will fail if image doesn't exist)
    if docker manifest inspect "$image_name" >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Function to push a Docker image
push_docker_image() {
    local service_name=$1
    local service_path=$2
    
    print_status "Processing $service_name..."
    
    # Check if local image exists
    if ! check_image_exists "$service_name"; then
        print_warning "Local image ${service_name}:${IMAGE_TAG} not found, skipping..."
        return 0
    fi
    
    local local_image="${service_name}:${IMAGE_TAG}"
    local registry_image="${DOCKER_REGISTRY}/${service_name}:${IMAGE_TAG}"
    
    # Tag the local image for registry
    print_status "Tagging local image for registry: $local_image -> $registry_image"
    
    if [ "$DRY_RUN" = false ]; then
        if ! docker tag "$local_image" "$registry_image"; then
            print_error "Failed to tag image for $service_name"
            return 1
        fi
    fi
    
    # Check if image already exists in registry
    if check_registry_image "$service_name"; then
        if [ "$FORCE_PUSH" = false ]; then
            print_warning "Image $registry_image already exists in registry. Use -f to force push."
            return 0
        else
            print_status "Image exists in registry, force pushing..."
        fi
    fi
    
    # Push the image
    if [ "$DRY_RUN" = true ]; then
        print_status "[DRY RUN] Would push: $registry_image"
        return 0
    fi
    
    print_status "Pushing image: $registry_image"
    
    if docker push "$registry_image"; then
        print_success "Image pushed successfully: $registry_image"
        
        # Clean up local registry-tagged image
        print_status "Cleaning up local registry-tagged image..."
        docker rmi "$registry_image"
    else
        print_error "Failed to push image: $registry_image"
        return 1
    fi
}

# Function to check Docker availability and authentication
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
    
    # Check if we can access the registry
    print_status "Testing registry access..."
    
    # Extract just the registry part (without username) for the test
    local test_registry
    if [[ "$DOCKER_REGISTRY" == docker.io/* ]]; then
        test_registry="docker.io"
    else
        test_registry="$DOCKER_REGISTRY"
    fi
    
    if ! docker manifest inspect "${test_registry}/hello-world:latest" >/dev/null 2>&1; then
        print_warning "Could not access registry ${test_registry}. You may need to login first."
        print_status "Run: docker login ${test_registry}"
    else
        print_success "Registry access confirmed"
    fi
}

# Function to login to registry if needed
login_to_registry() {
    if [ "$DRY_RUN" = true ]; then
        print_status "[DRY RUN] Would check registry login"
        return 0
    fi
    
    print_status "Checking registry authentication..."
    
    # Extract just the registry part (without username) for the test
    local test_registry
    if [[ "$DOCKER_REGISTRY" == docker.io/* ]]; then
        test_registry="docker.io"
    else
        test_registry="$DOCKER_REGISTRY"
    fi
    
    # Try to access a test image
    if docker manifest inspect "${test_registry}/hello-world:latest" >/dev/null 2>&1; then
        print_success "Already authenticated with registry ${test_registry}"
        return 0
    fi
    
    print_warning "Not authenticated with registry ${test_registry}"
    print_status "Please login to the registry first:"
    echo "  docker login ${test_registry}"
    echo ""
    
    read -p "Do you want to try logging in now? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if docker login "$test_registry"; then
            print_success "Successfully logged in to registry ${test_registry}"
        else
            print_error "Failed to login to registry ${test_registry}"
            exit 1
        fi
    else
        print_error "Registry authentication required to continue"
        exit 1
    fi
}

# Main execution
main() {
    print_status "Starting CRM Microservices Docker push process..."
    
    # Parse command line arguments
    parse_arguments "$@"
    
    # Validate arguments
    validate_arguments
    
    # Check Docker availability
    check_docker
    
    # Login to registry if needed
    login_to_registry
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    
    print_status "Project root: $PROJECT_ROOT"
    print_status "Docker registry: $DOCKER_REGISTRY"
    print_status "Image tag: $IMAGE_TAG"
    print_status "Force push: $FORCE_PUSH"
    print_status "Dry run: $DRY_RUN"
    
    if [ "$DRY_RUN" = true ]; then
        print_warning "DRY RUN MODE - No images will actually be pushed"
    fi
    
    # List of services to push
    services=(
        "service-registry:service-registry"
        "auth-service:auth-service"
        "users-service:users-service"
        "tenant-service:tenant-service"
        "api-gateway:api-gateway"
    )
    
    local push_errors=0
    
    # Push each service
    for service in "${services[@]}"; do
        IFS=':' read -r service_name service_path <<< "$service"
        service_path="$PROJECT_ROOT/$service_path"
        
        if push_docker_image "$service_name" "$service_path"; then
            print_success "$service_name push completed successfully"
        else
            print_error "$service_name push failed"
            ((push_errors++))
        fi
        
        echo ""
    done
    
    # Summary
    if [ $push_errors -eq 0 ]; then
        print_success "All Docker images processed successfully! 🎉"
        if [ "$DRY_RUN" = true ]; then
            print_status "This was a dry run. Run without -d flag to actually push images."
        else
            print_success "All images have been pushed to the registry"
        fi
    else
        print_error "$push_errors service(s) failed to push"
        exit 1
    fi
}

# Run main function
main "$@"
