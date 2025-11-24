package org.example.project.event

interface EventRepository {

    /**
     * Get all events associated with a Trip
     * @param tripId ID of associated Trip
     * @return List of all associated events
     */
    suspend fun allEventsByTrip(tripId: Int?): List<EventResponse>

    /**
     * Get an event by its ID.
     * @param eventId event ID
     * @return Event if found, null otherwise
     */
    suspend fun getEvent(eventId: Int?): EventResponse?

    /**
     * Add a new Event to the Event table, associated with a Trip in the Trip table.
     * @param eventDto object to add
     * @param tripId ID of the associated Trip requesting the creation
     * @return Created Event (with a generated surrogate key)
     */
    suspend fun addEvent(tripId: Int?, eventDto: EventCreateRequest): Result<EventResponse>

    /**
     * Update an existing Event (in the database table) by its ID and the associated Trip's ID
     * @param eventId ID of the Event to be updated
     * @param event Updated event data
     * @return true if updated, false otherwise
     */
    suspend fun updateEvent(eventId: Int?, event: Event): Result<Boolean>

    /**
     * Delete an event by its ID and the associated Trip's ID
     * @param eventId ID of the Event to be deleted
     * @return true if deleted, false if trip not found
     */
    suspend fun deleteEvent(eventId: Int): Result<Boolean>
}