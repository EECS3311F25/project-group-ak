-- Migration V3: Add imageUrl column to trips table

ALTER TABLE trips
ADD COLUMN image_url VARCHAR(500);