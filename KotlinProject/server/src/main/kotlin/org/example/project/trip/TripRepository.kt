package org.example.project.trip


/**
 *  Interface for Trip objects and their CRUD operations
 * Provides abstraction layer between application logic and database.
 */
interface TripRepository {
    /**
     * Get all trips associated with a User
     * @param userId ID of associated User
     * @return List of all associated trips
     */
    suspend fun allTripsByUserId(userId: Int?): List<TripResponseDto>

    /**
     * Get a trip by its ID.
     * @param tripId Trip ID
     * @return Trip if found, null otherwise
     */
    suspend fun getTripById(tripId: Int?): TripResponseDto?

    /**
     * Add a new Trip to the Trip table, associated with a User in the User table
     * @param trip Trip object to add
     * @param userId ID of associated User requesting the creation
     * @return Created trip (with a generated surrogate key)
     */
    suspend fun addTrip(userId: Int?, tripDto: TripCreateDto): Result<TripResponseDto>

    /**
     * Update an existing Trip (in the database table) by its ID and the associated User's ID
     * @param tripId ID of the Trip to be updated
     * @param userId ID of the associated User requesting the update
     * @param trip Updated trip data
     * @return true if updated, false otherwise
     */
    suspend fun updateTrip(userId: Int?, tripId: Int?, trip: Trip): Result<Boolean>

    /**
     * Delete a trip by its ID and the associated User's ID
     * @param tripId ID of the Trip to be deleted
     * @param userId ID of the associated User requesting the deletion
     * @return true if deleted, false if trip not found
     */
    suspend fun deleteTrip(userId: Int?, tripId: Int): Result<Boolean>
}