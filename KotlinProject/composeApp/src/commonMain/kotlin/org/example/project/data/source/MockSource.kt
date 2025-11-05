package org.example.project.data.source

import org.example.project.model.Trip
import org.example.project.model.User
import org.example.project.model.Duration
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class LocalTripDataSource : TripDataSource {
    
    // Mock in-memory storage (replace with Room/SQLDelight later)
    private val trips = mutableListOf<Trip>(
        // Move your mock data here from HomeView.kt
        Trip(
            title = "Summer Getaway",
            description = "Road trip across Ontario",
            location = "Toronto to Ottawa",
            duration = Duration(
                startDate = LocalDate(2025, 7, 1),
                startTime = LocalTime(9, 0),
                endDate = LocalDate(2025, 7, 10),
                endTime = LocalTime(17, 0)
            ),
            users = listOf(User(name = "Klodiana"), User(name = "Alex")),
            events = emptyList(),
            imageHeaderUrl = "https://images.pexels.com/photos/1285625/pexels-photo-1285625.jpeg",
            createdDate = LocalDate(2025, 6, 1)
        )
        // Add other mock trips here...
    )
    
    override suspend fun getAllTrips(): List<Trip> {
        return trips.sortedByDescending { it.createdDate }
    }
    
    override suspend fun getTripById(id: String): Trip? {
        return trips.find { it.title == id } // Using title as ID for now
    }
    
    override suspend fun insertTrip(trip: Trip): Trip {
        trips.add(trip)
        return trip
    }
    
    override suspend fun updateTrip(trip: Trip): Trip {
        val index = trips.indexOfFirst { it.title == trip.title }
        if (index != -1) {
            trips[index] = trip
        }
        return trip
    }
    
    override suspend fun deleteTrip(tripId: String) {
        trips.removeAll { it.title == tripId }
    }
}