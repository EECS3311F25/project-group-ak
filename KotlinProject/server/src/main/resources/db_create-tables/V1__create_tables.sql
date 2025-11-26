--  Database schema for Navi travel app

DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS users;

-- User table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    user_email VARCHAR(50) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL
);

--  Trip table
CREATE TABLE IF NOT EXISTS trips (
    id SERIAL PRIMARY KEY,
    trip_title VARCHAR(100) NOT NULL,
    trip_description VARCHAR(500),
    trip_location VARCHAR(255) NOT NULL,
    trip_duration VARCHAR(200) NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--  Event table
CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    event_title VARCHAR(100) NOT NULL,
    event_description VARCHAR(500),
    event_location VARCHAR(255) NOT NULL,
    event_duration VARCHAR(200) NOT NULL,
    trip_id INT NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);

-- Location table
CREATE TABLE IF NOT EXISTS locations (
    id SERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(500),
    title VARCHAR(255),
    event_id INT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_user_user_id ON users(id);
CREATE INDEX IF NOT EXISTS idx_trip_user_id ON trips(user_id);
CREATE INDEX IF NOT EXISTS idx_event_trip_id ON events(trip_id);
CREATE INDEX IF NOT EXISTS idx_location_event_id ON locations(event_id);