package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.model.Trip
import org.example.project.data.source.TripDataSource

class TripRepository(
    private val localDataSource: TripDataSource,
    private val remoteDataSource: TripDataSource? = null // For future API integration
) {
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: Flow<List<Trip>> = _trips.asStateFlow()
    
    suspend fun getAllTrips(): List<Trip> {
        return localDataSource.getAllTrips()
    }
    
    suspend fun getTripById(id: String): Trip? {
        return localDataSource.getTripById(id)
    }
    
    suspend fun createTrip(trip: Trip): Result<Trip> {
        return try {
            val createdTrip = localDataSource.insertTrip(trip)
            refreshTrips()
            Result.success(createdTrip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTrip(trip: Trip): Result<Trip> {
        return try {
            val updatedTrip = localDataSource.updateTrip(trip)
            refreshTrips()
            Result.success(updatedTrip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTrip(tripId: String): Result<Unit> {
        return try {
            localDataSource.deleteTrip(tripId)
            refreshTrips()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun refreshTrips() {
        _trips.value = localDataSource.getAllTrips()
    }
}