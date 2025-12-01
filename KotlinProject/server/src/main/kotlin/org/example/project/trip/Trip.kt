package org.example.project.trip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import Duration

/**
 * Domain model representing a Trip.
 * Trip ID is auto-generated in the database (surrogate key).
 */

@Serializable
data class Trip (
    @SerialName("trip_title")
    val tripTitle: String?,

    @SerialName("trip_description")
    val tripDescription: String?,

    // Trip location still stored as a string
    @SerialName("trip_location")
    val tripLocation: String?,

    @SerialName("trip_duration")
    val tripDuration: Duration,

    // Foreign key to User table
    @SerialName("user_id")
    val userId: Int?,

    // NEW FIELD: trip image URL
    @SerialName("image_url")
    val imageUrl: String? = null
)