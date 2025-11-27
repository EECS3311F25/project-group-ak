package org.example.project.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import Duration
import org.example.project.trip.Location  // <-- Reuse Location data class

/**
 * Event domain model (used in request bodies for update)
 * Note:
 * - Location is NOT an entity, NOT stored in a separate table.
 * - Location is simply a data class embedded inside Event and stored as JSON.
 */
@Serializable
data class Event(
    @SerialName("event_title")
    val eventTitle: String,

    @SerialName("event_description")
    val eventDescription: String? = "",

    @SerialName("event_duration")
    val eventDuration: Duration,

    // Embedded Location (stored as JSON in DB)
    val location: Location,

    @SerialName("trip_id")
    val tripId: Int
)