package org.example.project.event

import Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class EventResponseDto(
    val id: Int,
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
    val tripId: Int?,
)

@Serializable
data class EventRetrieveResponse(
    val message: String,
    val data: EventResponseDto
)

@Serializable
data class EventListResponse(
    val message: String,
    val events: List<EventResponseDto>
)