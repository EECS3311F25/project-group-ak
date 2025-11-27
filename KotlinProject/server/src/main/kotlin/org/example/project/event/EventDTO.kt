package org.example.project.event

import Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.location.LocationCreateRequest
import org.example.project.location.LocationResponse

/**
 * DTO for creating an Event.
 * Includes nested LocationCreateRequest as required by Toni.
 */
@Serializable
data class EventCreateRequest(
    @SerialName("event_title")
    val eventTitle: String?,
    @SerialName("event_description")
    val eventDescription: String?,
    @SerialName("event_location")
    val eventLocation: String?,    // legacy field required by DB schema
    @SerialName("event_duration")
    val eventDuration: Duration,
    @SerialName("trip_id")
    val tripId: Int,

    /**
     * NEW nested location object
     */
    val location: LocationCreateRequest? = null
)

@Serializable
data class EventResponse(
    val id: Int,
    @SerialName("event_title")
    val eventTitle: String?,
    @SerialName("event_description")
    val eventDescription: String?,
    @SerialName("event_location")
    val eventLocation: String?,
    @SerialName("event_duration")
    val eventDuration: Duration,
    @SerialName("trip_id")
    val tripId: Int?,
    val location: LocationResponse?
)

@Serializable
data class EventRetrieveResponse(
    val message: String,
    val data: EventResponse
)

@Serializable
data class EventListResponse(
    val message: String,
    val events: List<EventResponse>
)