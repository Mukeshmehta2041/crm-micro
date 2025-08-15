#!/bin/bash

# Flyway validation script for CRM microservices
# This script validates all database migrations

set -e

echo "🔍 Validating Database Migrations..."

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

# Function to validate migrations for a service
validate_service() {
    local service_name=$1
    print_status "Validating migrations for $service_name..."
    
    cd "$service_name"
    
    # Validate migrations
    if mvn flyway:validate -q; then
        print_success "$service_name migrations are valid"
    else
        print_error "Invalid migrations found in $service_name"
        cd ..
        exit 1
    fi
    
    # Show migration info
    print_status "Migration info for $service_name:"
    mvn flyway:info -q
    
    cd ..
    echo ""
}

# Function to check migration status
check_migration_status() {
    local service_name=$1
    print_status "Checking migration status for $service_name..."
    
    cd "$service_name"
    
    # Check if there are pending migrations
    if mvn flyway:info -q | grep -q "Pending"; then
        print_warning "$service_name has pending migrations"
        return 1
    else
        print_success "$service_name is up to date"
        return 0
    fi
    
    cd ..
}

# Main execution
main() {
    # Services with database migrations
    services=("auth-service" "tenant-service" "users-service")
    
    print_status "Validating all database migrations..."
    echo ""
    
    # Validate all services
    for service in "${services[@]}"; do
        if [ -d "$service" ]; then
            validate_service "$service"
        else
            print_warning "Directory $service not found, skipping..."
        fi
    done
    
    print_status "Checking for pending migrations..."
    echo ""
    
    # Check for pending migrations
    pending_count=0
    for service in "${services[@]}"; do
        if [ -d "$service" ]; then
            if ! check_migration_status "$service"; then
                ((pending_count++))
            fi
        fi
    done
    
    if [ $pending_count -eq 0 ]; then
        print_success "🎉 All migrations are valid and up to date!"
    else
        print_warning "⚠️  $pending_count service(s) have pending migrations"
        echo ""
        print_status "To apply pending migrations, run:"
        echo "  ./scripts/build-and-migrate.sh"
    fi
}

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    print_error "Please run this script from the project root directory"
    exit 1
fi

# Run main function
main