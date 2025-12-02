package org.example.project.event

import org.example.project.location.LocationDAO
import org.example.project.location.LocationTable
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
                val locationAddress = eventDto.locationAddress ?: eventDto.eventLocation ?: ""
                val locationTitle = eventDto.locationTitle ?: eventDto.eventTitle

                val newEvent = EventDAO.new {
                    eventTitle = eventDto.eventTitle!!
                    eventDescription = eventDto.eventDescription ?: "" // Allow empty description
                    eventLocation = locationAddress
                    eventDuration = eventDto.eventDuration
                    this.tripId = TripDAO[tripId!!]
                }
                upsertLocation(
                    eventDao = newEvent,
                    latitude = eventDto.locationLatitude,
                    longitude = eventDto.locationLongitude,
                    address = locationAddress,
                    title = locationTitle
                )
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
                        it.eventLocation = event.locationAddress ?: event.eventLocation
                        it.eventDuration = event.eventDuration
                    }
                if (eventToUpdate != null) {
                    upsertLocation(
                        eventDao = eventToUpdate,
                        latitude = event.locationLatitude,
                        longitude = event.locationLongitude,
                        address = event.locationAddress ?: event.eventLocation,
                        title = event.locationTitle ?: event.eventTitle
                    )
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

    private fun upsertLocation(
        eventDao: EventDAO,
        latitude: Double?,
        longitude: Double?,
        address: String?,
        title: String?
    ) {
        if (latitude == null || longitude == null) return // no coordinates provided, skip

        val existing = LocationDAO.find { LocationTable.eventId eq eventDao.id }.firstOrNull()
        if (existing != null) {
            existing.latitude = latitude
            existing.longitude = longitude
            existing.address = address
            existing.title = title
        } else {
            LocationDAO.new {
                this.latitude = latitude
                this.longitude = longitude
                this.address = address
                this.title = title
                this.eventId = eventDao
            }
        }
    }
}
