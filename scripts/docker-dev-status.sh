#!/bin/bash

# CRM Microservices Docker Development Status Script
# This script shows the status of all services in development environment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
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

print_header() {
    echo -e "${PURPLE}$1${NC}"
}

print_service() {
    echo -e "${CYAN}$1${NC}"
}

# Function to get service status
get_service_status() {
    local compose_file=$1
    local service=$2
    
    local container_id=$(docker-compose -f "$compose_file" ps -q "$service" 2>/dev/null)
    
    if [ -z "$container_id" ]; then
        echo "not created"
        return
    fi
    
    local status=$(docker inspect --format='{{.State.Status}}' "$container_id" 2>/dev/null || echo "unknown")
    local health=$(docker inspect --format='{{.State.Health.Status}}' "$container_id" 2>/dev/null || echo "none")
    
    if [ "$health" != "none" ] && [ "$health" != "<no value>" ]; then
        echo "$status ($health)"
    else
        echo "$status"
    fi
}

# Function to get service ports
get_service_ports() {
    local compose_file=$1
    local service=$2
    
    docker-compose -f "$compose_file" port "$service" 2>/dev/null | head -5 || echo "No ports exposed"
}

# Function to check service health endpoint
check_health_endpoint() {
    local url=$1
    local timeout=${2:-5}
    
    if curl -s -f --max-time "$timeout" "$url" > /dev/null 2>&1; then
        echo "✅ Healthy"
    else
        echo "❌ Unhealthy"
    fi
}

# Function to show detailed service status
show_service_details() {
    local compose_file=$1
    local service=$2
    
    local status=$(get_service_status "$compose_file" "$service")
    local ports=$(get_service_ports "$compose_file" "$service")
    
    print_service "📦 $service"
    echo "   Status: $status"
    echo "   Ports: $ports"
    
    # Check health endpoints for specific services
    case $service in
        "service-registry")
            local health=$(check_health_endpoint "http://localhost:8761/actuator/health")
            echo "   Health: $health"
            echo "   Dashboard: http://localhost:8761"
            ;;
        "auth-service")
            local health=$(check_health_endpoint "http://localhost:8081/actuator/health")
            echo "   Health: $health"
            echo "   API: http://localhost:8081"
            ;;
        "users-service")
            local health=$(check_health_endpoint "http://localhost:8082/actuator/health")
            echo "   Health: $health"
            echo "   API: http://localhost:8082"
            echo "   Swagger: http://localhost:8082/swagger-ui.html"
            ;;
        "postgres")
            echo "   Database: db_crm"
            echo "   Connection: localhost:5432"
            ;;
        "redis")
            local redis_status="❌ Unavailable"
            if redis-cli -h localhost -p 6379 ping > /dev/null 2>&1; then
                redis_status="✅ Available"
            fi
            echo "   Status: $redis_status"
            echo "   Connection: localhost:6379"
            ;;
    esac
    echo ""
}

# Function to show resource usage
show_resource_usage() {
    local compose_file=$1
    
    print_header "🔧 Resource Usage"
    echo ""
    
    # Get container stats
    local containers=$(docker-compose -f "$compose_file" ps -q 2>/dev/null)
    
    if [ -n "$containers" ]; then
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}" $containers
    else
        echo "No containers running"
    fi
    echo ""
}

# Function to show network information
show_network_info() {
    local compose_file=$1
    
    print_header "🌐 Network Information"
    echo ""
    
    # Get network name
    local network_name=$(docker-compose -f "$compose_file" config | grep -A 5 "networks:" | grep -v "networks:" | grep -v "driver:" | grep -v "ipam:" | grep -v "config:" | grep -v "subnet:" | head -1 | sed 's/^[[:space:]]*//' | sed 's/:$//')
    
    if [ -n "$network_name" ]; then
        local full_network_name="docker_${network_name}"
        if docker network inspect "$full_network_name" > /dev/null 2>&1; then
            echo "Network: $full_network_name"
            docker network inspect "$full_network_name" --format='Subnet: {{range .IPAM.Config}}{{.Subnet}}{{end}}'
            echo ""
            echo "Connected containers:"
            docker network inspect "$full_network_name" --format='{{range $k, $v := .Containers}}{{printf "  %s (%s)\n" $v.Name $v.IPv4Address}}{{end}}'
        else
            echo "Network not found: $full_network_name"
        fi
    else
        echo "No custom network found"
    fi
    echo ""
}

# Function to show volume information
show_volume_info() {
    local compose_file=$1
    
    print_header "💾 Volume Information"
    echo ""
    
    # Get volumes
    local volumes=$(docker-compose -f "$compose_file" config --volumes 2>/dev/null)
    
    if [ -n "$volumes" ]; then
        echo "$volumes" | while read volume; do
            if [ -n "$volume" ]; then
                local full_volume_name="docker_${volume}"
                if docker volume inspect "$full_volume_name" > /dev/null 2>&1; then
                    local mountpoint=$(docker volume inspect "$full_volume_name" --format='{{.Mountpoint}}')
                    local size=$(docker system df -v | grep "$full_volume_name" | awk '{print $3}' || echo "Unknown")
                    echo "📁 $volume"
                    echo "   Size: $size"
                    echo "   Path: $mountpoint"
                    echo ""
                fi
            fi
        done
    else
        echo "No volumes found"
    fi
}

# Main execution
main() {
    print_header "🚀 CRM Microservices Development Environment Status"
    echo ""
    
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
    
    # Show service status
    print_header "📋 Service Status"
    echo ""
    
    local services=("service-registry" "postgres" "redis" "auth-service" "users-service")
    
    for service in "${services[@]}"; do
        show_service_details "docker-compose.dev.yaml" "$service"
    done
    
    # Show resource usage
    show_resource_usage "docker-compose.dev.yaml"
    
    # Show network information
    show_network_info "docker-compose.dev.yaml"
    
    # Show volume information
    show_volume_info "docker-compose.dev.yaml"
    
    # Show quick access URLs
    print_header "🔗 Quick Access URLs"
    echo ""
    echo "  🔍 Service Registry: http://localhost:8761"
    echo "  🔐 Auth Service: http://localhost:8081"
    echo "  👥 Users Service: http://localhost:8082"
    echo "  📚 Users API Docs: http://localhost:8082/swagger-ui.html"
    echo "  🗄️  PostgreSQL: localhost:5432 (db_crm/user_crm/password_crm)"
    echo "  🔴 Redis: localhost:6379"
    echo ""
    
    print_success "Status check completed! 🎉"
}

# Run main function
main "$@"