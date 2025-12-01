package org.example.project.data.api.dto

import kotlinx.serialization.Serializable
/*
what does Serializable do
 - it let's you convert kotlin to json and json to kotlin object

 -- In data class the methods like equals() hashcode can overided automatically according to use to the use

*/

@Serializable
data class TripSummaryRequest(
    val tripId: String,
    val trip: BackendTrip? = null // Optional: trip data if not in backend
)

@Serializable
data class BackendTrip(
    val id: String,
    val name: String,
    val owner: String,
    val users: List<String>,
    val events: List<BackendEvent> = emptyList(),
    val duration: BackendDuration
)

@Serializable
data class BackendDuration(
    val start: String,
    val end: String
)

@Serializable
data class BackendEvent(
    val id: String,
    val title: String,
    val description: String? = null,
    val location: BackendLocation? = null,
    val duration: BackendDuration
)

@Serializable
data class BackendLocation(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class TripSummaryResponse(
    val summary : String,
    val generatedAt: String
)

@Serializable
data class ErrorResponse(
    val error : String,
    val message : String
)




