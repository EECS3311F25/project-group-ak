package org.example.project.repository

import org.example.project.trip.Trip

/**
 * Repository interface for trip data access.
 * This abstraction allows switching between mock and database implementations.
 */
interface TripRepository {
    /**
     * Get a trip by its ID.
     * @param id The trip ID
     * @return The trip if found, null otherwise
     */
    suspend fun getById(id: String): Trip?
}

