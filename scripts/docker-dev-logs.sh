#!/bin/bash

# CRM Microservices Docker Development Logs Script
# This script shows logs for services in development environment

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

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS] [SERVICE]"
    echo ""
    echo "Services:"
    echo "  service-registry  Service Registry (Eureka Server)"
    echo "  auth-service      Authentication Service"
    echo "  users-service     Users Management Service"
    echo "  postgres          PostgreSQL Database"
    echo "  redis             Redis Cache"
    echo ""
    echo "Options:"
    echo "  -f, --follow      Follow log output (like tail -f)"
    echo "  -t, --tail N      Number of lines to show from the end of logs (default: 100)"
    echo "  -s, --since TIME  Show logs since timestamp (e.g. 2023-01-01T00:00:00)"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                        Show logs for all services"
    echo "  $0 auth-service           Show logs for auth-service only"
    echo "  $0 -f                     Follow logs for all services"
    echo "  $0 -f auth-service        Follow logs for auth-service only"
    echo "  $0 -t 50 users-service    Show last 50 lines for users-service"
    echo "  $0 --since 1h             Show logs from last hour"
}

# Function to show logs
show_logs() {
    local compose_file=$1
    local service=$2
    local follow=$3
    local tail_lines=$4
    local since=$5
    
    local docker_args=""
    
    # Build docker-compose logs arguments
    if [ "$follow" = "true" ]; then
        docker_args="$docker_args -f"
    fi
    
    if [ -n "$tail_lines" ]; then
        docker_args="$docker_args --tail=$tail_lines"
    fi
    
    if [ -n "$since" ]; then
        docker_args="$docker_args --since=$since"
    fi
    
    # Show logs
    if [ -n "$service" ]; then
        print_status "Showing logs for service: $service"
        docker-compose -f "$compose_file" logs $docker_args "$service"
    else
        print_status "Showing logs for all services"
        docker-compose -f "$compose_file" logs $docker_args
    fi
}

# Function to list available services
list_services() {
    local compose_file=$1
    
    print_status "Available services:"
    docker-compose -f "$compose_file" config --services | while read service; do
        local status=$(docker-compose -f "$compose_file" ps -q "$service" | xargs docker inspect --format='{{.State.Status}}' 2>/dev/null || echo "not running")
        echo "  📦 $service ($status)"
    done
}

# Main execution
main() {
    local service=""
    local follow=false
    local tail_lines="100"
    local since=""
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -f|--follow)
                follow=true
                shift
                ;;
            -t|--tail)
                tail_lines="$2"
                shift 2
                ;;
            -s|--since)
                since="$2"
                shift 2
                ;;
            -h|--help)
                show_usage
                exit 0
                ;;
            -*)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
            *)
                if [ -z "$service" ]; then
                    service="$1"
                else
                    print_error "Multiple services specified. Please specify only one service."
                    show_usage
                    exit 1
                fi
                shift
                ;;
        esac
    done
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    COMPOSE_FILE="$PROJECT_ROOT/docker/docker-compose.dev.yaml"
    
    # Check if compose file exists
    if [ ! -f "$COMPOSE_FILE" ]; then
        print_error "Docker compose file not found: $COMPOSE_FILE"
        exit 1
    fi
    
    # Change to docker directory
    cd "$PROJECT_ROOT/docker"
    
    # Validate service name if provided
    if [ -n "$service" ]; then
        local available_services=$(docker-compose -f "docker-compose.dev.yaml" config --services)
        if ! echo "$available_services" | grep -q "^$service$"; then
            print_error "Service '$service' not found."
            echo ""
            list_services "docker-compose.dev.yaml"
            exit 1
        fi
    fi
    
    # Show logs
    print_status "CRM Microservices Development Environment Logs"
    echo ""
    
    if [ "$follow" = "true" ]; then
        print_status "Following logs... (Press Ctrl+C to stop)"
    fi
    
    show_logs "docker-compose.dev.yaml" "$service" "$follow" "$tail_lines" "$since"
}

# Run main function
main "$@"