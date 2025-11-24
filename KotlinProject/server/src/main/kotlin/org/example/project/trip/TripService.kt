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


//  TODO: improve verification criteria

object TripService {

    /**
     * Validate trip data before creating a new Trip.
     */
    fun validateTripForCreate(tripDto: TripCreateRequest): Result<Unit> {
        if (tripDto.tripTitle.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }
        if (tripDto.tripDescription.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip description cannot be empty"))
        }
        if (tripDto.tripLocation.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip location cannot be empty"))
        }

        //  TODO: implement verification for Duration's fields

        return Result.success(Unit)
    }

    /**
     * Validate trip data before updating an existing Trip.
     * (Currently same rules as create; can be customized later.)
     */
    fun validateTripForUpdate(trip: Trip): Result<Unit> {
        if (trip.tripTitle!!.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }
        if (trip.tripDescription.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Trip description cannot be empty"))
        }
        if (trip.tripLocation!!.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip location cannot be empty"))
        }

        //  TODO: implement verification for Duration's fields

        return Result.success(Unit)
    }
}