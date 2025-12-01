package org.example.project.repository

import org.example.project.event.PostgresEventRepository
import org.example.project.event.EventResponse
import org.example.project.event.Event
import org.example.project.trip.PostgresTripRepository
import org.example.project.trip.Trip
import org.example.project.trip.TripResponse
import org.slf4j.LoggerFactory

/**
 * Adapter that bridges the AI summary's TripRepository interface with the database PostgresTripRepository.
 * 
 * Converts between:
 * - AI summary interface: getById(id: String): Trip?
 * - Database interface: getTrip(tripId: Int?): TripResponse?
 */
class TripRepositoryImpl(
    private val postgresRepository: PostgresTripRepository = PostgresTripRepository(),
    private val eventRepository: PostgresEventRepository = PostgresEventRepository()
) : TripRepository {
    private val logger = LoggerFactory.getLogger(TripRepositoryImpl::class.java)

    override suspend fun getById(id: String): Trip? {
        val tripId = id.toIntOrNull() ?: run {
            logger.warn("Invalid trip ID format: $id (expected integer)")
            return null
        }
        
        val tripResponse = postgresRepository.getTrip(tripId) ?: run {
            logger.warn("Trip not found: $id")
            return null
        }
        
        // Fetch events for this trip
        val eventsResult = runCatching {
            eventRepository.allEventsByTrip(tripId).map { it.toEvent() }
        }
        
        val events = eventsResult.fold(
            onSuccess = { eventList -> 
                logger.info("Fetched trip $tripId with ${eventList.size} events for AI summary")
                eventList
            },
            onFailure = { error ->
                logger.error("Failed to fetch events for trip $tripId: ${error.message}", error)
                emptyList()
            }
        )
        
        return tripResponse.toTrip(events)
    }
    
    /**
     * Convert TripResponse (database DTO) to Trip (database model)
     */
    private fun TripResponse.toTrip(events: List<Event>): Trip {
        return Trip(
            tripTitle = this.tripTitle,
            tripDescription = this.tripDescription,
            tripLocation = this.tripLocation,
            tripDuration = this.tripDuration,
            userId = this.userId,
            events = events
        )
    }
    
    /**
     * Convert EventResponse (database DTO) to Event (database model)
     */
    private fun EventResponse.toEvent(): Event {
        return Event(
            eventTitle = this.eventTitle ?: "",
            eventDescription = this.eventDescription ?: "",
            eventLocation = this.eventLocation ?: "",
            eventDuration = this.eventDuration,
            tripId = this.tripId
        )
    }
}

