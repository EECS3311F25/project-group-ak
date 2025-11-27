package org.example.project.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import Duration
import org.example.project.trip.Location

/**
 * DTO for event creation requests.
 * Location is provided directly (no separate DTO/DAO for Location).
 */
@Serializable
data class EventCreateRequest(
    @SerialName("event_title")
    val eventTitle: String,

    @SerialName("event_description")
    val eventDescription: String? = "",

    @SerialName("event_duration")
    val eventDuration: Duration,

    // Embedded Location object included directly in request
    val location: Location,

    @SerialName("trip_id")
    val tripId: Int
)

/**
 * DTO returned to frontend.
 * Includes Location directly.
 */
@Serializable
data class EventResponse(
    val id: Int,
    @SerialName("event_title")
    val eventTitle: String,
    @SerialName("event_description")
    val eventDescription: String?,
    @SerialName("event_duration")
    val eventDuration: Duration,
    val location: Location,
    @SerialName("trip_id")
    val tripId: Int
)

@Serializable
data class EventListResponse(
    val message: String,
    val events: List<EventResponse>
)

@Serializable
data class EventRetrieveResponse(
    val message: String,
    val data: EventResponse
)