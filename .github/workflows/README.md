# GitHub Actions Workflow Documentation

## Overview

This workflow automates the complete CI/CD pipeline for the CRM Microservices project, including:
1. **JAR Building** - Compiles all services using Maven
2. **Docker Image Building** - Creates Docker images from compiled JARs
3. **Image Pushing** - Pushes images to Docker registry (optional)
4. **Security Scanning** - Scans images for vulnerabilities
5. **Deployment** - Deploys to staging/production environments

## Workflow Triggers

### Automatic Triggers
- **Push** to `main`, `develop`, or `feature/*` branches
- **Pull Requests** to `main` or `develop` branches
- **Tags** starting with `v*` (e.g., `v1.0.0`)

### Manual Triggers
- **Workflow Dispatch** - Manual execution with custom parameters

## Workflow Jobs

### 1. Build JAR Files (`build-jars`)
- **Matrix Strategy**: Builds all 5 services in parallel
- **Java Version**: JDK 17 (Temurin distribution)
- **Maven**: Uses Maven wrapper or system Maven
- **Caching**: Maven packages and dependencies
- **Artifacts**: Uploads JAR files for Docker build step

### 2. Build Docker Images (`build-docker-images`)
- **Dependencies**: Requires successful JAR builds
- **Matrix Strategy**: Builds Docker images for all services
- **Multi-platform**: Supports `linux/amd64` and `linux/arm64`
- **Caching**: GitHub Actions cache for faster builds
- **Conditional Push**: Only pushes if `PUSH_IMAGES=true`

### 3. Security Scan (`security-scan`)
- **Tool**: Trivy vulnerability scanner
- **Output**: SARIF format for GitHub Security tab
- **Condition**: Only runs when pushing images

### 4. Deploy to Staging (`deploy-staging`)
- **Trigger**: Only on `develop` branch when pushing images
- **Environment**: `staging` (requires environment setup)

### 5. Deploy to Production (`deploy-production`)
- **Trigger**: Only on version tags (e.g., `v1.0.0`)
- **Environment**: `production` (requires environment setup)

### 6. Build Summary (`summary`)
- **Always Runs**: Provides comprehensive build status
- **Output**: GitHub Step Summary with build details

## Configuration

### Environment Variables
```yaml
REGISTRY: Docker registry (default: ghcr.io)
IMAGE_TAG: Image tag (default: latest)
PUSH_IMAGES: Whether to push images (default: false)
```

### Secrets Required
- `GITHUB_TOKEN`: Automatically provided by GitHub
- Custom registry credentials (if using external registry)

## Usage Examples

### 1. Manual Execution with Custom Parameters
```bash
# Navigate to Actions tab in GitHub repository
# Click "Build and Deploy CRM Microservices"
# Fill in parameters:
# - Registry: docker.io/mukeshkr1234 (default)
# - Image Tag: v1.2.3
# - Push Images: true
```

### 2. Tag-based Release
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0

# This automatically triggers:
# - JAR builds
# - Docker image builds
# - Image pushing to registry
# - Security scanning
# - Production deployment
```

### 3. Feature Branch Development
```bash
# Push to feature branch
git push origin feature/new-feature

# This triggers:
# - JAR builds
# - Docker image builds (local only)
# - No pushing or deployment
```

## Customization

### Adding New Services
1. Add service name to the `matrix.service` array in both jobs
2. Ensure the service has a `Dockerfile`
3. Verify Maven configuration

### Custom Registry
```yaml
# For Docker Hub
REGISTRY: docker.io/yourusername

# For AWS ECR
REGISTRY: your-account.dkr.ecr.region.amazonaws.com

# For Google GCR
REGISTRY: gcr.io/your-project
```

### Deployment Logic
The workflow includes placeholder deployment steps. Customize them for your infrastructure:

```yaml
# Example: Kubernetes deployment
- name: Deploy to Kubernetes
  run: |
    kubectl apply -f k8s/
    kubectl rollout restart deployment/crm-services

# Example: Docker Compose
- name: Deploy with Docker Compose
  run: |
    docker-compose -f docker-compose.prod.yml up -d
```

## Best Practices

### 1. Branch Protection
- Protect `main` and `develop` branches
- Require status checks to pass
- Require pull request reviews

### 2. Environment Protection
- Set up `staging` and `production` environments
- Add required reviewers for production deployments
- Use environment secrets for sensitive data

### 3. Image Tagging Strategy
- Use semantic versioning for releases
- Include branch names for development builds
- Add commit SHA for traceability

### 4. Security
- Regularly update base images
- Monitor vulnerability scan results
- Use minimal base images when possible

## Troubleshooting

### Common Issues

1. **JAR Build Failures**
   - Check Maven configuration
   - Verify Java version compatibility
   - Check for dependency issues

2. **Docker Build Failures**
   - Ensure Dockerfile exists
   - Check for missing JAR files
   - Verify Docker context

3. **Registry Push Failures**
   - Verify registry credentials
   - Check network connectivity
   - Ensure proper permissions

4. **Deployment Failures**
   - Verify environment configuration
   - Check deployment scripts
   - Ensure target environment is accessible

### Debug Mode
Enable debug logging by adding this secret to your repository:
```
ACTIONS_STEP_DEBUG: true
```

## Support

For issues or questions:
1. Check GitHub Actions logs
2. Review workflow configuration
3. Verify environment setup
4. Check required secrets and permissions
