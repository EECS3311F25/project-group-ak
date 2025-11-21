package org.example.project.trip

import org.example.project.user.UserDAO
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere


class PostgresTripRepository: TripRepository {

    override suspend fun allTripsByUserId(userId: Int?): List<Trip> = suspendTransaction {
        TripDAO
            .find { TripTable.userId eq userId }
            .map(::daoToTripModel)
    }

    //  TODO: depending on frontend interaction, decide whether getTripById should require associated User ID
    override suspend fun getTripById(tripId: Int?): Trip? = suspendTransaction {
        TripDAO
            .find { (TripTable.id eq tripId) }
            .limit(1)
            .map(::daoToTripModel)
            .firstOrNull()
    }

    override suspend fun addTrip(userId: Int?, trip: Trip): Trip = suspendTransaction {
        val newTrip = TripDAO.new {
            tripTitle = trip.tripTitle!!
            tripDescription = trip.tripDescription!!
            tripLocation = trip.tripLocation!!
            tripDuration = trip.tripDuration
            this.userId = UserDAO[userId!!]
        }
        daoToTripModel(newTrip)
    }

    override suspend fun updateTrip(userId: Int?, tripId: Int?, trip: Trip): Boolean = suspendTransaction {
        val tripToUpdate = TripDAO
            .findSingleByAndUpdate((TripTable.id eq tripId!!) and (TripTable.userId eq userId!!)) {
            it.tripTitle = trip.tripTitle!!
            it.tripDescription = trip.tripDescription!!
            it.tripLocation = trip.tripLocation!!
            it.tripDuration = trip.tripDuration
        }
        return@suspendTransaction (tripToUpdate != null)
    }

    override suspend fun deleteTrip(userId: Int?, tripId: Int): Boolean = suspendTransaction {
        val rowsDeleted = TripTable.deleteWhere {
            (TripTable.id eq tripId) and (TripTable.userId eq userId)
        }
        rowsDeleted == 1
    }
}