package org.example.project.event

import org.example.project.trip.Trip
import org.example.project.trip.TripCreateDto
import org.example.project.trip.TripResponseDto

interface EventRepository {

    /**
     * Get all events associated with a Trip
     * @param tripId ID of associated Trip
     * @return List of all associated events
     */
    suspend fun allEventsByTripId(tripId: Int?): List<EventResponseDto>

    /**
     * Get an event by its ID.
     * @param eventId event ID
     * @return Event if found, null otherwise
     */
    suspend fun getEventById(eventId: Int?): EventResponseDto?

    /**
     * Add a new Event to the Event table, associated with a Trip in the Trip table.
     * @param eventDto object to add
     * @param tripId ID of the associated Trip requesting the creation
     * @return Created Event (with a generated surrogate key)
     */
    suspend fun addEvent(tripId: Int?, eventDto: EventCreateDto): Result<EventResponseDto>

    /**
     * Update an existing Event (in the database table) by its ID and the associated Trip's ID
     * @param eventId ID of the Event to be updated
     * @param tripId ID of the associated Trip requesting the update
     * @param event Updated event data
     * @return true if updated, false otherwise
     */
    suspend fun updateEvent(tripId: Int?, eventId: Int?, event: Event): Result<Boolean>

    /**
     * Delete an event by its ID and the associated Trip's ID
     * @param eventId ID of the Event to be deleted
     * @param tripId ID of the associated Trip requesting the deletion
     * @return true if deleted, false if trip not found
     */
    suspend fun deleteEvent(tripId: Int?, eventId: Int): Result<Boolean>
}