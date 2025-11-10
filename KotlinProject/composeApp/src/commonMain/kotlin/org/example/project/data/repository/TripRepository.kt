package org.example.project.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.model.Trip
import org.example.project.data.source.TripDataSource

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
}