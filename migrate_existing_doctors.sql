-- Script to migrate existing doctor data to new schema
-- Run this BEFORE dropping the specialist/generalist tables

BEGIN;

-- Update existing doctors with proper enum specialty values
-- Convert string specialties to enum values
UPDATE doctor SET specialty = 'GENERAL_PRACTICE' WHERE LOWER(specialty) LIKE '%general%' OR LOWER(specialty) LIKE '%practice%';
UPDATE doctor SET specialty = 'CARDIOLOGY' WHERE LOWER(specialty) LIKE '%cardio%';
UPDATE doctor SET specialty = 'DERMATOLOGY' WHERE LOWER(specialty) LIKE '%derma%';
UPDATE doctor SET specialty = 'NEUROLOGY' WHERE LOWER(specialty) LIKE '%neuro%';

-- Set default specialty for doctors without one
UPDATE doctor SET specialty = 'CARDIOLOGY' WHERE specialty IS NULL OR specialty = 'Specialist';

-- Ensure doctor_type is set correctly based on specialty
UPDATE doctor SET doctor_type = 'GENERALIST' WHERE specialty = 'GENERAL_PRACTICE';
UPDATE doctor SET doctor_type = 'SPECIALIST' WHERE specialty != 'GENERAL_PRACTICE';

COMMIT;

-- Verification
SELECT id, phone, specialty, doctor_type FROM doctor ORDER BY id;
