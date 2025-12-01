-- Seed data for demo purposes

-- Insert a demo user (ID will be 1)
INSERT INTO users (user_name, user_email, user_password)
VALUES ('Demo User', 'demo@navi.app', 'password123')
ON CONFLICT (user_email) DO NOTHING;

-- Insert a sample 3-day trip to Toronto (ID will be 1)
INSERT INTO trips (trip_title, trip_description, trip_location, trip_duration, user_id)
VALUES (
    'Explore Toronto',
    'A 3-day adventure exploring the best of Toronto',
    'Toronto, ON, Canada',
    '{"startDate":"2025-11-27","startTime":"09:00","endDate":"2025-11-29","endTime":"18:00"}',
    1
);

-- Day 1 Events (November 27, 2025)
INSERT INTO events (event_title, event_description, event_location, event_duration, trip_id)
VALUES
(
    'CN Tower Visit',
    'Visit the iconic CN Tower and enjoy panoramic views of Toronto',
    '290 Bremner Blvd, Toronto, ON M5V 3L9',
    '{"startDate":"2025-11-27","startTime":"09:00","endDate":"2025-11-27","endTime":"11:30"}',
    1
),
(
    'Ripley''s Aquarium',
    'Explore marine life at Canada''s largest aquarium',
    '288 Bremner Blvd, Toronto, ON M5V 3L9',
    '{"startDate":"2025-11-27","startTime":"12:00","endDate":"2025-11-27","endTime":"14:30"}',
    1
),
(
    'St. Lawrence Market',
    'Browse local vendors and enjoy authentic Canadian cuisine',
    '93 Front St E, Toronto, ON M5E 1C3',
    '{"startDate":"2025-11-27","startTime":"15:00","endDate":"2025-11-27","endTime":"18:00"}',
    1
);

-- Day 2 Events (November 28, 2025)
INSERT INTO events (event_title, event_description, event_location, event_duration, trip_id)
VALUES
(
    'Royal Ontario Museum',
    'Discover world cultures and natural history',
    '100 Queens Park, Toronto, ON M5S 2C6',
    '{"startDate":"2025-11-28","startTime":"09:00","endDate":"2025-11-28","endTime":"12:00"}',
    1
),
(
    'Distillery District',
    'Walk through historic cobblestone streets filled with art galleries and cafes',
    '9 Trinity St, Toronto, ON M5A 3C4',
    '{"startDate":"2025-11-28","startTime":"13:00","endDate":"2025-11-28","endTime":"15:30"}',
    1
),
(
    'Harbourfront Centre',
    'Enjoy waterfront activities and entertainment',
    '235 Queens Quay W, Toronto, ON M5J 2G8',
    '{"startDate":"2025-11-28","startTime":"16:00","endDate":"2025-11-28","endTime":"18:00"}',
    1
);

-- Day 3 Events (November 29, 2025)
INSERT INTO events (event_title, event_description, event_location, event_duration, trip_id)
VALUES
(
    'Casa Loma',
    'Tour Toronto''s majestic Gothic Revival castle',
    '1 Austin Terrace, Toronto, ON M5R 1X8',
    '{"startDate":"2025-11-29","startTime":"09:00","endDate":"2025-11-29","endTime":"11:30"}',
    1
),
(
    'Kensington Market',
    'Explore the eclectic bohemian neighborhood',
    'Kensington Ave, Toronto, ON M5T 2K1',
    '{"startDate":"2025-11-29","startTime":"12:00","endDate":"2025-11-29","endTime":"14:30"}',
    1
),
(
    'High Park',
    'Relax in Toronto''s largest public park',
    '1873 Bloor St W, Toronto, ON M6R 2Z3',
    '{"startDate":"2025-11-29","startTime":"15:00","endDate":"2025-11-29","endTime":"18:00"}',
    1
);

-- Insert locations for each event
INSERT INTO locations (latitude, longitude, address, title, event_id)
VALUES
-- Day 1 locations
(43.6426, -79.3871, '290 Bremner Blvd, Toronto, ON M5V 3L9', 'CN Tower', 1),
(43.6424, -79.3860, '288 Bremner Blvd, Toronto, ON M5V 3L9', 'Ripley''s Aquarium of Canada', 2),
(43.6487, -79.3716, '93 Front St E, Toronto, ON M5E 1C3', 'St. Lawrence Market', 3),
-- Day 2 locations
(43.6677, -79.3948, '100 Queens Park, Toronto, ON M5S 2C6', 'Royal Ontario Museum', 4),
(43.6503, -79.3599, '9 Trinity St, Toronto, ON M5A 3C4', 'Distillery District', 5),
(43.6388, -79.3831, '235 Queens Quay W, Toronto, ON M5J 2G8', 'Harbourfront Centre', 6),
-- Day 3 locations
(43.6780, -79.4094, '1 Austin Terrace, Toronto, ON M5R 1X8', 'Casa Loma', 7),
(43.6544, -79.4005, 'Kensington Ave, Toronto, ON M5T 2K1', 'Kensington Market', 8),
(43.6465, -79.4637, '1873 Bloor St W, Toronto, ON M6R 2Z3', 'High Park', 9);