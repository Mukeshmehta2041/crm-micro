#!/bin/bash

echo "🚀 Deploying CRM Microservices using Kustomize..."

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install it first."
    exit 1
fi

# Check if minikube is running
if ! minikube status | grep -q "Running"; then
    echo "❌ Minikube is not running. Please start it first with: minikube start"
    exit 1
fi

# Check if kustomize is installed
if ! command -v kustomize &> /dev/null; then
    echo "📦 Installing Kustomize..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        brew install kustomize
    else
        # Linux
        curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh" | bash
        sudo mv kustomize /usr/local/bin/
    fi
fi

# Deploy using kustomize
echo "🔧 Applying Kustomization..."
kubectl apply -k .

# Wait for all pods to be ready
echo "⏳ Waiting for all pods to be ready..."
kubectl wait --for=condition=available --timeout=600s deployment/postgres -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/redis -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/service-registry -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/auth-service -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/tenant-service -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/users-service -n crm-microservices
kubectl wait --for=condition=available --timeout=600s deployment/api-gateway -n crm-microservices

echo "✅ Deployment complete!"
echo ""
echo "📊 Check status with: kubectl get all -n crm-microservices"
echo "🌐 Access dashboard with: minikube dashboard"
echo "🔍 View logs with: kubectl logs -f deployment/[service-name] -n crm-microservices"
