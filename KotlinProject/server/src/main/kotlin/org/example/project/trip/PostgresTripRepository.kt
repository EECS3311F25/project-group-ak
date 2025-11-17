package org.example.project.trip

import org.example.project.user.UserDAO
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere


class PostgresTripRepository: TripRepository {

    override suspend fun allTripsByUsername(userName: String?): List<Trip> = suspendTransaction {
        TripDAO.all().map(::daoToTripModel)
    }

    override suspend fun getTripById(tripId: Int?): Trip? = suspendTransaction {
        TripDAO
            .find { (TripTable.id eq tripId) }
            .limit(1)
            .map(::daoToTripModel)
            .firstOrNull()
    }

    override suspend fun addTrip(trip: Trip): Trip = suspendTransaction {
        val newTrip = TripDAO.new {
            tripTitle = trip.tripTitle!!
            tripDescription = trip.tripDescription!!
            tripLocation = trip.tripLocation!!
            tripDuration = trip.tripDuration
            userId = UserDAO[trip.userid]
        }
        daoToTripModel(newTrip)
    }

    override suspend fun updateTrip(tripId: Int?, trip: Trip): Boolean = suspendTransaction {
        val tripToUpdate = TripDAO.findSingleByAndUpdate(TripTable.id eq tripId!!) {
            it.tripTitle = trip.tripTitle!!
            it.tripDescription = trip.tripDescription!!
            it.tripLocation = trip.tripLocation!!
            it.tripDuration = trip.tripDuration
            it.userId = UserDAO[trip.userid]
        }

        return@suspendTransaction (tripToUpdate != null)
    }

    override suspend fun deleteTripById(tripId: Int): Boolean = suspendTransaction {
        val rowsDeleted = TripTable.deleteWhere {
            TripTable.id eq tripId
        }
        rowsDeleted == 1
    }
}