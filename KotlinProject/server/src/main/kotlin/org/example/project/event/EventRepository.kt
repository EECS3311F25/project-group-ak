package org.example.project.event

import org.example.project.trip.Trip

interface EventRepository {

    /**
     * Get all Events associated with a Trip (via its ID)
     * @return List of all associated Events
     */
    suspend fun allEventsByTripId(tripId: Int?): List<Event>

    /**
     * Get an Event by its ID.
     * @param eventId event ID
     * @return Event if found, null otherwise
     */
    suspend fun getEventById(eventId: Int?): Event?

    /**
     * Add a new Event to the database table.
     * @param event Event object to add
     * @return Created Event (with a generated surrogate key)
     */
    suspend fun addEvent(event: Event): Event

    /**
     * Update an existing Event (in the database table) by its ID
     * @param eventId ID of the Event to be updated
     * @param event Updated event data
     * @return true if updated, false if Event not found
     */
    suspend fun updateEvent(eventId: Int?, event: Event): Boolean

    /**
     * Delete an Event (from the database table) by its ID.
     * @param eventId ID of the Event to be deleted
     * @return true if deleted, false if event not found
     */
    suspend fun deleteEventById(eventId: Int): Boolean
}