-- Set the search path to the app schema
SET search_path TO app;

-- Drop tables
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS RSVP;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS online_venue;
DROP TABLE IF EXISTS physical_venue;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS funding_application;
DROP TABLE IF EXISTS admin_role;
DROP TABLE IF EXISTS club;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS faculty_member;
DROP TABLE IF EXISTS refresh_token;

-- Drop the custom enum type
DROP TYPE IF EXISTS status_enum;
DROP TYPE IF EXISTS decision_enum;

-- Optionally, drop the entire schema (uncomment if needed)
-- DROP SCHEMA IF EXISTS app CASCADE;
