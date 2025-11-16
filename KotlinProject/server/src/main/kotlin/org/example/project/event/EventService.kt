package org.example.project.event

class EventService(private val eventRepository: EventRepository) {

    suspend fun allEventsByTripId(tripId: Int?): List<Event> {
        return eventRepository.allEventsByTripId(tripId)
    }

    suspend fun getEventById(eventId: Int): Event? {
        return eventRepository.getEventById(eventId)
    }

    suspend fun addEvent(event: Event): Result<Event> {
        if (event.eventTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))
        }
        if (event.eventLocation.isBlank()) {
            return Result.failure(IllegalArgumentException("Event location cannot be empty"))
        }


        return try {
            val createdEvent = eventRepository.addEvent(event)
            Result.success(createdEvent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(eventId: Int, event: Event): Result<Boolean> {
        if (event.eventTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))
        }
        if (event.eventLocation.isBlank()) {
            return Result.failure(IllegalArgumentException("Event location cannot be empty"))
        }

        return try {
            val updated = eventRepository.updateEvent(eventId, event)
            if (updated) {
                Result.success(true)
            } else {
                Result.failure(NoSuchElementException("Event with ID $eventId not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEventById(eventId: Int): Result<Boolean> {
        return try {
            val deleted = eventRepository.deleteEventById(eventId)
            if (deleted) {
                Result.success(true)
            } else {
                Result.failure(NoSuchElementException("Event with ID $eventId not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}