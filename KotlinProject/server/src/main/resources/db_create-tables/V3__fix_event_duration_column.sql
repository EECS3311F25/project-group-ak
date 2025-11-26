-- Fix typo in events table: rename trip_duration to event_duration
ALTER TABLE events RENAME COLUMN trip_duration TO event_duration;
