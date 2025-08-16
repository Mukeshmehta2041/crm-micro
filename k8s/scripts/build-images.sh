#!/bin/bash

echo "🐳 Building and loading Docker images for CRM Microservices..."

# Check if docker is running
if ! docker info &> /dev/null; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if minikube is running
if ! minikube status | grep -q "Running"; then
    echo "❌ Minikube is not running. Please start it first with: minikube start"
    exit 1
fi

# Build and load images
echo "🔨 Building service-registry..."
cd ../service-registry
docker build -t crm-service-registry:latest .
minikube image load crm-service-registry:latest

echo "🔨 Building auth-service..."
cd ../auth-service
docker build -t crm-auth-service:latest .
minikube image load crm-auth-service:latest

echo "🔨 Building tenant-service..."
cd ../tenant-service
docker build -t crm-tenant-service:latest .
minikube image load crm-tenant-service:latest

echo "🔨 Building users-service..."
cd ../users-service
docker build -t crm-users-service:latest .
minikube image load crm-users-service:latest

echo "🔨 Building api-gateway..."
cd ../api-gateway
docker build -t crm-api-gateway:latest .
minikube image load crm-api-gateway:latest

echo "✅ All images built and loaded successfully!"
echo ""
echo "📝 You can now run: ./k8s/scripts/setup-minikube.sh"
