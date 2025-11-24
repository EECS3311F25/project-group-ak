package org.example.project.event

import Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.trip.TripDAO


@Serializable
data class EventCreateDto(
    @SerialName("event_title")
    val eventTitle: String?,
    @SerialName("event_description")
    val eventDescription: String?,

    //  TODO: implement Location data type
    //  TODO: implement Location type's logic + interaction w/ app's map view
    @SerialName("event_location")
    val eventLocation: String?,
    @SerialName("event_duration")
    val eventDuration: Duration,

    //  Foreign key to Trip table
    @SerialName("trip_id")
    val tripId: Int
)

fun EventCreateDto.toDao(): EventDAO = EventDAO.new {
    eventTitle = this@toDao.eventTitle!!
    eventDescription = this@toDao.eventDescription!!
    eventLocation = this@toDao.eventLocation!!
    eventDuration = this@toDao.eventDuration
    tripId = TripDAO[this@toDao.tripId]
}