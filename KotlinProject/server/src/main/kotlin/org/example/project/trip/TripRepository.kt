package org.example.project.trip


/**
 *  Interface for Trip objects and their CRUD operations
 * Provides abstraction layer between application logic and database.
 */
interface TripRepository {
    /**
     * Get all trips associated with a User (via the username)
     * @return List of all associated trips
     */
    suspend fun allTripsByUsername(userName: String?): List<Trip>

    /**
     * Get a trip by its ID.
     * @param tripId Trip ID
     * @return Trip if found, null otherwise
     */
    suspend fun getTripById(tripId: Int?): Trip?

    /**
     * Add a new Trip to the database table.
     * @param trip Trip object to add
     * @return Created trip (with a generated surrogate key)
     */
    suspend fun addTrip(trip: Trip): Trip

    /**
     * Update an existing Trip (in the database table) by its ID
     * @param tripId ID of the Trip to be updated
     * @param trip Updated trip data
     * @return true if updated, false if Trip not found
     */
    suspend fun updateTrip(tripId: Int?, trip: Trip): Boolean

    /**
     * Delete a trip by its ID.
     * @param tripId ID of the Trip to be deleted
     * @return true if deleted, false if trip not found
     */
    suspend fun deleteTripById(tripId: Int): Boolean
}