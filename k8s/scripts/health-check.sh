#!/bin/bash

echo "🏥 Health Check for CRM Microservices"
echo "======================================"

# Check namespace
echo ""
echo "📦 Namespace Status:"
kubectl get namespace crm-microservices

# Check all resources
echo ""
echo "🔍 All Resources:"
kubectl get all -n crm-microservices

# Check pod status
echo ""
echo "📊 Pod Status:"
kubectl get pods -n crm-microservices -o wide

# Check services
echo ""
echo "🌐 Services:"
kubectl get svc -n crm-microservices

# Check endpoints
echo ""
echo "📍 Endpoints:"
kubectl get endpoints -n crm-microservices

# Check persistent volumes
echo ""
echo "💾 Storage:"
kubectl get pv,pvc -n crm-microservices

# Check HPA
echo ""
echo "📈 Horizontal Pod Autoscalers:"
kubectl get hpa -n crm-microservices

# Check ingress
echo ""
echo "🛣️  Ingress:"
kubectl get ingress -n crm-microservices

# Check events
echo ""
echo "📝 Recent Events:"
kubectl get events -n crm-microservices --sort-by='.lastTimestamp' | tail -10

# Check resource usage
echo ""
echo "💻 Resource Usage:"
kubectl top pods -n crm-microservices 2>/dev/null || echo "Metrics server not available"

echo ""
echo "✅ Health check complete!"
echo ""
echo "💡 For detailed logs: kubectl logs -f deployment/[service-name] -n crm-microservices"
echo "💡 For service access: kubectl port-forward svc/[service-name] 8080:8080 -n crm-microservices"
