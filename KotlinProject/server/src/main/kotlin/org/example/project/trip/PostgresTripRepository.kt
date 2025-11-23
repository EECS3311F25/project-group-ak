package org.example.project.trip

import org.example.project.user.UserDAO
import org.example.project.user.UserService
import org.example.project.user.UserTable
import org.example.project.user.suspendTransaction
import org.example.project.user.toResponseDto
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.util.NoSuchElementException


class PostgresTripRepository: TripRepository {

    override suspend fun allTripsByUserId(userId: Int?): List<TripResponseDto> = suspendTransaction {
        TripDAO
            .find { TripTable.userId eq userId }
            .map { it.toResponseDto() }
    }

    //  TODO: depending on frontend interaction, decide whether getTripById should require associated User ID
    override suspend fun getTripById(tripId: Int?): TripResponseDto? = suspendTransaction {
        tripId ?: return@suspendTransaction null
        TripDAO
            .find { (TripTable.id eq tripId) }
            .limit(1)
            .map { it.toResponseDto() }
            .firstOrNull()
    }

    override suspend fun addTrip(userId: Int?, tripDto: TripCreateDto): Result<TripResponseDto> = suspendTransaction {
        TripService.validateTripForCreate(tripDto)
            .mapCatching {
                val newTrip = TripDAO.new {
                    tripTitle = tripDto.tripTitle!!
                    tripDescription = tripDto.tripDescription!!
                    tripLocation = tripDto.tripLocation!!
                    tripDuration = tripDto.tripDuration
                    this.userId = UserDAO[userId!!]
                }
                newTrip.toResponseDto()
            }
    }

    override suspend fun updateTrip(userId: Int?, tripId: Int?, trip: Trip): Result<Boolean> = suspendTransaction {
        TripService.validateTripForUpdate(trip)
            .mapCatching {
                val tripToUpdate = TripDAO
                    .findSingleByAndUpdate((TripTable.id eq tripId!!) and (TripTable.userId eq userId!!)) {
                        it.tripTitle = trip.tripTitle!!
                        it.tripDescription = trip.tripDescription!!
                        it.tripLocation = trip.tripLocation!!
                        it.tripDuration = trip.tripDuration
                    }
                tripToUpdate != null
            }
    }

    override suspend fun deleteTrip(userId: Int?, tripId: Int): Result<Boolean> = suspendTransaction {
        val tripDeleted = TripTable.deleteWhere {
            (TripTable.id eq tripId) and (TripTable.userId eq userId)
        }
        if (tripDeleted != 1) {
            Result.failure<Boolean>(NoSuchElementException("Trip not found)"))
        } else {
            Result.success(true)
        }
    }
}