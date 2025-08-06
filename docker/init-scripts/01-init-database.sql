-- Initialize CRM Database
-- This script runs when the PostgreSQL container starts for the first time

-- The user_crm user is automatically created by Docker with POSTGRES_USER environment variable

-- Create additional schemas if needed
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS users;

-- Grant permissions to user_crm (who is the database owner)
GRANT ALL PRIVILEGES ON SCHEMA auth TO user_crm;
GRANT ALL PRIVILEGES ON SCHEMA users TO user_crm;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auth TO user_crm;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA users TO user_crm;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auth TO user_crm;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA users TO user_crm;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON TABLES TO user_crm;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON SEQUENCES TO user_crm;
ALTER DEFAULT PRIVILEGES IN SCHEMA users GRANT ALL ON TABLES TO user_crm;
ALTER DEFAULT PRIVILEGES IN SCHEMA users GRANT ALL ON SEQUENCES TO user_crm;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log initialization completion
\echo 'Database initialization completed successfully'