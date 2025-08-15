-- Create collection tables for users-service
-- These tables store user-related collections (working days, skills, certifications, languages)

-- User working days table
CREATE TABLE user_working_days (
    user_id UUID NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    PRIMARY KEY (user_id, day_of_week)
);

-- User skills table
CREATE TABLE user_skills (
    user_id UUID NOT NULL,
    skill VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, skill)
);

-- User certifications table
CREATE TABLE user_certifications (
    user_id UUID NOT NULL,
    certification VARCHAR(200) NOT NULL,
    PRIMARY KEY (user_id, certification)
);

-- User languages table
CREATE TABLE user_languages (
    user_id UUID NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    PRIMARY KEY (user_id, language_code)
);

-- Add foreign key constraints
ALTER TABLE user_working_days ADD CONSTRAINT fk_user_working_days_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_skills ADD CONSTRAINT fk_user_skills_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_certifications ADD CONSTRAINT fk_user_certifications_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_languages ADD CONSTRAINT fk_user_languages_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add constraints for valid values
ALTER TABLE user_working_days ADD CONSTRAINT chk_day_of_week_valid 
    CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));

-- Create indexes for performance
CREATE INDEX idx_user_working_days_user_id ON user_working_days(user_id);
CREATE INDEX idx_user_skills_user_id ON user_skills(user_id);
CREATE INDEX idx_user_certifications_user_id ON user_certifications(user_id);
CREATE INDEX idx_user_languages_user_id ON user_languages(user_id);

-- Add comments for documentation
COMMENT ON TABLE user_working_days IS 'Stores user working days of the week';
COMMENT ON TABLE user_skills IS 'Stores user skills and competencies';
COMMENT ON TABLE user_certifications IS 'Stores user certifications and qualifications';
COMMENT ON TABLE user_languages IS 'Stores user spoken languages';