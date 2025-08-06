#!/bin/bash

# API Gateway Test Script
# Tests all features: Circuit Breaker, Rate Limiting, Load Balancing, Caching, Authentication

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
GATEWAY_URL="http://localhost:8080"
AUTH_SERVICE_URL="http://localhost:8081"
USERS_SERVICE_URL="http://localhost:8082"

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

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service_name to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$url/actuator/health" > /dev/null 2>&1; then
            print_success "$service_name is ready!"
            return 0
        fi
        
        print_status "Attempt $attempt/$max_attempts - $service_name not ready yet..."
        sleep 2
        ((attempt++))
    done
    
    print_error "$service_name failed to start within expected time"
    return 1
}

# Test basic connectivity
test_connectivity() {
    print_status "Testing basic connectivity..."
    
    # Test gateway health
    if curl -s -f "$GATEWAY_URL/actuator/health" > /dev/null; then
        print_success "API Gateway is accessible"
    else
        print_error "API Gateway is not accessible"
        return 1
    fi
    
    # Test gateway info
    if curl -s -f "$GATEWAY_URL/gateway/info" > /dev/null; then
        print_success "Gateway info endpoint is working"
    else
        print_error "Gateway info endpoint is not working"
        return 1
    fi
    
    # Test routes endpoint
    if curl -s -f "$GATEWAY_URL/gateway/routes" > /dev/null; then
        print_success "Gateway routes endpoint is working"
    else
        print_error "Gateway routes endpoint is not working"
        return 1
    fi
}

