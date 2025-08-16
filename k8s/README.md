# Kubernetes Setup for CRM Microservices

This directory contains all the Kubernetes manifests and scripts needed to deploy the CRM microservices on Minikube.

## Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/) installed and running
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) installed
- [Docker](https://docs.docker.com/get-docker/) running
- [Helm](https://helm.sh/docs/intro/install/) (optional, for additional tools)

## Quick Start

1. **Start Minikube** (if not already running):
   ```bash
   minikube start --cpus=4 --memory=8192 --disk-size=20g --driver=docker
   ```

2. **Build and load Docker images**:
   ```bash
   chmod +x scripts/build-images.sh
   ./scripts/build-images.sh
   ```

3. **Deploy to Kubernetes**:
   ```bash
   chmod +x scripts/setup-minikube.sh
   ./scripts/setup-minikube.sh
   ```

## Architecture

The setup includes:

- **Infrastructure**: PostgreSQL, Redis
- **Core Services**: Service Registry, Auth Service, Tenant Service, Users Service
- **Gateway**: API Gateway with ingress routing
- **Monitoring**: Metrics server, health checks, HPA

## Directory Structure

```
k8s/
├── configmaps/          # Configuration maps
├── deployments/         # Application deployments
├── hpa/                # Horizontal Pod Autoscalers
├── ingress/            # Ingress resources
├── scripts/            # Setup and utility scripts
├── secrets/            # Kubernetes secrets
├── services/           # Service definitions
├── storage/            # Persistent volumes and claims
└── namespace.yaml      # Namespace definition
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Main entry point with routing |
| Auth Service | 8080 | Authentication and authorization |
| Tenant Service | 8080 | Tenant management |
| Users Service | 8080 | User management |
| Service Registry | 8761 | Service discovery |
| PostgreSQL | 5432 | Primary database |
| Redis | 6379 | Caching layer |

## Configuration

### Environment Variables

Services are configured via ConfigMaps and Secrets:

- **Database**: PostgreSQL connection details
- **Redis**: Cache configuration
- **JWT**: Authentication secrets
- **Profiles**: Spring profiles set to "kubernetes"

### Resource Limits

Each service has defined resource requests and limits:
- **CPU**: 250m-1000m
- **Memory**: 256Mi-1Gi

## Scaling

Horizontal Pod Autoscalers (HPA) are configured for:
- Auth Service: 2-10 replicas
- Tenant Service: 2-10 replicas
- Users Service: 2-10 replicas

Scaling is based on CPU (70%) and Memory (80%) utilization.

## Monitoring

### Health Checks
- **Liveness Probe**: `/actuator/health` endpoint
- **Readiness Probe**: Health check before traffic routing

### Metrics
- Metrics server enabled for resource monitoring
- Kubernetes dashboard accessible via `minikube dashboard`

## Access

### External Access
- API Gateway exposed via LoadBalancer service
- Ingress configured for path-based routing
- Host: `crm.local` (add to `/etc/hosts`)

### Internal Communication
- Services communicate via internal service names
- Service discovery via Eureka (Service Registry)

## Troubleshooting

### Common Issues

1. **Images not found**:
   ```bash
   # Check if images are loaded
   minikube image ls | grep crm
   
   # Rebuild and reload if needed
   ./scripts/build-images.sh
   ```

2. **Pods not starting**:
   ```bash
   # Check pod status
   kubectl get pods -n crm-microservices
   
   # Check logs
   kubectl logs -f deployment/[service-name] -n crm-microservices
   ```

3. **Services not accessible**:
   ```bash
   # Check service status
   kubectl get svc -n crm-microservices
   
   # Check endpoints
   kubectl get endpoints -n crm-microservices
   ```

### Useful Commands

```bash
# View all resources
kubectl get all -n crm-microservices

# Check pod logs
kubectl logs -f deployment/[service-name] -n crm-microservices

# Access service directly
kubectl port-forward svc/[service-name] 8080:8080 -n crm-microservices

# Check events
kubectl get events -n crm-microservices --sort-by='.lastTimestamp'

# Access Minikube dashboard
minikube dashboard
```

## Cleanup

To remove all resources:

```bash
chmod +x scripts/teardown.sh
./scripts/teardown.sh
```

To completely reset Minikube:

```bash
minikube delete
minikube start
```

## Customization

### Adding New Services

1. Create deployment manifest in `deployments/`
2. Create service manifest in `services/`
3. Add to ingress routing in `ingress/`
4. Update setup scripts

### Modifying Configuration

1. Update ConfigMaps in `configmaps/`
2. Update Secrets in `secrets/`
3. Redeploy affected services

### Scaling Configuration

1. Modify HPA manifests in `hpa/`
2. Adjust resource limits in deployments
3. Update replica counts as needed

## Security Notes

- Secrets are base64 encoded (not encrypted)
- For production, use proper secret management
- Consider network policies for service isolation
- Enable RBAC for production deployments

## Next Steps

- Implement proper secret management (HashiCorp Vault, AWS Secrets Manager)
- Add network policies for service-to-service communication
- Configure persistent storage with proper storage classes
- Implement monitoring and alerting (Prometheus, Grafana)
- Add CI/CD pipeline integration
- Configure backup and disaster recovery
