package org.example.project.trip

import Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TripResponseDto(
    val id: Int,
    @SerialName("trip_title")
    val tripTitle: String?,
    @SerialName("trip_description")
    val tripDescription: String?,

    //  TODO: implement Location data type
    //  TODO: implement Location type's logic + interaction w/ app's map view
    @SerialName("trip_location")
    val tripLocation: String?,
    @SerialName("trip_duration")
    val tripDuration: Duration,

    //  Foreign key to User table
    @SerialName("user_id")
    val userId: Int?,
)

@Serializable
data class TripRetrieveResponse(
    val message: String,
    val data: TripResponseDto
)

@Serializable
data class TripListResponse(
    val message: String,
    val trips: List<TripResponseDto>
)