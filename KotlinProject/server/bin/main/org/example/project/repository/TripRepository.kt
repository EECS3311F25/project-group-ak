package org.example.project.repository

import org.example.project.Trip

/**
 * Repository interface for Trip data operations.
 * Provides abstraction layer between application logic and database.
 */
interface TripRepository {
    /**
     * Get all trips from the database.
     * @return List of all trips
     */
    suspend fun allTrips(): List<Trip>
    
    /**
     * Get a trip by its ID.
     * @param id Trip ID
     * @return Trip if found, null otherwise
     */
    suspend fun getTripById(id: Int): Trip?
    
    /**
     * Add a new trip to the database.
     * @param trip Trip object to add
     * @return Created trip with generated ID
     */
    suspend fun addTrip(trip: Trip): Trip
    
    /**
     * Update an existing trip.
     * @param id Trip ID to update
     * @param trip Updated trip data
     * @return true if updated, false if trip not found
     */
    suspend fun updateTrip(id: Int, trip: Trip): Boolean
    
    /**
     * Delete a trip by ID.
     * @param id Trip ID to delete
     * @return true if deleted, false if trip not found
     */
    suspend fun deleteTrip(id: Int): Boolean
}

