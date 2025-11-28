package org.example.project.data.remote


import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.data.source.TripDataSource
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.Duration
import org.example.project.model.dataClasses.Location

// Response DTOs matching server-side models
@Serializable
private data class TripResponse(
    val id: Int,
    @SerialName("trip_title")
    val tripTitle: String?,
    @SerialName("trip_description")
    val tripDescription: String?,
    @SerialName("trip_location")
    val tripLocation: Location?,
    @SerialName("trip_duration")
    val tripDuration: Duration,
    @SerialName("user_id")
    val userId: Int?
)

@Serializable
private data class TripRetrieveResponse(
    val message: String,
    val data: TripResponse
)

@Serializable
private data class TripListResponse(
    val message: String,
    val trips: List<TripResponse>
)

@Serializable
private data class TripCreateRequest(
    @SerialName("trip_title")
    val tripTitle: String?,
    @SerialName("trip_description")
    val tripDescription: String?,
    @SerialName("trip_location")
    val tripLocation: Location?,
    @SerialName("trip_duration")
    val tripDuration: Duration,
    @SerialName("user_id")
    val userId: Int
)

// Event DTOs matching server-side models
@Serializable
private data class LocationResponse(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val title: String?,
    @SerialName("event_id")
    val eventId: Int
)

@Serializable
private data class EventResponse(
    val id: Int,
    @SerialName("event_title")
    val eventTitle: String?,
    @SerialName("event_description")
    val eventDescription: String?,
    @SerialName("event_location")
    val eventLocation: Location?,
    @SerialName("event_duration")
    val eventDuration: Duration,
    @SerialName("trip_id")
    val tripId: Int?,
    val location: LocationResponse?
)

@Serializable
private data class EventRetrieveResponse(
    val message: String,
    val data: EventResponse
)

@Serializable
private data class EventListResponse(
    val message: String,
    val events: List<EventResponse>
)

@Serializable
private data class EventCreateRequest(
    @SerialName("event_title")
    val eventTitle: String?,
    @SerialName("event_description")
    val eventDescription: String?,
    @SerialName("event_location")
    val eventLocation: Location?,
    @SerialName("event_duration")
    val eventDuration: Duration,
    @SerialName("trip_id")
    val tripId: Int
)

class RemoteTripDataSource(
    private val userDataSource: RemoteUserDataSource
) : TripDataSource {
    private val apiBaseUrl = "http://localhost:8080"
    
    private fun getCurrentUserId(): Int = userDataSource.currentUserId

    // Helper function to convert server TripResponse to client Trip model
    private fun TripResponse.toTrip(): Trip {
        val now = kotlinx.datetime.Clock.System.now()
        val currentDate = now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        
        return Trip(
            id = id.toString(),
            title = tripTitle ?: "",
            description = tripDescription ?: "",
            location = tripLocation,
            duration = tripDuration,
            users = emptyList(), // TODO: Fetch associated users
            events = emptyList(), // TODO: Fetch associated events
            imageHeaderUrl = null,
            createdDate = currentDate
        )
    }

    // Helper function to convert server EventResponse to client Event model
    private fun EventResponse.toEvent(): Event {
        return Event(
            id = id.toString(),
            title = eventTitle ?: "",
            description = eventDescription ?: "",
            location = if (location != null) {
                Location(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    address = location.address,
                    title = location.title
                )
            } else if (eventLocation != null) {
                // Fallback to just address if no GPS coordinates
                Location(
                    latitude = 0.0,
                    longitude = 0.0,
                    address = null
                )
            } else null,
            duration = eventDuration,
            imageUrl = null
        )
    }

    // USES MOCK ENDPOINTS (deprecated - kept for backward compatibility)
    // suspend fun fetchTrips(): List<Trip> {
    //     return HttpClientProvider.client.get("$apiBaseUrl/mocktrips").body()
    // }

    suspend fun helloWorld(): String {
        return HttpClientProvider.client.get(apiBaseUrl).body()
    }

    // TripDataSource interface implementations using real endpoints
    override suspend fun getAllTrips(): List<Trip> {
        val response: TripListResponse = HttpClientProvider.client
            .get("$apiBaseUrl/user/${getCurrentUserId()}/trip")
            .body()
        return response.trips.map { it.toTrip() }
    }

    override suspend fun getTripById(id: String): Trip? {
        return try {
            // Fetch the trip
            val tripResponse: TripRetrieveResponse = HttpClientProvider.client
                .get("$apiBaseUrl/user/${getCurrentUserId()}/trip/$id")
                .body()
            
            // Fetch the events for this trip
            val eventsResponse: EventListResponse = HttpClientProvider.client
                .get("$apiBaseUrl/user/${getCurrentUserId()}/trip/$id/event")
                .body()
            
            // Convert trip and include the fetched events
            val trip = tripResponse.data.toTrip()
            trip.copy(events = eventsResponse.events.map { it.toEvent() })
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun insertTrip(trip: Trip): Trip {
        val request = TripCreateRequest(
            tripTitle = trip.title,
            tripDescription = trip.description,
            tripLocation = trip.location,
            tripDuration = trip.duration,
            userId = getCurrentUserId()
        )
        
        println("RemoteTripDataSource: URL: $apiBaseUrl/user/${getCurrentUserId()}/trip")
        
        val response: TripRetrieveResponse = HttpClientProvider.client.post("$apiBaseUrl/user/${getCurrentUserId()}/trip") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        
        println("RemoteTripDataSource: Trip created successfully: ${response.data}")
        return response.data.toTrip()
    }

    override suspend fun updateTrip(trip: Trip): Trip {
        val request = TripCreateRequest(
            tripTitle = trip.title,
            tripDescription = trip.description,
            tripLocation = trip.location,
            tripDuration = trip.duration,
            userId = getCurrentUserId()
        )
        
        HttpClientProvider.client.put("$apiBaseUrl/user/${getCurrentUserId()}/trip/${trip.id}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return trip
    }

    override suspend fun deleteTrip(tripId: String) {
        HttpClientProvider.client.delete("$apiBaseUrl/user/${getCurrentUserId()}/trip/$tripId")
    }

    override suspend fun addEventToTrip(tripId: String, event: Event) {
        val request = EventCreateRequest(
            eventTitle = event.title,
            eventDescription = event.description,
            eventLocation = event.location,
            eventDuration = event.duration,
            tripId = tripId.toInt()
        )
        
        HttpClientProvider.client.post("$apiBaseUrl/user/${getCurrentUserId()}/trip/$tripId/event") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun deleteEventFromTrip(tripId: String, eventId: String) {
        HttpClientProvider.client.delete("$apiBaseUrl/user/${getCurrentUserId()}/trip/$tripId/event/$eventId")
    }

    override suspend fun updateEventInTrip(tripId: String, eventId: String, updated: Event) {
        val request = EventCreateRequest(
            eventTitle = updated.title,
            eventDescription = updated.description,
            eventLocation = updated.location,
            eventDuration = updated.duration,
            tripId = tripId.toInt()
        )
        
        HttpClientProvider.client.put("$apiBaseUrl/user/${getCurrentUserId()}/trip/$tripId/event/$eventId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun addMemberToTrip(tripId: String, userId: String) {
        // TODO: Implement when member endpoints are available
        throw NotImplementedError("Member endpoints not yet implemented on server")
    }

    override suspend fun removeMemberFromTrip(tripId: String, userId: String) {
        // TODO: Implement when member endpoints are available
        throw NotImplementedError("Member endpoints not yet implemented on server")
    }
}