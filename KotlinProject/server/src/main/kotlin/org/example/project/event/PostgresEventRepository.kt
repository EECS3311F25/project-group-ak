package org.example.project.event

import org.example.project.location.LocationDAO
import org.example.project.location.LocationTable
import org.example.project.location.LocationCreateRequest
import org.example.project.trip.TripDAO
import org.example.project.user.suspendTransaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.util.NoSuchElementException

class PostgresEventRepository : EventRepository {

    override suspend fun allEventsByTrip(tripId: Int?): List<EventResponse> =
        suspendTransaction {
            EventDAO
                .find { EventTable.tripId eq tripId }
                .map { it.toResponseDto() }
        }

    override suspend fun getEvent(eventId: Int?): EventResponse? =
        suspendTransaction {
            eventId ?: return@suspendTransaction null
            EventDAO
                .find { EventTable.id eq eventId }
                .limit(1)
                .map { it.toResponseDto() }
                .firstOrNull()
        }

    override suspend fun addEvent(tripId: Int?, eventDto: EventCreateRequest): Result<EventResponse> =
        suspendTransaction {

            EventService.validateEventForCreate(eventDto)
                .mapCatching {

                    val newEvent = EventDAO.new {
                        eventTitle = eventDto.eventTitle!!
                        eventDescription = eventDto.eventDescription ?: ""
                        eventLocation = eventDto.eventLocation ?: ""
                        eventDuration = eventDto.eventDuration
                        this.tripId = TripDAO[tripId!!]
                    }

                    // NEW — create Location record if provided
                    eventDto.location?.let { loc ->
                        LocationDAO.new {
                            latitude = loc.latitude
                            longitude = loc.longitude
                            address = loc.address
                            title = loc.title
                            eventId = newEvent
                        }
                    }

                    newEvent.toResponseDto()
                }
        }

    override suspend fun updateEvent(eventId: Int?, event: Event): Result<Boolean> =
        suspendTransaction {

            EventService.validateEventForUpdate(event)
                .mapCatching {

                    val updated = EventDAO
                        .find { EventTable.id eq eventId!! }
                        .firstOrNull()
                        ?.apply {
                            eventTitle = event.eventTitle
                            eventDescription = event.eventDescription
                            eventLocation = event.eventLocation
                            eventDuration = event.eventDuration

                            // Update location if the event has one
                            location?.apply {
                                address = event.location?.address
                                title = event.location?.title
                                latitude = event.location?.latitude!!
                                longitude = event.location?.longitude!!
                            }
                        }

                    updated != null
                }
        }

    override suspend fun deleteEvent(eventId: Int): Result<Boolean> =
        suspendTransaction {
            val deleted = EventTable.deleteWhere { EventTable.id eq eventId }
            if (deleted != 1) Result.failure(NoSuchElementException("Event not found"))
            else Result.success(true)
        }
}