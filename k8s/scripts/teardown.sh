#!/bin/bash

echo "🧹 Cleaning up CRM Microservices from Minikube..."

# Delete all resources in the namespace
echo "🗑️  Deleting all resources..."
kubectl delete namespace crm-microservices

# Delete persistent volumes
echo "💾 Cleaning up persistent volumes..."
kubectl delete pv postgres-pv redis-pv --ignore-not-found=true

# Clean up local storage directories
echo "🧹 Cleaning up local storage..."
sudo rm -rf /tmp/postgres-data /tmp/redis-data 2>/dev/null || true

echo "✅ Cleanup complete!"
echo ""
echo "💡 To completely reset Minikube, run: minikube delete"
echo "💡 To stop Minikube, run: minikube stop"
