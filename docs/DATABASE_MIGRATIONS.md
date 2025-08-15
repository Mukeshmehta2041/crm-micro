# Database Migrations Guide

This document provides comprehensive information about database migrations in the CRM microservices system using Flyway.

## Overview

The CRM system uses Flyway for database version control and migrations. Each service has its own database and migration scripts to ensure data consistency and schema evolution.

## Services and Databases

| Service | Database | Port | User | Migration Path |
|---------|----------|------|------|----------------|
| auth-service | auth_service_db | 8081 | auth_user | `auth-service/src/main/resources/db/migration` |
| tenant-service | tenant_service_db | 8082 | tenant_user | `tenant-service/src/main/resources/db/migration` |
| users-service | users_service_db | 8083 | users_user | `users-service/src/main/resources/db/migration` |

## Prerequisites

1. **PostgreSQL 12+** installed and running
2. **Maven 3.6+** for building and running migrations
3. **Java 21** for running the services

## Database Setup

### 1. Create Databases and Users

Run the database setup script as PostgreSQL superuser:

```bash
# Connect to PostgreSQL as superuser
psql -U postgres -h localhost

# Run the setup script
\i scripts/setup-databases.sql
```

Or manually create databases:

```sql
-- Create databases
CREATE DATABASE auth_service_db;
CREATE DATABASE tenant_service_db;
CREATE DATABASE users_service_db;

-- Create users
CREATE USER auth_user WITH PASSWORD 'auth_password';
CREATE USER tenant_user WITH PASSWORD 'tenant_password';
CREATE USER users_user WITH PASSWORD 'users_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE auth_service_db TO auth_user;
GRANT ALL PRIVILEGES ON DATABASE tenant_service_db TO tenant_user;
GRANT ALL PRIVILEGES ON DATABASE users_service_db TO users_user;
```

### 2. Environment Variables (Optional)

You can override database connection settings using environment variables:

```bash
# Auth Service
export AUTH_DB_URL=jdbc:postgresql://localhost:5432/auth_service_db
export AUTH_DB_USER=auth_user
export AUTH_DB_PASSWORD=auth_password

# Tenant Service
export TENANT_DB_URL=jdbc:postgresql://localhost:5432/tenant_service_db
export TENANT_DB_USER=tenant_user
export TENANT_DB_PASSWORD=tenant_password

# Users Service
export USERS_DB_URL=jdbc:postgresql://localhost:5432/users_service_db
export USERS_DB_USER=users_user
export USERS_DB_PASSWORD=users_password
```

## Migration Scripts

### Auth Service Migrations

#### V1__Create_user_credentials_table.sql
- Creates the `user_credentials` table
- Stores authentication credentials and security settings
- Includes indexes for performance optimization
- Adds constraints for data validation
- Creates triggers for automatic timestamp updates

**Key Features:**
- UUID primary keys
- Multi-factor authentication support
- Account lockout mechanism
- Email and phone verification
- Trusted device management

### Tenant Service Migrations

#### V1__Create_tenants_table.sql
- Creates the `tenants` table
- Stores tenant organization information
- Includes subscription and billing details
- Supports trial and paid subscriptions

#### V2__Create_tenant_configurations_table.sql
- Creates the `tenant_configurations` table
- Stores tenant-specific configuration settings
- Supports different data types (string, integer, boolean, etc.)
- Includes encryption and system configuration flags

**Key Features:**
- Multi-tenant architecture support
- Flexible configuration system
- Subscription management
- Branding and customization

### Users Service Migrations

#### V1__Create_users_table.sql
- Creates the `users` table
- Stores comprehensive user profile information
- Includes personal, professional, and preference data
- Supports GDPR compliance features

#### V2__Create_user_collections_tables.sql
- Creates collection tables for user-related data
- `user_working_days` - Working schedule
- `user_skills` - User competencies
- `user_certifications` - Professional certifications
- `user_languages` - Spoken languages

**Key Features:**
- Comprehensive user profiles
- Privacy and visibility controls
- GDPR compliance
- Soft delete functionality
- Audit trail

