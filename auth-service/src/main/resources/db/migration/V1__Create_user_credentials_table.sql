-- Create user_credentials table for auth-service
-- This table stores authentication credentials and security settings for users

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE user_credentials (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    last_login_at TIMESTAMP,
    password_changed_at TIMESTAMP,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0 CHECK (failed_login_attempts >= 0 AND failed_login_attempts <= 10),
    account_locked_until TIMESTAMP,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(32),
    backup_codes TEXT,
    mfa_method VARCHAR(20),
    trusted_devices TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance optimization
CREATE INDEX idx_user_credentials_username ON user_credentials(username);
CREATE INDEX idx_user_credentials_email ON user_credentials(email);
CREATE INDEX idx_user_credentials_user_id ON user_credentials(user_id);
CREATE INDEX idx_user_credentials_tenant_id ON user_credentials(tenant_id);
CREATE INDEX idx_user_credentials_email_verified ON user_credentials(email_verified);
CREATE INDEX idx_user_credentials_mfa_enabled ON user_credentials(mfa_enabled);
CREATE INDEX idx_user_credentials_account_locked ON user_credentials(account_locked_until);

-- Add constraints
ALTER TABLE user_credentials ADD CONSTRAINT chk_username_format 
    CHECK (username ~ '^[a-zA-Z0-9_-]+$');

ALTER TABLE user_credentials ADD CONSTRAINT chk_email_format 
    CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE user_credentials ADD CONSTRAINT chk_mfa_method_valid 
    CHECK (mfa_method IS NULL OR mfa_method IN ('TOTP', 'SMS', 'EMAIL', 'HARDWARE_KEY', 'PUSH_NOTIFICATION'));

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_credentials_updated_at 
    BEFORE UPDATE ON user_credentials 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE user_credentials IS 'Stores user authentication credentials and security settings';
COMMENT ON COLUMN user_credentials.id IS 'Primary key using UUID';
COMMENT ON COLUMN user_credentials.tenant_id IS 'Multi-tenant identifier';
COMMENT ON COLUMN user_credentials.user_id IS 'Reference to user profile';
COMMENT ON COLUMN user_credentials.username IS 'Unique username for authentication';
COMMENT ON COLUMN user_credentials.email IS 'User email address for authentication';
COMMENT ON COLUMN user_credentials.password_hash IS 'Securely hashed password';
COMMENT ON COLUMN user_credentials.failed_login_attempts IS 'Counter for failed login attempts';
COMMENT ON COLUMN user_credentials.account_locked_until IS 'Account lockout expiration timestamp';
COMMENT ON COLUMN user_credentials.mfa_enabled IS 'Multi-factor authentication enabled flag';
COMMENT ON COLUMN user_credentials.mfa_secret IS 'TOTP secret key for MFA';
COMMENT ON COLUMN user_credentials.backup_codes IS 'JSON array of backup recovery codes';
COMMENT ON COLUMN user_credentials.trusted_devices IS 'JSON array of trusted device fingerprints';