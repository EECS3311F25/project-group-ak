package org.example.project.event

import Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Event (
    @SerialName("event_title")
    val eventTitle: String,
    @SerialName("event_description")
    val eventDescription: String,

    @SerialName("event_location")
    val eventLocation: String,
    @SerialName("location_latitude")
    val locationLatitude: Double? = null,
    @SerialName("location_longitude")
    val locationLongitude: Double? = null,
    @SerialName("location_address")
    val locationAddress: String? = null,
    @SerialName("location_title")
    val locationTitle: String? = null,
    @SerialName("event_duration")
    val eventDuration: Duration,

    //  Foreign key to Trip table
    @SerialName("trip_id")
    val tripId: Int?,
)
