package org.example.project.trip

import org.example.project.user.UserDAO
import org.example.project.user.suspendTransaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.util.NoSuchElementException


class PostgresTripRepository: TripRepository {

    override suspend fun allTripsByUser(userId: Int?): List<TripResponse> = suspendTransaction {
        TripDAO
            .find { TripTable.userId eq userId }
            .map { it.toResponseDto() }
    }

    //  TODO: depending on frontend interaction, decide whether getTripById should require associated User ID
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
                try {
                    val user = UserDAO.findById(userId!!) ?: throw IllegalArgumentException("User not found")
                    val newTrip = TripDAO.new {
                        tripTitle = tripDto.tripTitle!!
                        tripDescription = tripDto.tripDescription ?: "" // Allow empty description
                        tripLocation = tripDto.tripLocation!!
                        tripDuration = tripDto.tripDuration
                        this.userId = user
                    }
                    newTrip.toResponseDto()
                } catch (e: Exception) {
                    println("Exception during TripDAO.new: ${e.message}")
                    e.printStackTrace()
                    throw e
                }
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