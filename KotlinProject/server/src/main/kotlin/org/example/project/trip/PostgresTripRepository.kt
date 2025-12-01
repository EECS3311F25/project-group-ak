package org.example.project.trip

import org.example.project.user.UserDAO
import org.example.project.user.suspendTransaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.util.NoSuchElementException

/**
 * Trip repository for PostgreSQL using Exposed DAO.
 * Updated to support imageUrl field.
 */

class PostgresTripRepository: TripRepository {

    override suspend fun allTripsByUser(userId: Int?): List<TripResponse> = suspendTransaction {
        TripDAO
            .find { TripTable.userId eq userId }
            .map { it.toResponseDto() }
    }

    override suspend fun getTrip(tripId: Int?): TripResponse? = suspendTransaction {
        tripId ?: return@suspendTransaction null
        TripDAO
            .find { TripTable.id eq tripId }
            .limit(1)
            .map { it.toResponseDto() }
            .firstOrNull()
    }

    override suspend fun addTrip(userId: Int?, tripDto: TripCreateRequest): Result<TripResponse> = suspendTransaction {
        TripService.validateTripForCreate(tripDto)
            .mapCatching {
                val user = UserDAO.findById(userId!!) ?: throw IllegalArgumentException("User not found")
                val newTrip = TripDAO.new {
                    tripTitle = tripDto.tripTitle!!
                    tripDescription = tripDto.tripDescription!!
                    tripLocation = tripDto.tripLocation!!
                    tripDuration = tripDto.tripDuration
                    this.userId = user

                    // NEW FIELD
                    imageUrl = tripDto.imageUrl
                }
                newTrip.toResponseDto()
            }
    }

    override suspend fun updateTrip(tripId: Int?, trip: Trip): Result<Boolean> = suspendTransaction {
        TripService.validateTripForUpdate(trip)
            .mapCatching {
                val tripToUpdate = TripDAO
                    .findSingleByAndUpdate(TripTable.id eq tripId!!) {
                        it.tripTitle = trip.tripTitle!!
                        it.tripDescription = trip.tripDescription!!
                        it.tripLocation = trip.tripLocation!!
                        it.tripDuration = trip.tripDuration

                        // NEW FIELD
                        it.imageUrl = trip.imageUrl
                    }
                tripToUpdate != null
            }
    }

    override suspend fun deleteTrip(tripId: Int): Result<Boolean> = suspendTransaction {
        val tripDeleted = TripTable.deleteWhere { (TripTable.id eq tripId) }
        if (tripDeleted != 1) {
            Result.failure<Boolean>(NoSuchElementException("Trip not found)"))
        } else {
            Result.success(true)
        }
    }
}