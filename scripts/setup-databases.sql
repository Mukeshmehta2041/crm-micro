-- Database setup script for CRM microservices
-- Run this script as PostgreSQL superuser to create databases and users

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

-- Connect to each database and grant schema privileges
\c auth_service_db;
GRANT ALL ON SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO auth_user;

\c tenant_service_db;
GRANT ALL ON SCHEMA public TO tenant_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tenant_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tenant_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO tenant_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO tenant_user;

\c users_service_db;
GRANT ALL ON SCHEMA public TO users_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO users_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO users_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO users_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO users_user;