-- Create users table for users-service
-- This table stores user profile information and personal details

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(200),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    work_phone VARCHAR(20),
    mobile_phone VARCHAR(20),
    birth_date DATE,
    hire_date DATE,
    gender VARCHAR(10),
    job_title VARCHAR(150),
    department VARCHAR(100),
    company VARCHAR(100),
    employee_id VARCHAR(50),
    cost_center VARCHAR(100),
    office_location VARCHAR(200),
    profile_image_url VARCHAR(500),
    bio TEXT,
    website_url VARCHAR(500),
    linkedin_url VARCHAR(500),
    twitter_handle VARCHAR(100),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    timezone VARCHAR(50) DEFAULT 'UTC',
    language VARCHAR(10) DEFAULT 'en',
    date_format VARCHAR(20) DEFAULT 'MM/dd/yyyy',
    time_format VARCHAR(10) DEFAULT '12h',
    theme_preference VARCHAR(20) DEFAULT 'light',
    currency_preference VARCHAR(10) DEFAULT 'USD',
    manager_id UUID,
    team_id UUID,
    last_activity_at TIMESTAMP,
    last_login_at TIMESTAMP,
    login_count BIGINT DEFAULT 0,
    failed_login_attempts INTEGER DEFAULT 0 CHECK (failed_login_attempts >= 0 AND failed_login_attempts <= 10),
    account_locked_until TIMESTAMP,
    last_password_change_at TIMESTAMP,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    onboarding_step INTEGER DEFAULT 0 CHECK (onboarding_step >= 0),
    onboarding_completed_at TIMESTAMP,
    email_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    push_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sms_notifications_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    profile_visibility VARCHAR(20) DEFAULT 'TEAM',
    activity_visibility VARCHAR(20) DEFAULT 'TEAM',
    email_visibility VARCHAR(20) DEFAULT 'TEAM',
    phone_visibility VARCHAR(20) DEFAULT 'TEAM',
    working_hours_start TIME,
    working_hours_end TIME,
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relationship VARCHAR(100),
    custom_fields TEXT,
    gdpr_consent_given BOOLEAN NOT NULL DEFAULT FALSE,
    gdpr_consent_date TIMESTAMP,
    marketing_consent_given BOOLEAN NOT NULL DEFAULT FALSE,
    marketing_consent_date TIMESTAMP,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance optimization
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE UNIQUE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_tenant_id ON users(tenant_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_department ON users(department);
CREATE INDEX idx_users_company ON users(company);
CREATE INDEX idx_users_manager_id ON users(manager_id);
CREATE INDEX idx_users_team_id ON users(team_id);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_users_last_activity ON users(last_activity_at);

-- Add constraints
ALTER TABLE users ADD CONSTRAINT chk_username_format 
    CHECK (username ~ '^[a-zA-Z0-9._-]+$');

ALTER TABLE users ADD CONSTRAINT chk_email_format 
    CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE users ADD CONSTRAINT chk_first_name_format 
    CHECK (first_name ~ '^[a-zA-Z\s''-]+$');

ALTER TABLE users ADD CONSTRAINT chk_last_name_format 
    CHECK (last_name ~ '^[a-zA-Z\s''-]+$');

ALTER TABLE users ADD CONSTRAINT chk_phone_format 
    CHECK (phone_number IS NULL OR phone_number ~ '^[+]?[0-9\s()-]+$');

ALTER TABLE users ADD CONSTRAINT chk_work_phone_format 
    CHECK (work_phone IS NULL OR work_phone ~ '^[+]?[0-9\s()-]+$');

ALTER TABLE users ADD CONSTRAINT chk_mobile_phone_format 
    CHECK (mobile_phone IS NULL OR mobile_phone ~ '^[+]?[0-9\s()-]+$');

ALTER TABLE users ADD CONSTRAINT chk_emergency_phone_format 
    CHECK (emergency_contact_phone IS NULL OR emergency_contact_phone ~ '^[+]?[0-9\s()-]+$');

ALTER TABLE users ADD CONSTRAINT chk_birth_date_past 
    CHECK (birth_date IS NULL OR birth_date < CURRENT_DATE);

ALTER TABLE users ADD CONSTRAINT chk_gender_valid 
    CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY'));

ALTER TABLE users ADD CONSTRAINT chk_status_valid 
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING', 'DELETED'));

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE users IS 'Stores user profile information and personal details';
COMMENT ON COLUMN users.id IS 'Primary key using UUID';
COMMENT ON COLUMN users.tenant_id IS 'Multi-tenant identifier';
COMMENT ON COLUMN users.username IS 'Unique username for authentication';
COMMENT ON COLUMN users.email IS 'User email address';
COMMENT ON COLUMN users.status IS 'Current user account status';
COMMENT ON COLUMN users.manager_id IS 'Reference to user manager for hierarchy';
COMMENT ON COLUMN users.team_id IS 'Reference to user team';
COMMENT ON COLUMN users.deleted_at IS 'Soft delete timestamp';