package org.example.project.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects for Trip API requests and responses.
 */

/**
 * DTO for creating a new trip (without ID).
 */
@Serializable
data class CreateTripRequest(
    val tripTitle: String,
    val tripLocation: String,
    val tripStartDate: String,
    val tripEndDate: String
)

/**
 * DTO for updating an existing trip.
 */
@Serializable
data class UpdateTripRequest(
    val tripTitle: String,
    val tripLocation: String,
    val tripStartDate: String,
    val tripEndDate: String
)

/**
 * DTO for trip response (includes ID).
 */
@Serializable
data class TripResponse(
    val tripID: Int,
    val tripTitle: String,
    val tripLocation: String,
    val tripStartDate: String,
    val tripEndDate: String
)

