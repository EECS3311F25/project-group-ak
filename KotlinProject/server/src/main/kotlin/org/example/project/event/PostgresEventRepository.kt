package org.example.project.event

import org.example.project.user.suspendTransaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere

class PostgresEventRepository : EventRepository {

    override suspend fun eventsByTrip(tripId: Int?): List<EventResponse> = suspendTransaction {
        EventDAO
            .find { EventTable.tripId eq tripId }
            .map { it.toResponseDto() }
    }

    override suspend fun getEvent(eventId: Int?): EventResponse? = suspendTransaction {
        eventId ?: return@suspendTransaction null
        EventDAO.find { EventTable.id eq eventId }
            .limit(1)
            .map { it.toResponseDto() }
            .firstOrNull()
    }

    override suspend fun addEvent(eventDto: EventCreateRequest): Result<EventResponse> =
        suspendTransaction {
            EventService.validateEventForCreate(eventDto)
                .mapCatching {
                    val newEvent = EventDAO.new {
                        eventTitle = eventDto.eventTitle
                        eventDescription = eventDto.eventDescription ?: ""
                        eventDuration = eventDto.eventDuration
                        location = eventDto.location    // ← JSON encoded automatically
                        trip = org.example.project.trip.TripDAO[eventDto.tripId]
                    }
                    newEvent.toResponseDto()
                }
        }

    override suspend fun updateEvent(eventId: Int?, event: Event): Result<Boolean> =
        suspendTransaction {
            EventService.validateEventForUpdate(event)
                .mapCatching {
                    val updated = EventDAO.findSingleByAndUpdate(EventTable.id eq eventId!!) {
                        it.eventTitle = event.eventTitle
                        it.eventDescription = event.eventDescription ?: ""
                        it.eventDuration = event.eventDuration
                        it.location = event.location          // ← JSON encode
                    }
                    updated != null
                }
        }

    override suspend fun deleteEvent(eventId: Int): Result<Boolean> = suspendTransaction {
        val deletedCount = EventTable.deleteWhere { (EventTable.id eq eventId) }
        if (deletedCount != 1) Result.failure(Exception("Event not found"))
        else Result.success(true)
    }
}