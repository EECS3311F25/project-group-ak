package org.example.project.event

import org.example.project.trip.TripDAO
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere


class PostgresEventRepository: EventRepository {

    override suspend fun allEventsByTripId(tripId: Int?): List<Event> = suspendTransaction {
        EventDAO.all().map(::daoToEventModel)
    }

    override suspend fun getEventById(eventId: Int?): Event? = suspendTransaction {
        EventDAO
            .find { (EventTable.id eq eventId) }
            .limit(1)
            .map(::daoToEventModel)
            .firstOrNull()
    }

    override suspend fun addEvent(event: Event): Event = suspendTransaction {
        val newEvent = EventDAO.new {
            eventTitle = event.eventTitle
            eventDescription = event.eventDescription
            eventLocation = event.eventLocation
            eventDuration = event.eventDuration!!
            tripId = TripDAO[event.tripId]
        }
        daoToEventModel(newEvent)
    }

    override suspend fun updateEvent(eventId: Int?, event: Event): Boolean = suspendTransaction {
        val eventToUpdate = EventDAO.findSingleByAndUpdate(EventTable.id eq eventId!!) {
            it.eventTitle = event.eventTitle
            it.eventLocation = event.eventLocation
            it.eventDuration = event.eventDuration!!
            it.tripId = TripDAO[event.tripId]
        }

        return@suspendTransaction (eventToUpdate != null)
    }

    override suspend fun deleteEventById(eventId: Int): Boolean = suspendTransaction {
        val rowsDeleted = EventTable.deleteWhere {
            EventTable.id eq eventId
        }
        rowsDeleted == 1
    }
}