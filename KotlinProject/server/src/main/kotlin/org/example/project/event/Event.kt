package org.example.project.event

import Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.location.LocationResponse

/**
 * Domain model representing an Event.
 * Now includes a new Location field as required by Toni's instructions.
 */
@Serializable
data class Event(
    @SerialName("event_title")
    val eventTitle: String,
    @SerialName("event_description")
    val eventDescription: String,

    // Legacy string location (still stored in DB)
    @SerialName("event_location")
    val eventLocation: String,

    @SerialName("event_duration")
    val eventDuration: Duration,

    @SerialName("trip_id")
    val tripId: Int,

    /**
     * New Location object (GPS coordinates + optional title/address)
     */
    val location: LocationResponse? = null
)