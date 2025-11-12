package org.example.project.service

import org.example.project.Trip
import org.example.project.repository.TripRepository

/**
 * Service layer for Trip business logic.
 * Handles validation, business rules, and coordinates between routes and repository.
 */
class TripService(private val tripRepository: TripRepository) {
    
    /**
     * Get all trips.
     */
    suspend fun getAllTrips(): List<Trip> {
        return tripRepository.allTrips()
    }
    
    /**
     * Get a specific trip by ID.
     */
    suspend fun getTripById(id: Int): Trip? {
        return tripRepository.getTripById(id)
    }
    
    /**
     * Create a new trip with validation.
     */
    suspend fun createTrip(trip: Trip): Result<Trip> {
        // Validation
        if (trip.tripTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }
        if (trip.tripLocation.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip location cannot be empty"))
        }
        
        // TODO: Add more validation (date validation, etc.)
        
        return try {
            val createdTrip = tripRepository.addTrip(trip)
            Result.success(createdTrip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing trip.
     */
    suspend fun updateTrip(id: Int, trip: Trip): Result<Boolean> {
        // Validation
        if (trip.tripTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Trip title cannot be empty"))
        }
        
        return try {
            val updated = tripRepository.updateTrip(id, trip)
            if (updated) {
                Result.success(true)
            } else {
                Result.failure(NoSuchElementException("Trip with ID $id not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a trip by ID.
     */
    suspend fun deleteTrip(id: Int): Result<Boolean> {
        return try {
            val deleted = tripRepository.deleteTrip(id)
            if (deleted) {
                Result.success(true)
            } else {
                Result.failure(NoSuchElementException("Trip with ID $id not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

