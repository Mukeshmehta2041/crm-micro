#!/bin/bash

# CRM Microservices Stop Script
# This script stops all running services

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

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file=$2
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            print_status "Stopping $service_name (PID: $pid)..."
            kill $pid
            
            # Wait for process to stop
            local attempts=0
            while ps -p $pid > /dev/null 2>&1 && [ $attempts -lt 30 ]; do
                sleep 1
                ((attempts++))
            done
            
            if ps -p $pid > /dev/null 2>&1; then
                print_warning "Force killing $service_name..."
                kill -9 $pid
            fi
            
            print_success "$service_name stopped"
        else
            print_warning "$service_name was not running"
        fi
        
        rm -f "$pid_file"
    else
        print_warning "PID file not found for $service_name"
    fi
}

# Main execution
main() {
    print_status "Stopping CRM Microservices..."
    
    # Get the script directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
    LOGS_DIR="$PROJECT_ROOT/logs"
    
    # Stop services
    services=("auth-service" "users-service" "service-registry")
    
    for service in "${services[@]}"; do
        stop_service "$service" "$LOGS_DIR/${service}.pid"
    done
    
    # Also kill any Java processes that might be our services
    print_status "Checking for any remaining service processes..."
    pkill -f "auth-service.*\.jar" 2>/dev/null || true
    pkill -f "users-service.*\.jar" 2>/dev/null || true
    pkill -f "service-registry.*\.jar" 2>/dev/null || true
    
    print_success "All services stopped! 🎉"
}

# Run main function
main "$@"