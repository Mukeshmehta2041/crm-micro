#!/bin/bash

# CRM Microservices Docker Development Shutdown Script
# This script stops all services using docker-compose for development

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

# Function to stop services
stop_services() {
    local compose_file=$1
    local remove_volumes=${2:-false}
    
    print_status "Stopping services..."
    
    # Stop and remove containers
    docker-compose -f "$compose_file" down
    
    if [ $? -ne 0 ]; then
        print_error "Failed to stop services"
        return 1
    fi
    
    # Remove volumes if requested
    if [ "$remove_volumes" = "true" ]; then
        print_status "Removing volumes..."
        docker-compose -f "$compose_file" down -v
        
        # Remove any dangling volumes
        docker volume prune -f
        
        print_success "Volumes removed"
    fi
    
    print_success "Services stopped successfully"
    return 0
}

# Function to clean up Docker resources
cleanup_docker() {
    local deep_clean=${1:-false}
    
    if [ "$deep_clean" = "true" ]; then
        print_status "Performing deep cleanup..."
        
        # Remove unused containers
        docker container prune -f
        
        # Remove unused images
        docker image prune -f
        
        # Remove unused networks
        docker network prune -f
        
        print_success "Deep cleanup completed"
    else
        print_status "Performing basic cleanup..."
        
        # Remove only dangling images
        docker image prune -f
        
        print_success "Basic cleanup completed"
    fi
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -v, --volumes     Remove volumes (WARNING: This will delete all data)"
    echo "  -c, --clean       Perform deep cleanup of Docker resources"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                Stop services only"
    echo "  $0 -v             Stop services and remove volumes"
    echo "  $0 -c             Stop services and clean up Docker resources"
    echo "  $0 -v -c          Stop services, remove volumes, and clean up"
}

# Main execution
main() {
    local remove_volumes=false
    local deep_clean=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -v|--volumes)
                remove_volumes=true
                shift
                ;;
            -c|--clean)
                deep_clean=true
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
    
    print_status "Stopping CRM Microservices Development Environment..."
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    COMPOSE_FILE="$PROJECT_ROOT/docker/docker-compose.dev.yaml"
    
    print_status "Project root: $PROJECT_ROOT"
    print_status "Using compose file: $COMPOSE_FILE"
    
    # Check if compose file exists
    if [ ! -f "$COMPOSE_FILE" ]; then
        print_error "Docker compose file not found: $COMPOSE_FILE"
        exit 1
    fi
    
    # Change to docker directory
    cd "$PROJECT_ROOT/docker"
    
    # Show warning for destructive operations
    if [ "$remove_volumes" = "true" ]; then
        print_warning "WARNING: This will remove all volumes and delete all data!"
        read -p "Are you sure you want to continue? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_status "Operation cancelled"
            exit 0
        fi
    fi
    
    # Stop services
    if stop_services "docker-compose.dev.yaml" "$remove_volumes"; then
        print_success "Services stopped successfully"
    else
        print_error "Failed to stop services"
        exit 1
    fi
    
    # Cleanup if requested
    if [ "$deep_clean" = "true" ]; then
        cleanup_docker true
    else
        cleanup_docker false
    fi
    
    print_success "CRM Microservices Development Environment stopped! 🎉"
    
    if [ "$remove_volumes" = "true" ]; then
        print_warning "All data has been removed. Next startup will be a fresh installation."
    fi
    
    print_status ""
    print_status "To start services again, run: ./scripts/docker-dev-up.sh"
}

# Run main function
main "$@"