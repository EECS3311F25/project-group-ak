package org.example.project.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val title: String?,
    @SerialName("event_id")
    val eventId: Int
)

@Serializable
data class LocationCreateRequest(
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val title: String?,
    @SerialName("event_id")
    val eventId: Int
)

@Serializable
data class LocationRetrieveResponse(
    val message: String,
    val data: LocationResponse
)

@Serializable
data class LocationListResponse(
    val message: String,
    val locations: List<LocationResponse>
)
