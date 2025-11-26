-- Seed data for demo purposes

-- Insert a demo user (ID will be 1)
INSERT INTO users (user_name, user_email, user_password)
VALUES ('Demo User', 'demo@navi.app', 'password123')
ON CONFLICT (user_email) DO NOTHING;

-- You can optionally add sample trips and events here
-- Example:
-- INSERT INTO trips (trip_title, trip_description, trip_location, trip_duration, user_id)
-- VALUES ('Paris Adventure', 'A wonderful trip to Paris', 'Paris, France', '{"startDate":"2025-12-01","startTime":"09:00:00","endDate":"2025-12-07","endTime":"18:00:00"}', 1)
-- ON CONFLICT DO NOTHING;
