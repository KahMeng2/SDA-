-- Create the app schema and set authorization
CREATE SCHEMA IF NOT EXISTS app AUTHORIZATION brogammerbrigade_owner;

-- Set the search path to use the app schema
SET search_path TO app;

-- Create faculty_member table
CREATE TABLE faculty_member (
    id BIGSERIAL PRIMARY KEY,
    role TEXT NOT NULL,
    department TEXT NOT NULL,
    email TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    middle_name TEXT,
    date_of_birth DATE NOT NULL
);

-- Create student table
CREATE TABLE student (
    id BIGSERIAL PRIMARY KEY,
    email TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    middle_name TEXT,
    date_of_birth DATE NOT NULL
);

-- Create club table
CREATE TABLE club (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    balance DOUBLE PRECISION NOT NULL
);

-- Create admin_role table
CREATE TABLE admin_role (
    student_id BIGINT,
    club_id BIGINT,
    PRIMARY KEY (student_id, club_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (club_id) REFERENCES club(id) ON DELETE CASCADE
);

-- Create status_enum type
CREATE TYPE status_enum AS ENUM ('In Draft', 'Submitted', 'In Review', 'Approved', 'Rejected', 'Cancelled');

-- Create decision_enum type
CREATE TYPE decision_enum AS ENUM ('Approved', 'Rejected');

-- Create funding_application table
CREATE TABLE funding_application (
    id BIGSERIAL PRIMARY KEY,
    club_id BIGINT,
    description TEXT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    status status_enum NOT NULL,
    submitted_at DATE NOT NULL,
	semester INTEGER NOT NULL CHECK (semester IN (1, 2)),
	year INTEGER NOT NULL,
    FOREIGN KEY (club_id) REFERENCES club(id) ON DELETE CASCADE,
	UNIQUE (club_id, semester, year)
);

-- Create review table
CREATE TABLE review (
    faculty_id BIGINT,
    application_id BIGINT,
    review_start_date DATE NOT NULL,
	decision decision_enum NOT NULL,
    comments TEXT NOT NULL,
    PRIMARY KEY (faculty_id, application_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty_member(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES funding_application(id) ON DELETE CASCADE
);

-- Create address table
CREATE TABLE address (
    id BIGSERIAL PRIMARY KEY,
    address_line_1 TEXT NOT NULL,
    address_line_2 TEXT,
    city TEXT NOT NULL,
    state TEXT NOT NULL,
    country TEXT NOT NULL,
    postcode INT NOT NULL
);

-- Create physical_venue table
CREATE TABLE physical_venue (
    id BIGSERIAL PRIMARY KEY,
    description TEXT,
    cost DOUBLE PRECISION NOT NULL,
    venue_capacity INTEGER NOT NULL,
    address_id BIGINT,
    floor TEXT NOT NULL,
    room TEXT NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address(id)
);

-- Create online_venue table
CREATE TABLE online_venue (
    id BIGSERIAL PRIMARY KEY,
    description TEXT,
    cost DOUBLE PRECISION NOT NULL,
    venue_capacity INTEGER NOT NULL,
    link TEXT NOT NULL
);

-- Create event table
CREATE TABLE event (
    id BIGSERIAL PRIMARY KEY,
    club_id BIGINT,
    is_online BOOLEAN NOT NULL,
	is_cancelled BOOLEAN NOT NULL,
    physical_venue_id BIGINT,
    online_venue_id BIGINT,
    name TEXT NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    cost DOUBLE PRECISION NOT NULL,
    num_tickets INTEGER NOT NULL,
    event_capacity INTEGER NOT NULL,
    FOREIGN KEY (club_id) REFERENCES club(id) ON DELETE CASCADE,
    FOREIGN KEY (physical_venue_id) REFERENCES physical_venue(id),
    FOREIGN KEY (online_venue_id) REFERENCES online_venue(id)
);

-- Create RSVP table
CREATE TABLE RSVP (
    RSVP_student_id BIGINT,
    event_id BIGINT,
    date_created DATE NOT NULL,
    PRIMARY KEY (RSVP_student_id, event_id),
    FOREIGN KEY (RSVP_student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);

-- Create ticket table
CREATE TABLE ticket (
    RSVP_student_id BIGINT,
    event_id BIGINT,
    ticket_student_id BIGINT,
	special_preferences TEXT,
    PRIMARY KEY (RSVP_student_id, event_id, ticket_student_id),
    FOREIGN KEY (RSVP_student_id, event_id) REFERENCES RSVP(RSVP_student_id, event_id) ON DELETE CASCADE,
    FOREIGN KEY (ticket_student_id) REFERENCES student(id) ON DELETE CASCADE,
    UNIQUE (ticket_student_id, event_id)
);

-- Create token table
CREATE TABLE app.refresh_token (
    id uuid NOT NULL UNIQUE,
    token_id VARCHAR(44) NOT NULL,
    username VARCHAR(255) NOT NULL,
	expires TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX refresh_token_username
ON app.refresh_token (username);
