package org.example.project.data.source

import org.example.project.model.Trip

interface TripDataSource {
    suspend fun getAllTrips(): List<Trip>
    suspend fun getTripById(id: String): Trip?
    suspend fun insertTrip(trip: Trip): Trip
    suspend fun updateTrip(trip: Trip): Trip
    suspend fun deleteTrip(tripId: String)
}