-- Create tenant_configurations table for tenant-service
-- This table stores tenant-specific configuration settings

CREATE TABLE tenant_configurations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_value TEXT,
    config_type VARCHAR(50) NOT NULL DEFAULT 'STRING',
    category VARCHAR(100),
    description VARCHAR(500),
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    is_editable BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance optimization
CREATE INDEX idx_tenant_configs_tenant_id ON tenant_configurations(tenant_id);
CREATE INDEX idx_tenant_configs_config_key ON tenant_configurations(config_key);
CREATE INDEX idx_tenant_configs_category ON tenant_configurations(category);
CREATE INDEX idx_tenant_configs_is_system ON tenant_configurations(is_system);
CREATE UNIQUE INDEX idx_tenant_configs_tenant_key ON tenant_configurations(tenant_id, config_key);

-- Add constraints
ALTER TABLE tenant_configurations ADD CONSTRAINT chk_config_key_format 
    CHECK (config_key ~ '^[a-zA-Z0-9._-]+$');

ALTER TABLE tenant_configurations ADD CONSTRAINT chk_config_type_valid 
    CHECK (config_type IN ('STRING', 'INTEGER', 'BOOLEAN', 'DECIMAL', 'JSON', 'URL', 'EMAIL'));

-- Add foreign key constraint to tenants table
ALTER TABLE tenant_configurations ADD CONSTRAINT fk_tenant_configurations_tenant_id 
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_tenant_configurations_updated_at 
    BEFORE UPDATE ON tenant_configurations 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE tenant_configurations IS 'Stores tenant-specific configuration settings';
COMMENT ON COLUMN tenant_configurations.id IS 'Primary key using UUID';
COMMENT ON COLUMN tenant_configurations.tenant_id IS 'Reference to tenant';
COMMENT ON COLUMN tenant_configurations.config_key IS 'Configuration key identifier';
COMMENT ON COLUMN tenant_configurations.config_value IS 'Configuration value as text';
COMMENT ON COLUMN tenant_configurations.config_type IS 'Data type of the configuration value';
COMMENT ON COLUMN tenant_configurations.category IS 'Configuration category for grouping';
COMMENT ON COLUMN tenant_configurations.is_encrypted IS 'Whether the value is encrypted';
COMMENT ON COLUMN tenant_configurations.is_system IS 'Whether this is a system configuration';
COMMENT ON COLUMN tenant_configurations.is_editable IS 'Whether users can edit this configuration';