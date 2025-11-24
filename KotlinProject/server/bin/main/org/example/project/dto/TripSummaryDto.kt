package org.example.project.dto

import kotlinx.serialization.Serializable

/* 
Serializable tells Kotlin that this one class can be converted to Json
and converted back from Json Automatically
*/

@Serializable
data class TripSummaryRequest(
    val tripId: String,
    val trip: org.example.project.trip.Trip? = null // Optional: trip data if not in backend
)

@Serializable
data class TripSummaryResponse(val summary : String, val generatedAt : String)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)