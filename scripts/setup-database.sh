#!/bin/bash

# Setup Database for CRM Microservices
# This script initializes the PostgreSQL database with necessary users and databases

echo "Setting up CRM Microservices Database..."

# Check if PostgreSQL container is running
if ! docker ps | grep -q "crm-postgres"; then
    echo "PostgreSQL container is not running. Starting it first..."
    docker compose up postgres -d
    echo "Waiting for PostgreSQL to be ready..."
    sleep 10
fi

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
until docker exec crm-postgres pg_isready -U user_crm -d db_crm; do
    echo "PostgreSQL is not ready yet. Waiting..."
    sleep 2
done

echo "PostgreSQL is ready. Setting up database..."

# Run the initialization script
echo "Running database initialization script..."
docker exec -i crm-postgres psql -U user_crm -d db_crm < scripts/init-database.sql

echo "Database setup completed!"
echo ""
echo "Created databases:"
echo "  - auth_service_db (user: auth_user)"
echo "  - users_service_db (user: users_user)"
echo "  - tenant_service_db (user: tenant_user)"
echo ""
echo "You can now start your services."