## Running Migrations

### Automated Build and Migration

Use the provided script to build all services and run migrations:

```bash
./scripts/build-and-migrate.sh
```

This script will:
1. Check PostgreSQL connectivity
2. Build all services
3. Run database migrations
4. Package the services

### Manual Migration Commands

#### Run migrations for a specific service:

```bash
# Auth Service
cd auth-service
mvn flyway:migrate

# Tenant Service
cd tenant-service
mvn flyway:migrate

# Users Service
cd users-service
mvn flyway:migrate
```

#### Validate migrations:

```bash
# Validate all services
./scripts/validate-migrations.sh

# Or validate individual services
cd auth-service
mvn flyway:validate
mvn flyway:info
```

#### Other useful Flyway commands:

```bash
# Show migration information
mvn flyway:info

# Validate applied migrations
mvn flyway:validate

# Repair migration history (use with caution)
mvn flyway:repair

# Clean database (development only - disabled in production)
mvn flyway:clean
```

## Migration Best Practices

### 1. Naming Convention
- Use descriptive names: `V{version}__{description}.sql`
- Version format: `V1`, `V2`, `V1_1`, etc.
- Examples:
  - `V1__Create_users_table.sql`
  - `V2__Add_email_verification_column.sql`
  - `V2_1__Fix_email_constraint.sql`

### 2. Migration Content
- **Always** include rollback considerations
- Use transactions for data migrations
- Add proper indexes for performance
- Include constraints for data integrity
- Add comments for documentation

### 3. Testing
- Test migrations on a copy of production data
- Validate migration performance on large datasets
- Ensure migrations are idempotent where possible

### 4. Production Deployment
- Always backup database before migration
- Run migrations during maintenance windows
- Monitor migration progress and performance
- Have rollback plan ready

## Troubleshooting

### Common Issues

#### 1. Connection Refused
```
Error: Connection refused. Check that the hostname and port are correct
```
**Solution:** Ensure PostgreSQL is running and accessible

#### 2. Authentication Failed
```
Error: FATAL: password authentication failed for user "auth_user"
```
**Solution:** Verify user credentials and database permissions

#### 3. Migration Checksum Mismatch
```
Error: Migration checksum mismatch for migration version 1
```
**Solution:** Use `mvn flyway:repair` to fix checksums (development only)

#### 4. Pending Migrations
```
Warning: Database has pending migrations
```
**Solution:** Run `mvn flyway:migrate` to apply pending migrations

### Debugging

Enable debug logging in `application.yml`:

```yaml
logging:
  level:
    org.flywaydb: DEBUG
    org.hibernate.SQL: DEBUG
```

## Security Considerations

1. **Database Credentials**: Use environment variables or secure vaults in production
2. **Migration Scripts**: Review all scripts for security vulnerabilities
3. **Permissions**: Use least-privilege principle for database users
4. **Backup**: Always backup before running migrations in production

## Monitoring

Monitor migration status using:

1. **Flyway Schema History Table**: `flyway_schema_history`
2. **Application Logs**: Check for migration errors
3. **Health Endpoints**: `/actuator/health` includes database status
4. **Metrics**: Database connection pool metrics via `/actuator/metrics`

## Development Workflow

1. **Create Migration**: Add new `.sql` file in `src/main/resources/db/migration`
2. **Test Locally**: Run migration on local database
3. **Validate**: Use validation script to check migration
4. **Code Review**: Review migration script with team
5. **Deploy**: Apply migration in target environment

## Production Checklist

- [ ] Database backup completed
- [ ] Migration tested on staging environment
- [ ] Performance impact assessed
- [ ] Rollback plan prepared
- [ ] Maintenance window scheduled
- [ ] Team notified
- [ ] Monitoring alerts configured

## Support

For issues or questions regarding database migrations:

1. Check this documentation
2. Review Flyway logs
3. Validate database connectivity
4. Contact the development team

---

**Note**: This guide assumes PostgreSQL as the database. Adjust connection strings and drivers if using a different database system.