-- Migration to add BASE role to the users table constraint
-- Execute this script on your PostgreSQL database

-- Drop the existing role check constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Add new check constraint that includes BASE role
ALTER TABLE users ADD CONSTRAINT users_role_check 
    CHECK (role IN ('BASE', 'ADMIN', 'NURSE', 'GENERALIST', 'SPECIALIST'));

-- Verify the constraint was added
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'users'::regclass 
AND conname = 'users_role_check';
