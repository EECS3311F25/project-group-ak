package org.example.project.data.source

import org.example.project.model.Event
import org.example.project.model.Trip

interface TripDataSource {
    suspend fun getAllTrips(): List<Trip>
    suspend fun getTripById(id: String): Trip?
    suspend fun insertTrip(trip: Trip): Trip
    suspend fun updateTrip(trip: Trip): Trip
    suspend fun deleteTrip(tripId: String)
    suspend fun addEventToTrip(tripId: String, event: Event)
    suspend fun deleteEventFromTrip(tripId: String, eventId: String)
    suspend fun updateEventInTrip(tripId: String, eventId: String, updated: Event)
    suspend fun addMemberToTrip(tripId: String, userId: String)
    suspend fun removeMemberFromTrip(tripId: String, userId: String)
}