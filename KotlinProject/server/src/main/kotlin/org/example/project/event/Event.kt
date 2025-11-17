package org.example.project.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Event (
    @SerialName("event_title")
    val eventTitle: String,
    @SerialName("event_description")
    val eventDescription: String,

    //  TODO: implement Location data type
    //  TODO: implement Location type's logic + interaction w/ app's map view
    @SerialName("event_location")
    val eventLocation: String,

    //  TODO: implement type for date + time
    @SerialName("trip_duration")
    val eventDuration: String?,

    //  Foreign key - Trip.id
    @SerialName("trip_id")
    val tripId: Int
)