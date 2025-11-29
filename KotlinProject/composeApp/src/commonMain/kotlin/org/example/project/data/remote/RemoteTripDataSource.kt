package org.example.project.data.remote


import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.example.project.data.source.TripDataSource
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip

class RemoteTripDataSource : TripDataSource {
    private val apiBaseUrl = "http://localhost:8080"

    // USES MOCK ENDPOINTS
    override suspend fun fetchTrips(): List<Trip> {
        return HttpClientProvider.client.get("$apiBaseUrl/mocktrips").body()
    }

    suspend fun helloWorld(): String {
        return HttpClientProvider.client.get(apiBaseUrl).body()
    }

    // TripDataSource interface implementations
    override suspend fun getAllTrips(): List<Trip> {
        // Uses the mock endpoint
        return fetchTrips()
    }

    override suspend fun getTripById(id: String): Trip? {
        // Mock: Find trip from the list by ID
        return fetchTrips().find { it.id == id }
    }

    override suspend fun insertTrip(trip: Trip): Trip {
        // Mock: Just return the trip as if it was successfully created
        return trip
    }

    override suspend fun updateTrip(trip: Trip): Trip {
        // Mock: Just return the updated trip as if it was successfully saved
        return trip
    }

    override suspend fun deleteTrip(tripId: String) {
        // Mock: Simulate successful deletion (no-op)
    }

    override suspend fun addEventToTrip(tripId: String, event: Event) {
        // Mock: Simulate successful event addition (no-op)
    }

    override suspend fun deleteEventFromTrip(tripId: String, eventId: String) {
        // Mock: Simulate successful event deletion (no-op)
    }

    override suspend fun updateEventInTrip(tripId: String, eventId: String, updated: Event) {
        // Mock: Simulate successful event update (no-op)
    }

    override suspend fun addMemberToTrip(tripId: String, userId: String) {
        // Mock: Simulate successful member addition (no-op)
    }

    override suspend fun removeMemberFromTrip(tripId: String, userId: String) {
        // Mock: Simulate successful member removal (no-op)
    }
}