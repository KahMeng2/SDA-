BEGIN;

CREATE SCHEMA IF NOT EXISTS app AUTHORIZATION brogammerbrigade_owner;

CREATE TABLE app.club (
 id uuid NOT NULL UNIQUE,  
 name varchar(255),  
 balance integer NOT NULL,  
 admin varchar(255) NOT NULL,  
 created timestamp with time zone NOT NULL,  
 PRIMARY KEY (id)
);

COMMIT;

