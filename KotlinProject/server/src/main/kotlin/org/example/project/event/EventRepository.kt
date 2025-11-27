package org.example.project.event

/**
 * Repository interface defining all Event-related CRUD operations.
 * Acts as the abstraction layer between DB and application logic.
 */
interface EventRepository {

    /**
     * Get all events belonging to a Trip.
     */
    suspend fun eventsByTrip(tripId: Int?): List<EventResponse>

    /**
     * Get a specific Event by ID.
     */
    suspend fun getEvent(eventId: Int?): EventResponse?

    /**
     * Create a new Event belonging to a specific Trip.
     */
    suspend fun addEvent(eventDto: EventCreateRequest): Result<EventResponse>

    /**
     * Update an existing Event.
     */
    suspend fun updateEvent(eventId: Int?, event: Event): Result<Boolean>

    /**
     * Delete an event by ID.
     */
    suspend fun deleteEvent(eventId: Int): Result<Boolean>
}