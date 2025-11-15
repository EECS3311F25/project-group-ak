package org.example.project.trip

class TripService(private val tripRepository: TripRepository) {

    suspend fun allTripsByUsername(userName: String?): List<Trip> {
        return tripRepository.allTripsByUsername(userName)
    }

    suspend fun getTripById(tripId: Int): Trip? {
        return tripRepository.getTripById(tripId)
    }

    suspend fun addTrip(trip: Trip): Result<Trip> {
        if (trip.tripTitle!!.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }
        if (trip.tripLocation!!.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip location cannot be empty"))
        }

        return try {
            val createdTrip = tripRepository.addTrip(trip)
            Result.success(createdTrip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTrip(id: Int, trip: Trip): Result<Boolean> {
        if (trip.tripTitle!!.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }

        return try {
            val updated = tripRepository.updateTrip(id, trip)
            if (updated) {
                Result.success(true)
            } else {
                Result.failure(NoSuchElementException("Trip with ID $id not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTripById(tripId: Int): Result<Boolean> {
        return try {
            val deleted = tripRepository.deleteTripById(tripId)
            if (deleted) {
                Result.success(true)
            } else {
                Result.failure(NoSuchElementException("Trip with ID $tripId not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}