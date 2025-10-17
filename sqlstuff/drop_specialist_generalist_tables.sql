-- Migration script to drop generalist and specialist tables
-- and update foreign keys to reference doctor table directly

BEGIN;

-- Step 1: Update foreign key constraints in consultations table
ALTER TABLE consultations 
    DROP CONSTRAINT IF EXISTS fk9y6t4o3roq1davd8rvg0e3gpp;

ALTER TABLE consultations 
    ADD CONSTRAINT fk_consultations_generalist_id 
    FOREIGN KEY (generalist_id) REFERENCES doctor(id);

-- Step 2: Update foreign key constraints in expertise_requests table
ALTER TABLE expertise_requests 
    DROP CONSTRAINT IF EXISTS fkn9hemq5lhcayixknv1qs85oey;

ALTER TABLE expertise_requests 
    ADD CONSTRAINT fk_expertise_requests_specialist_id 
    FOREIGN KEY (specialist_id) REFERENCES doctor(id);

-- Step 3: Update foreign key constraints in specialist_profiles table
ALTER TABLE specialist_profiles 
    DROP CONSTRAINT IF EXISTS fk8l1217e4dael7qa8f7b0xfc8i;

ALTER TABLE specialist_profiles 
    ADD CONSTRAINT fk_specialist_profiles_specialist_id 
    FOREIGN KEY (specialist_id) REFERENCES doctor(id);

-- Step 4: Drop the generalist table (no longer needed)
DROP TABLE IF EXISTS generalist CASCADE;

-- Step 5: Drop the specialist table (no longer needed)
DROP TABLE IF EXISTS specialist CASCADE;

-- Step 6: Make doctor_type NOT NULL (ensure all doctors have a type)
UPDATE doctor SET doctor_type = 'GENERALIST' WHERE doctor_type IS NULL AND specialty = 'GENERAL_PRACTICE';
UPDATE doctor SET doctor_type = 'SPECIALIST' WHERE doctor_type IS NULL AND specialty != 'GENERAL_PRACTICE';

ALTER TABLE doctor ALTER COLUMN doctor_type SET NOT NULL;

COMMIT;

-- Verification queries
SELECT 'Doctors count:' as info, COUNT(*) as count FROM doctor;
SELECT 'Doctor types distribution:' as info, doctor_type, COUNT(*) as count FROM doctor GROUP BY doctor_type;
SELECT 'Consultations count:' as info, COUNT(*) as count FROM consultations;
SELECT 'Expertise requests count:' as info, COUNT(*) as count FROM expertise_requests;