# Test authentication
test_authentication() {
    print_status "Testing authentication..."
    
    # Test access without token (should fail)
    local response_code=$(curl -s -o /dev/null -w "%{http_code}" "$GATEWAY_URL/api/v1/users")
    if [ "$response_code" = "401" ]; then
        print_success "Authentication properly blocks unauthorized requests"
    else
        print_warning "Expected 401, got $response_code for unauthorized request"
    fi
    
    # Register a test user
    print_status "Registering test user..."
    local register_response=$(curl -s -X POST "$GATEWAY_URL/api/v1/auth/register" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "testuser",
            "email": "test@example.com",
            "password": "TestPass123!",
            "firstName": "Test",
            "lastName": "User"
        }')
    
    if echo "$register_response" | grep -q "error"; then
        print_warning "User registration failed or user already exists"
    else
        print_success "User registration successful"
    fi
    
    # Login to get token
    print_status "Logging in to get JWT token..."
    local login_response=$(curl -s -X POST "$GATEWAY_URL/api/v1/auth/login" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "testuser",
            "password": "TestPass123!"
        }')
    
    local token=$(echo "$login_response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    
    if [ -n "$token" ]; then
        print_success "Login successful, token obtained"
        export JWT_TOKEN="$token"
    else
        print_error "Failed to obtain JWT token"
        print_error "Login response: $login_response"
        return 1
    fi
    
    # Test access with token (should succeed)
    local auth_response_code=$(curl -s -o /dev/null -w "%{http_code}" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        "$GATEWAY_URL/api/v1/users")
    
    if [ "$auth_response_code" = "200" ]; then
        print_success "Authentication with JWT token works"
    else
        print_error "Authentication with JWT token failed (code: $auth_response_code)"
        return 1
    fi
}

# Test rate limiting
test_rate_limiting() {
    print_status "Testing rate limiting..."
    
    if [ -z "$JWT_TOKEN" ]; then
        print_error "JWT token not available for rate limiting test"
        return 1
    fi
    
    local success_count=0
    local rate_limited_count=0
    
    print_status "Sending 25 requests to test rate limiting..."
    
    for i in {1..25}; do
        local response_code=$(curl -s -o /dev/null -w "%{http_code}" \
            -H "Authorization: Bearer $JWT_TOKEN" \
            "$GATEWAY_URL/api/v1/users")
        
        if [ "$response_code" = "200" ]; then
            ((success_count++))
        elif [ "$response_code" = "429" ]; then
            ((rate_limited_count++))
            print_status "Request $i: Rate limited (429)"
        else
            print_warning "Request $i: Unexpected response code $response_code"
        fi
        
        # Small delay between requests
        sleep 0.1
    done
    
    print_status "Rate limiting test results:"
    print_status "  Successful requests: $success_count"
    print_status "  Rate limited requests: $rate_limited_count"
    
    if [ $rate_limited_count -gt 0 ]; then
        print_success "Rate limiting is working correctly"
    else
        print_warning "Rate limiting may not be working as expected"
    fi
}

# Test caching
test_caching() {
    print_status "Testing response caching..."
    
    if [ -z "$JWT_TOKEN" ]; then
        print_error "JWT token not available for caching test"
        return 1
    fi
    
    # First request (should be cache miss)
    print_status "Making first request (should be cache miss)..."
    local first_response=$(curl -s -H "Authorization: Bearer $JWT_TOKEN" \
        "$GATEWAY_URL/api/v1/users")
    
    local first_cache_header=$(curl -s -I -H "Authorization: Bearer $JWT_TOKEN" \
        "$GATEWAY_URL/api/v1/users" | grep -i "x-cache" | tr -d '\r')
    
    # Second request (should be cache hit)
    print_status "Making second request (should be cache hit)..."
    local second_response=$(curl -s -H "Authorization: Bearer $JWT_TOKEN" \
        "$GATEWAY_URL/api/v1/users")
    
    local second_cache_header=$(curl -s -I -H "Authorization: Bearer $JWT_TOKEN" \
        "$GATEWAY_URL/api/v1/users" | grep -i "x-cache" | tr -d '\r')
    
    print_status "First request cache header: $first_cache_header"
    print_status "Second request cache header: $second_cache_header"
    
    if echo "$second_cache_header" | grep -q "HIT"; then
        print_success "Response caching is working correctly"
    else
        print_warning "Response caching may not be working as expected"
    fi
}

# Test circuit breaker (requires stopping a service)
test_circuit_breaker() {
    print_status "Testing circuit breaker..."
    print_warning "Circuit breaker test requires manual service shutdown"
    print_status "To test circuit breaker:"
    print_status "1. Stop the users-service: docker stop crm-users-service-dev"
    print_status "2. Make requests to $GATEWAY_URL/api/v1/users"
    print_status "3. After several failures, you should see fallback responses"
    print_status "4. Restart the service: docker start crm-users-service-dev"
}

# Test response transformation
test_response_transformation() {
    print_status "Testing response transformation..."
    
    if [ -z "$JWT_TOKEN" ]; then
        print_error "JWT token not available for response transformation test"
        return 1
    fi
    
    local response=$(curl -s -H "Authorization: Bearer $JWT_TOKEN" \
        "$GATEWAY_URL/api/v1/users")
    
    # Check if response contains metadata
    if echo "$response" | grep -q "_metadata"; then
        print_success "Response transformation is adding metadata"
    else
        print_warning "Response transformation metadata not found"
    fi
    
    # Check if passwords are removed (if any user data is returned)
    if echo "$response" | grep -q "password"; then
        print_error "Response transformation is not removing passwords"
    else
        print_success "Response transformation is properly removing sensitive data"
    fi
}

# Test load balancing (requires multiple service instances)
test_load_balancing() {
    print_status "Testing load balancing..."
    print_warning "Load balancing test requires multiple service instances"
    print_status "To test load balancing:"
    print_status "1. Scale up users-service: docker-compose up --scale users-service=2"
    print_status "2. Make multiple requests and check service instance responses"
    print_status "3. Requests should be distributed across instances"
}

# Main test execution
main() {
    print_status "Starting API Gateway comprehensive test suite..."
    echo ""
    
    # Wait for services to be ready
    wait_for_service "$GATEWAY_URL" "API Gateway" || exit 1
    wait_for_service "$AUTH_SERVICE_URL" "Auth Service" || exit 1
    wait_for_service "$USERS_SERVICE_URL" "Users Service" || exit 1
    
    echo ""
    
    # Run tests
    local test_failures=0
    
    test_connectivity || ((test_failures++))
    echo ""
    
    test_authentication || ((test_failures++))
    echo ""
    
    test_rate_limiting || ((test_failures++))
    echo ""
    
    test_caching || ((test_failures++))
    echo ""
    
    test_response_transformation || ((test_failures++))
    echo ""
    
    test_circuit_breaker
    echo ""
    
    test_load_balancing
    echo ""
    
    # Summary
    if [ $test_failures -eq 0 ]; then
        print_success "All automated tests passed! 🎉"
        print_status "Manual tests for circuit breaker and load balancing are available"
    else
        print_error "$test_failures test(s) failed"
        exit 1
    fi
    
    print_status "API Gateway test suite completed"
}

# Run main function
main "$@"