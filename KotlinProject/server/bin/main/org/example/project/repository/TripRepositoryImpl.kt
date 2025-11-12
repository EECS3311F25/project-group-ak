package org.example.project.repository


/*
Suspend :  can be paused or removed
    - Long running Tasks 
    - 1) Network Request and Database Operations without freezing the main point


*/
import org.example.project.Trip
import org.example.project.db.dao.TripDAO
import org.example.project.db.dao.daoToTripModel
import org.example.project.db.tables.TripTable
import org.example.project.db.suspendTransaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere

/**
 * PostgreSQL implementation of TripRepository using Exposed ORM.
 * Handles all database operations for trips.
 */
class TripRepositoryImpl : TripRepository {
    
    override suspend fun allTrips(): List<Trip> = suspendTransaction {
        TripDAO.all().map(::daoToTripModel)
    }
    
    override suspend fun getTripById(id: Int): Trip? = suspendTransaction {
        TripDAO.findById(id)?.let(::daoToTripModel)
    }
    
    override suspend fun addTrip(trip: Trip): Trip = suspendTransaction {
        val newTrip = TripDAO.new {
            tripTitle = trip.tripTitle
            tripLocation = trip.tripLocation
            tripStartDate = trip.tripStartDate
            tripEndDate = trip.tripEndDate
        }
        daoToTripModel(newTrip)
    }
    
    override suspend fun updateTrip(id: Int, trip: Trip): Boolean = suspendTransaction {
        val existingTrip = TripDAO.findById(id)
        if (existingTrip != null) {
            existingTrip.tripTitle = trip.tripTitle
            existingTrip.tripLocation = trip.tripLocation
            existingTrip.tripStartDate = trip.tripStartDate
            existingTrip.tripEndDate = trip.tripEndDate
            true
        } else {
            false
        }
    }
    
    override suspend fun deleteTrip(id: Int): Boolean = suspendTransaction {
        val rowsDeleted = TripTable.deleteWhere {
            TripTable.id eq id
        }
        rowsDeleted == 1
    }
}

