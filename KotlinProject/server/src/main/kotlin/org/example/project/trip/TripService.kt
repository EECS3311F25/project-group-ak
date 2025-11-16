package org.example.project.trip

/**
 * Service layer for Trip business logic.
 *
 * IMPORTANT:
 * - Routes -> TripService (validation only) -> TripRepository (PostgresTripRepository) -> database
 * - This class does NOT talk to the database directly.
 * - This class does NOT hold a reference to any repository.
 *
 * Responsibility:
 * - Validate Trip data for create / update.
 * - Return Result<Unit> so routes can map it to HTTP responses.
 */
object TripService {

    /**
     * Validate trip data before creating a new Trip.
     */
    fun validateTripForCreate(trip: Trip): Result<Unit> {
        if (trip.tripTitle.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }

        if (trip.tripLocation.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip location cannot be empty"))
        }

        // Optional: basic check for dates not being empty
        if (trip.tripStartDate.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip start date cannot be empty"))
        }
        if (trip.tripEndDate.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip end date cannot be empty"))
        }

        return Result.success(Unit)
    }

    /**
     * Validate trip data before updating an existing Trip.
     * (Currently same rules as create; can be customized later.)
     */
    fun validateTripForUpdate(trip: Trip): Result<Unit> {
        if (trip.tripTitle.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }

        if (trip.tripLocation.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip location cannot be empty"))
        }

        // Optional: basic check for dates
        if (trip.tripStartDate.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip start date cannot be empty"))
        }
        if (trip.tripEndDate.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip end date cannot be empty"))
        }

        return Result.success(Unit)
    }
}