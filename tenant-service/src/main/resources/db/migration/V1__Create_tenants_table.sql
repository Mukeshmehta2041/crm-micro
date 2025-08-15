-- Create tenants table for tenant-service
-- This table stores tenant organization information and subscription details

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL, -- Self-referencing for consistency with BaseEntity
    name VARCHAR(255) NOT NULL,
    subdomain VARCHAR(100) NOT NULL UNIQUE,
    plan_type VARCHAR(50) NOT NULL DEFAULT 'BASIC',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    max_users INTEGER CHECK (max_users >= 1 AND max_users <= 10000) DEFAULT 10,
    max_storage_gb INTEGER CHECK (max_storage_gb >= 1 AND max_storage_gb <= 10000) DEFAULT 100,
    custom_domain VARCHAR(255),
    logo_url VARCHAR(500),
    primary_color VARCHAR(7),
    secondary_color VARCHAR(7),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    billing_email VARCHAR(255),
    subscription_expires_at TIMESTAMP,
    trial_ends_at TIMESTAMP,
    is_trial BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance optimization
CREATE UNIQUE INDEX idx_tenants_subdomain ON tenants(subdomain);
CREATE INDEX idx_tenants_status ON tenants(status);
CREATE INDEX idx_tenants_plan_type ON tenants(plan_type);
CREATE INDEX idx_tenants_custom_domain ON tenants(custom_domain);
CREATE INDEX idx_tenants_contact_email ON tenants(contact_email);
CREATE INDEX idx_tenants_tenant_id ON tenants(tenant_id);

-- Add constraints
ALTER TABLE tenants ADD CONSTRAINT chk_subdomain_format 
    CHECK (subdomain ~ '^[a-z0-9]([a-z0-9-]*[a-z0-9])?$');

ALTER TABLE tenants ADD CONSTRAINT chk_plan_type_valid 
    CHECK (plan_type IN ('BASIC', 'STANDARD', 'PREMIUM', 'ENTERPRISE'));

ALTER TABLE tenants ADD CONSTRAINT chk_status_valid 
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING', 'DELETED'));

ALTER TABLE tenants ADD CONSTRAINT chk_primary_color_format 
    CHECK (primary_color IS NULL OR primary_color ~ '^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$');

ALTER TABLE tenants ADD CONSTRAINT chk_secondary_color_format 
    CHECK (secondary_color IS NULL OR secondary_color ~ '^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$');

ALTER TABLE tenants ADD CONSTRAINT chk_contact_email_format 
    CHECK (contact_email IS NULL OR contact_email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE tenants ADD CONSTRAINT chk_billing_email_format 
    CHECK (billing_email IS NULL OR billing_email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE tenants ADD CONSTRAINT chk_phone_format 
    CHECK (contact_phone IS NULL OR contact_phone ~ '^[+]?[0-9\s()-]+$');

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_tenants_updated_at 
    BEFORE UPDATE ON tenants 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Set tenant_id to be the same as id for root tenant records
CREATE OR REPLACE FUNCTION set_tenant_id()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.tenant_id IS NULL THEN
        NEW.tenant_id = NEW.id;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER set_tenant_id_trigger 
    BEFORE INSERT ON tenants 
    FOR EACH ROW EXECUTE FUNCTION set_tenant_id();

-- Add comments for documentation
COMMENT ON TABLE tenants IS 'Stores tenant organization information and subscription details';
COMMENT ON COLUMN tenants.id IS 'Primary key using UUID';
COMMENT ON COLUMN tenants.tenant_id IS 'Self-referencing tenant identifier for consistency';
COMMENT ON COLUMN tenants.name IS 'Tenant organization name';
COMMENT ON COLUMN tenants.subdomain IS 'Unique subdomain for tenant access';
COMMENT ON COLUMN tenants.plan_type IS 'Subscription plan type';
COMMENT ON COLUMN tenants.status IS 'Current tenant status';
COMMENT ON COLUMN tenants.max_users IS 'Maximum number of users allowed';
COMMENT ON COLUMN tenants.max_storage_gb IS 'Maximum storage in GB allowed';
COMMENT ON COLUMN tenants.is_trial IS 'Whether tenant is in trial mode';