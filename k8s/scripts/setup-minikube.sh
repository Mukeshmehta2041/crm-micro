#!/bin/bash

echo "🚀 Setting up Minikube for CRM Microservices..."

# Check if minikube is installed
if ! command -v minikube &> /dev/null; then
    echo "❌ Minikube is not installed. Please install it first:"
    echo "   macOS: brew install minikube"
    echo "   Linux: https://minikube.sigs.k8s.io/docs/start/"
    exit 1
fi

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install it first:"
    echo "   macOS: brew install kubectl"
    echo "   Linux: https://kubernetes.io/docs/tasks/tools/install-kubectl/"
    exit 1
fi

# Start minikube if not running
if ! minikube status | grep -q "Running"; then
    echo "🔄 Starting Minikube..."
    minikube start --cpus=4 --memory=8192 --disk-size=20g --driver=docker
    
    if [ $? -ne 0 ]; then
        echo "❌ Failed to start Minikube"
        exit 1
    fi
else
    echo "✅ Minikube is already running"
fi

# Enable addons
echo "🔧 Enabling Minikube addons..."
minikube addons enable ingress
minikube addons enable metrics-server
minikube addons enable dashboard

# Wait for addons to be ready
echo "⏳ Waiting for addons to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/ingress-nginx-controller -n ingress-nginx
kubectl wait --for=condition=available --timeout=300s deployment/metrics-server -n kube-system

# Create namespace
echo "📦 Creating namespace..."
kubectl apply -f namespace.yaml

# Apply storage class
echo "💾 Setting up storage..."
kubectl apply -f storage/

# Apply configmaps and secrets
echo "🔐 Applying configuration..."
kubectl apply -f configmaps/
kubectl apply -f secrets/

# Apply services
echo "🌐 Applying services..."
kubectl apply -f services/

# Apply deployments
echo "🚀 Applying deployments..."
kubectl apply -f deployments/

# Apply HPA
echo "📈 Applying Horizontal Pod Autoscalers..."
kubectl apply -f hpa/

# Apply ingress
echo "🛣️  Applying ingress..."
kubectl apply -f ingress/

# Wait for all pods to be ready
echo "⏳ Waiting for all pods to be ready..."
kubectl wait --for=condition=available --timeout=600s deployment/postgres -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/redis -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/service-registry -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/auth-service -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/tenant-service -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/users-service -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/api-gateway -n crm-microservices

echo "✅ Setup complete!"
echo ""
echo "📊 Check status with: kubectl get all -n crm-microservices"
echo "🌐 Access dashboard with: minikube dashboard"
echo "🔍 View logs with: kubectl logs -f deployment/[service-name] -n crm-microservices"
echo ""
echo "📝 Note: You may need to build and push your Docker images first:"
echo "   docker build -t crm-[service-name]:latest ./[service-name]"
echo "   minikube image load crm-[service-name]:latest"
