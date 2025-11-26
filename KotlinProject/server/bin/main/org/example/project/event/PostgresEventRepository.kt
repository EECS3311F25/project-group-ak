package org.example.project.event

import org.example.project.trip.TripDAO
import org.example.project.user.suspendTransaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.util.NoSuchElementException


class PostgresEventRepository: EventRepository {

    override suspend fun allEventsByTrip(tripId: Int?): List<EventResponse> = suspendTransaction {
        EventDAO
            .find { EventTable.tripId eq tripId }
            .map { it.toResponseDto() }
    }

    override suspend fun getEvent(eventId: Int?): EventResponse? = suspendTransaction {
        eventId ?: return@suspendTransaction null
        EventDAO
            .find { (EventTable.id eq eventId) }
            .limit(1)
            .map { it.toResponseDto() }
            .firstOrNull()
    }

    override suspend fun addEvent(tripId: Int?, eventDto: EventCreateRequest): Result<EventResponse> = suspendTransaction {
        EventService.validateEventForCreate(eventDto)
            .mapCatching {
                val newEvent = EventDAO.new {
                    eventTitle = eventDto.eventTitle!!
                    eventDescription = eventDto.eventDescription!!
                    eventLocation = eventDto.eventLocation!!
                    eventDuration = eventDto.eventDuration
                    this.tripId = TripDAO[tripId!!]
                }
                newEvent.toResponseDto()
            }
    }

    override suspend fun updateEvent(eventId: Int?, event: Event): Result<Boolean> = suspendTransaction {
        EventService.validateEventForUpdate(event)
            .mapCatching {
                val eventToUpdate = EventDAO
                    .findSingleByAndUpdate(EventTable.id eq eventId!!) {
                        it.eventTitle = event.eventTitle
                        it.eventDescription = event.eventDescription
                        it.eventLocation = event.eventLocation
                        it.eventDuration = event.eventDuration
                    }
                eventToUpdate != null
            }
    }

    override suspend fun deleteEvent(eventId: Int): Result<Boolean> = suspendTransaction {
        val eventsDeleted = EventTable.deleteWhere { EventTable.id eq eventId }
        if (eventsDeleted != 1) {
            Result.failure<Boolean>(NoSuchElementException("Event not found)"))
        } else {
            Result.success(true)
        }
    }
}