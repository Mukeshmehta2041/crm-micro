-- Initialize CRM Microservices Database
-- This script creates the necessary databases and users for the CRM microservices

-- Create databases for each service
CREATE DATABASE auth_service_db;
CREATE DATABASE users_service_db;
CREATE DATABASE tenant_service_db;

-- Create users for each service
CREATE USER auth_user WITH PASSWORD 'auth_password';
CREATE USER users_user WITH PASSWORD 'users_password';
CREATE USER tenant_user WITH PASSWORD 'tenant_password';

-- Grant privileges to auth_user
GRANT ALL PRIVILEGES ON DATABASE auth_service_db TO auth_user;
GRANT CONNECT ON DATABASE auth_service_db TO auth_user;
GRANT CREATE ON DATABASE auth_service_db TO auth_user;

-- Grant privileges to users_user
GRANT ALL PRIVILEGES ON DATABASE users_service_db TO users_user;
GRANT CONNECT ON DATABASE users_service_db TO users_user;
GRANT CREATE ON DATABASE users_service_db TO users_user;

-- Grant privileges to tenant_user
GRANT ALL PRIVILEGES ON DATABASE tenant_service_db TO tenant_user;
GRANT CONNECT ON DATABASE tenant_service_db TO tenant_user;
GRANT CREATE ON DATABASE tenant_service_db TO tenant_user;

-- Connect to auth_service_db and set up permissions
\c auth_service_db;

-- Grant schema permissions to auth_user
GRANT USAGE ON SCHEMA public TO auth_user;
GRANT CREATE ON SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO auth_user;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO auth_user;

-- Connect to users_service_db and set up permissions
\c users_service_db;

-- Grant schema permissions to users_user
GRANT USAGE ON SCHEMA public TO users_user;
GRANT CREATE ON SCHEMA public TO users_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO users_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO users_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO users_user;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO users_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO users_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO users_user;

-- Connect to tenant_service_db and set up permissions
\c tenant_service_db;

-- Grant schema permissions to tenant_user
GRANT USAGE ON SCHEMA public TO tenant_user;
GRANT CREATE ON SCHEMA public TO tenant_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tenant_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tenant_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO tenant_user;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO tenant_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO tenant_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO tenant_user;

-- Connect back to main database for verification
\c db_crm;

-- Create extensions that might be needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Verify the setup
\du
\l
