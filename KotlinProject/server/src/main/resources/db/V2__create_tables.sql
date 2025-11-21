--  Database schema for Navi travel app

-- Drop V1 tables, migrate -> V2
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS trip;
DROP TABLE IF EXISTS "user";

-- User table
CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    user_email VARCHAR(50) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL
);

--  Trip table
CREATE TABLE IF NOT EXISTS trip (
    id SERIAL PRIMARY KEY,
    trip_title VARCHAR(100) NOT NULL,
    trip_location VARCHAR(255) NOT NULL,
    trip_duration VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

--  Event table
CREATE TABLE IF NOT EXISTS event (
    id SERIAL PRIMARY KEY,
    event_title VARCHAR(100) NOT NULL,
    event_description VARCHAR(500),
    event_location VARCHAR(255) NOT NULL,
    trip_duration VARCHAR(50) NOT NULL,
    trip_id INT NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trip(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_user_email ON "user"(user_email);
CREATE INDEX IF NOT EXISTS idx_event_trip_id ON event(trip_id);