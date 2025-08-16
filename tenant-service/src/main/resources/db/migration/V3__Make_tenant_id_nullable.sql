-- Migration to make tenant_id nullable in tenants table
-- This allows the Tenant entity to be created without a tenantId initially
-- The database trigger will automatically set it to the same value as id

-- Make tenant_id column nullable
ALTER TABLE tenants ALTER COLUMN tenant_id DROP NOT NULL;

-- Update the trigger function to handle the case where tenant_id is null
CREATE OR REPLACE FUNCTION set_tenant_id()
RETURNS TRIGGER AS $$
BEGIN
    -- For Tenant entity, tenant_id should reference itself (same as id)
    IF NEW.tenant_id IS NULL THEN
        NEW.tenant_id = NEW.id;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Drop and recreate the trigger to ensure it uses the updated function
DROP TRIGGER IF EXISTS set_tenant_id_trigger ON tenants;
CREATE TRIGGER set_tenant_id_trigger 
    BEFORE INSERT ON tenants 
    FOR EACH ROW EXECUTE FUNCTION set_tenant_id();

-- Add comment explaining the change
COMMENT ON COLUMN tenants.tenant_id IS 'Self-referencing tenant identifier for consistency. Can be null during creation and will be auto-set to id.';
