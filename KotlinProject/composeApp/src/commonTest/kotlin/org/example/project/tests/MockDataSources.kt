package org.example.project.tests

import org.example.project.data.source.TripDataSource
import org.example.project.data.source.UserDataSource
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.User

/**
 * Mock TripDataSource for testing
 * Implements TripDataSource interface directly to avoid HTTP dependencies
 */
class MockTripDataSource : TripDataSource {
    private val tripsList = mutableListOf<Trip>()
    
    fun setTrips(trips: List<Trip>) {
        tripsList.clear()
        tripsList.addAll(trips)
    }
    
    // This method is used by TripRepository.fetchTrips()
    override suspend fun fetchTrips(): List<Trip> {
        return tripsList.toList()
    }
    
    override suspend fun getAllTrips(): List<Trip> {
        return tripsList.toList()
    }
    
    override suspend fun getTripById(id: String): Trip? {
        return tripsList.find { it.id == id }
    }
    
    override suspend fun insertTrip(trip: Trip): Trip {
        tripsList.add(trip)
        return trip
    }
    
    override suspend fun updateTrip(trip: Trip): Trip {
        val index = tripsList.indexOfFirst { it.id == trip.id }
        if (index >= 0) {
            tripsList[index] = trip
        } else {
            tripsList.add(trip)
        }
        return trip
    }
    
    override suspend fun deleteTrip(tripId: String) {
        tripsList.removeAll { it.id == tripId }
    }
    
    override suspend fun addEventToTrip(tripId: String, event: Event) {
        val trip = tripsList.find { it.id == tripId }
        if (trip != null) {
            val updatedTrip = trip.copy(events = trip.events + event)
            updateTrip(updatedTrip)
        }
    }
    
    override suspend fun deleteEventFromTrip(tripId: String, eventId: String) {
        val trip = tripsList.find { it.id == tripId }
        if (trip != null) {
            val updatedTrip = trip.copy(events = trip.events.filter { it.id != eventId })
            updateTrip(updatedTrip)
        }
    }
    
    override suspend fun updateEventInTrip(tripId: String, eventId: String, updated: Event) {
        val trip = tripsList.find { it.id == tripId }
        if (trip != null) {
            val updatedEvents = trip.events.map { if (it.id == eventId) updated else it }
            val updatedTrip = trip.copy(events = updatedEvents)
            updateTrip(updatedTrip)
        }
    }
    
    override suspend fun addMemberToTrip(tripId: String, userId: String) {
        // Mock implementation
    }
    
    override suspend fun removeMemberFromTrip(tripId: String, userId: String) {
        // Mock implementation
    }
}

/**
 * Mock UserDataSource for testing
 * Implements UserDataSource interface directly to avoid HTTP dependencies
 */
class MockUserDataSource : UserDataSource {
    private var currentUser: User? = null
    
    fun setCurrentUser(user: User) {
        currentUser = user
    }
    
    override suspend fun getCurrentUser(): User {
        return currentUser ?: TestDataFactory.createTestUser("Test User")
    }
    
    override suspend fun getAllUsers(): List<User> {
        return currentUser?.let { listOf(it) } ?: emptyList()
    }
    
    override suspend fun getUserById(id: String): User? {
        return if (currentUser?.name == id) currentUser else null
    }
}

