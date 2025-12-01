package org.example.project.trip

import Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.user.UserDAO

/**
 * DTOs for Trip API input/output.
 * Added imageUrl field to support trip cover images.
 */

@Serializable
data class TripCreateRequest(
    @SerialName("trip_title")
    val tripTitle: String?,

    @SerialName("trip_description")
    val tripDescription: String?,

    @SerialName("trip_location")
    val tripLocation: String?,

    @SerialName("trip_duration")
    val tripDuration: Duration,

    @SerialName("user_id")
    val userId: Int,

    // NEW FIELD: optional image URL for the trip
    @SerialName("image_url")
    val imageUrl: String? = null
)

fun TripCreateRequest.toDao(): TripDAO = TripDAO.new {
    tripTitle = this@toDao.tripTitle!!
    tripDescription = this@toDao.tripDescription!!
    tripLocation = this@toDao.tripLocation!!
    tripDuration = this@toDao.tripDuration
    userId = UserDAO[this@toDao.userId]

    // NEW FIELD
    imageUrl = this@toDao.imageUrl
}

@Serializable
data class TripResponse(
    val id: Int,

    @SerialName("trip_title")
    val tripTitle: String?,

    @SerialName("trip_description")
    val tripDescription: String?,

    @SerialName("trip_location")
    val tripLocation: String?,

    @SerialName("trip_duration")
    val tripDuration: Duration,

    @SerialName("user_id")
    val userId: Int?,

    // NEW FIELD
    @SerialName("image_url")
    val imageUrl: String?
)

@Serializable
data class TripRetrieveResponse(
    val message: String,
    val data: TripResponse
)

@Serializable
data class TripListResponse(
    val message: String,
    val trips: List<TripResponse>
)