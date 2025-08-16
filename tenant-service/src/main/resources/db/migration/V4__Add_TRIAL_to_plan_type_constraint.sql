-- Migration to add TRIAL to the plan_type constraint
-- This allows the TRIAL plan type that exists in the PlanType enum

-- Drop the existing constraint
ALTER TABLE tenants DROP CONSTRAINT IF EXISTS chk_plan_type_valid;

-- Add the updated constraint that includes TRIAL
ALTER TABLE tenants ADD CONSTRAINT chk_plan_type_valid 
    CHECK (plan_type IN ('BASIC', 'STANDARD', 'PREMIUM', 'ENTERPRISE', 'TRIAL'));

-- Add comment explaining the change
COMMENT ON COLUMN tenants.plan_type IS 'Subscription plan type (BASIC, STANDARD, PREMIUM, ENTERPRISE, or TRIAL)';
