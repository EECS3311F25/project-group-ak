package org.example.project.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.data.remote.RemoteTripDataSource
import org.example.project.model.dataClasses.Duration
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.Event

// USES ONLY REMOTE DATA SOURCE. NO LOCAL DB => NO LOCAL SOURCE
class TripRepository(
    private val remoteDataSource: RemoteTripDataSource
) {
    // StateFlow for reactive updates
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Local cache for created/updated trips (until real backend persistence exists)
    private val localCreatedTrips = mutableMapOf<String, Trip>()
    
    // Load initial data
    init {
        CoroutineScope(Dispatchers.Default).launch {
            refreshTrips()
        }
    }
    
    suspend fun getAllTrips(): List<Trip> {
        return try {
            val remoteTrips = remoteDataSource.fetchTrips()
            // Merge remote trips with locally created trips
            val allTrips = (remoteTrips + localCreatedTrips.values).distinctBy { it.id }
            allTrips
        } catch (e: Exception) {
            println("Failed to fetch remote trips: ${e.message}")
            e.printStackTrace()
            // Return at least the locally created trips
            localCreatedTrips.values.toList()
        }
    }
    
    suspend fun getTripById(id: String): Trip? {
        // Check local cache first
        localCreatedTrips[id]?.let { return it }
        
        // Then check remote
        return try {
            remoteDataSource.getAllTrips().find { it.id == id }
        } catch (e: Exception) {
            println("Failed to fetch trip by id: ${e.message}")
            null
        }
    }
    
    suspend fun createTrip(trip: Trip): Result<Trip> {
        _isLoading.value = true
        _error.value = null
        
        return try {
            val createdTrip = remoteDataSource.insertTrip(trip)
            // 🔥 Store in local cache
            localCreatedTrips[createdTrip.id] = createdTrip
            // 🔥 Key: Update StateFlow so all screens automatically refresh
            refreshTrips()
            _isLoading.value = false
            Result.success(createdTrip)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }
    
    suspend fun updateTrip(trip: Trip): Result<Trip> {
        _isLoading.value = true
        _error.value = null
        
        return try {
            val updatedTrip = remoteDataSource.updateTrip(trip)
            // 🔥 Update local cache if it exists there
            if (localCreatedTrips.containsKey(updatedTrip.id)) {
                localCreatedTrips[updatedTrip.id] = updatedTrip
            }
            refreshTrips()
            _isLoading.value = false
            Result.success(updatedTrip)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }
    
    suspend fun deleteTrip(tripId: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        
        return try {
            remoteDataSource.deleteTrip(tripId)
            // 🔥 Remove from local cache
            localCreatedTrips.remove(tripId)
            refreshTrips()
            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }
    
    // 🔥 This method triggers updates to all subscribers
    private suspend fun refreshTrips() {
        _trips.value = try {
            val remoteTrips = remoteDataSource.fetchTrips()
            // Merge remote trips with locally created trips
            (remoteTrips + localCreatedTrips.values).distinctBy { it.id }
        } catch (e: Exception) {
            println("Failed to refresh trips: ${e.message}")
            // Return at least the locally created trips
            localCreatedTrips.values.toList()
        }
    }
    
    // Manual refresh method for pull-to-refresh
    suspend fun refresh() {
        _isLoading.value = true
        _error.value = null
        try {
            refreshTrips()
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
        }
    }

    suspend fun addEvent(tripId: String, event: Event): Result<Event> {
        _isLoading.value = true
        _error.value = null
        return try {
            // Check local cache first, then remote
            val trip = localCreatedTrips[tripId] ?: remoteDataSource.getTripById(tripId)
                ?: throw IllegalArgumentException("Trip not found")
            trip.events.firstOrNull { it.duration.conflictsWith(event.duration) }?.let { conflicting ->
                val conflictingRange = "${conflicting.duration.startDate} ${conflicting.duration.startTime} - " +
                    "${conflicting.duration.endDate} ${conflicting.duration.endTime}"
                throw IllegalArgumentException(
                    "Overlaps with existing event ${conflicting.title} ($conflictingRange)"
                )
            }
            remoteDataSource.addEventToTrip(tripId, event)
            
            // 🔥 Update local cache if trip exists there
            if (localCreatedTrips.containsKey(tripId)) {
                val updatedTrip = trip.copy(events = trip.events + event)
                localCreatedTrips[tripId] = updatedTrip
            }
            
            refreshTrips()
            _isLoading.value = false
            Result.success(event)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(tripId: String, eventId: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return try {
            remoteDataSource.deleteEventFromTrip(tripId, eventId)
            
            // 🔥 Update local cache if trip exists there
            if (localCreatedTrips.containsKey(tripId)) {
                val trip = localCreatedTrips[tripId]!!
                val updatedTrip = trip.copy(events = trip.events.filter { it.id != eventId })
                localCreatedTrips[tripId] = updatedTrip
            }
            
            refreshTrips()
            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }

    suspend fun updateEvent(tripId: String, eventId: String, updated: Event): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return try {
            remoteDataSource.updateEventInTrip(tripId, eventId, updated)
            
            // 🔥 Update local cache if trip exists there
            if (localCreatedTrips.containsKey(tripId)) {
                val trip = localCreatedTrips[tripId]!!
                val updatedEvents = trip.events.map { if (it.id == eventId) updated else it }
                val updatedTrip = trip.copy(events = updatedEvents)
                localCreatedTrips[tripId] = updatedTrip
            }
            
            refreshTrips()
            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }

    suspend fun addMember(tripId: String, userId: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return try {
            remoteDataSource.addMemberToTrip(tripId, userId)
            refreshTrips()
            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }

    suspend fun removeMember(tripId: String, userId: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return try {
            remoteDataSource.removeMemberFromTrip(tripId, userId)
            refreshTrips()
            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }
       // TODO: FIX AND ADD
//    suspend fun updateTripImage(tripId: String, imageUrl: String): Result<Unit> {
//        return updateTripField(tripId) { it.copy(imageUrl = imageUrl) }
//    }
//
//    suspend fun updateTripDuration(tripId: String, durationMinutes: Int): Result<Unit> {
//        return updateTripField(tripId) { it.copy(durationMinutes = durationMinutes) }
//    }

    suspend fun updateTripTitle(tripId: String, title: String): Result<Unit> {
        return updateTripField(tripId) { it.copy(title = title) }
    }

    suspend fun updateTripDuration(tripId: String, duration: Duration): Result<Unit> {
        return updateTripField(tripId) { it.copy(duration = duration) }
    }

    suspend fun updateTripDetails(
        tripId: String,
        title: String,
        duration: Duration,
        imageHeaderUrl: String?,
        description: String
    ): Result<Unit> {
        val normalizedImageUrl = imageHeaderUrl?.takeIf { it.isNotBlank() }
        return updateTripField(tripId) {
            it.copy(
                title = title,
                duration = duration,
                imageHeaderUrl = normalizedImageUrl,
                description = description
            )
        }
    }

    suspend fun updateTripDescription(tripId: String, description: String): Result<Unit> {
        return updateTripField(tripId) { it.copy(description = description) }
    }

    private suspend fun updateTripField(tripId: String, update: (Trip) -> Trip): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return try {
            val trip = remoteDataSource.getTripById(tripId) ?: return Result.failure(Exception("Trip not found"))
            remoteDataSource.updateTrip(update(trip))
            refreshTrips()
            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            Result.failure(e)
        }
    }

}
