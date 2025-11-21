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
    suspend fun fetchTrips(): List<Trip> {
        return HttpClientProvider.client.get("$apiBaseUrl/mocktrips").body()
    }

    suspend fun helloWorld(): String {
        return HttpClientProvider.client.get(apiBaseUrl).body()
    }

    // TripDataSource interface implementations (scaffolded with TODOs)
    override suspend fun getAllTrips(): List<Trip> {
        TODO("Implement remote getAllTrips")
    }

    override suspend fun getTripById(id: String): Trip? {
        TODO("Implement remote getTripById")
    }

    override suspend fun insertTrip(trip: Trip): Trip {
        TODO("Implement remote insertTrip")
    }

    override suspend fun updateTrip(trip: Trip): Trip {
        TODO("Implement remote updateTrip")
    }

    override suspend fun deleteTrip(tripId: String) {
        TODO("Implement remote deleteTrip")
    }

    override suspend fun addEventToTrip(tripId: String, event: Event) {
        TODO("Implement remote addEventToTrip")
    }

    override suspend fun deleteEventFromTrip(tripId: String, eventId: String) {
        TODO("Implement remote deleteEventFromTrip")
    }

    override suspend fun updateEventInTrip(tripId: String, eventId: String, updated: Event) {
        TODO("Implement remote updateEventInTrip")
    }

    override suspend fun addMemberToTrip(tripId: String, userId: String) {
        TODO("Implement remote addMemberToTrip")
    }

    override suspend fun removeMemberFromTrip(tripId: String, userId: String) {
        TODO("Implement remote removeMemberFromTrip")
    }
}