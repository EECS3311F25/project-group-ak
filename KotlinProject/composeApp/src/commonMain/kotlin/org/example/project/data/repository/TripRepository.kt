package org.example.project.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.model.Trip
import org.example.project.data.source.TripDataSource
import org.example.project.model.Event

class TripRepository(
    private val localDataSource: TripDataSource
) {
    // StateFlow for reactive updates
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Load initial data
    init {
        CoroutineScope(Dispatchers.Default).launch {
            refreshTrips()
        }
    }
    
    suspend fun getAllTrips(): List<Trip> {
        return localDataSource.getAllTrips()
    }
    
    suspend fun getTripById(id: String): Trip? {
        return localDataSource.getTripById(id)
    }
    
    suspend fun createTrip(trip: Trip): Result<Trip> {
        _isLoading.value = true
        _error.value = null
        
        return try {
            val createdTrip = localDataSource.insertTrip(trip)
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
            val updatedTrip = localDataSource.updateTrip(trip)
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
            localDataSource.deleteTrip(tripId)
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
        _trips.value = localDataSource.getAllTrips()
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
            val updatedTrip = localDataSource.addEventToTrip(tripId, event)
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
            localDataSource.deleteEventFromTrip(tripId, eventId)
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
            val event = localDataSource.updateEventInTrip(tripId, eventId, updated)
            refreshTrips()
            _isLoading.value = false
            Result.success(event)
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
            localDataSource.addMemberToTrip(tripId, userId)
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
            localDataSource.removeMemberFromTrip(tripId, userId)
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

    suspend fun updateTripDescription(tripId: String, description: String): Result<Unit> {
        return updateTripField(tripId) { it.copy(description = description) }
    }

    private suspend fun updateTripField(tripId: String, update: (Trip) -> Trip): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return try {
            val trip = localDataSource.getTripById(tripId) ?: return Result.failure(Exception("Trip not found"))
            localDataSource.updateTrip(update(trip))
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