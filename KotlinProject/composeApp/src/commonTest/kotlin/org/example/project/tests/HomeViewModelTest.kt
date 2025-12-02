package org.example.project.tests

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.example.project.data.repository.TripRepository
import org.example.project.data.repository.UserRepository
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.User
import org.example.project.model.dataClasses.Event
import org.example.project.presentation.home.HomeViewModel

/**
 * HomeViewModelTest
 * 
 * PURPOSE:
 * Tests the HomeViewModel to ensure the home screen works correctly.
 * 
 * TEST CASES:
 * 
 * 1. testLoadTrips_UpdatesState
 *    - Verifies that trips are loaded from the repository
 *    - Ensures the UI displays the correct list of trips
 * 
 * 2. testLoadTrips_EmptyList_ShowsEmptyState
 *    - Verifies that when no trips exist, the UI shows an empty state
 * 
 * 3. testCurrentUser_LoadedCorrectly
 *    - Verifies that the current user's name is loaded and displayed
 */
class HomeViewModelTest {
    
    @Test
    fun testLoadTrips_UpdatesState() = runTest {
        // Given: Mock repository with test trips
        val testTrips = TestDataFactory.createTestTrips(3)
        val mockTripDataSource = MockTripDataSource()
        mockTripDataSource.setTrips(testTrips)
        val mockUserDataSource = MockUserDataSource()
        val tripRepository = TripRepository(mockTripDataSource)
        val userRepository = UserRepository(mockUserDataSource)
        
        // When: ViewModel is created and trips are loaded
        val viewModel = HomeViewModel(tripRepository, userRepository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // Then: Trips should be in state
        val state = viewModel.uiState.value
        assertTrue(state.trips.size >= 3, "Should have at least 3 trips")
        assertFalse(state.isLoading, "Should not be loading after trips are loaded")
        assertNull(state.errorMessage, "Should have no error")
    }
    
    @Test
    fun testLoadTrips_EmptyList_ShowsEmptyState() = runTest {
        // Given: Mock repository with no trips
        val mockTripDataSource = MockTripDataSource()
        mockTripDataSource.setTrips(emptyList())
        val mockUserDataSource = MockUserDataSource()
        val tripRepository = TripRepository(mockTripDataSource)
        val userRepository = UserRepository(mockUserDataSource)
        
        // When: ViewModel is created
        val viewModel = HomeViewModel(tripRepository, userRepository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // Then: Should show empty state
        val state = viewModel.uiState.value
        assertTrue(state.trips.isEmpty(), "Should have no trips")
        assertFalse(state.isLoading, "Should not be loading")
    }
    
    @Test
    fun testCurrentUser_LoadedCorrectly() = runTest {
        // Given: Mock user repository with test user
        val testUser = TestDataFactory.createTestUser("John Doe")
        val mockTripDataSource = MockTripDataSource()
        val mockUserDataSource = MockUserDataSource()
        mockUserDataSource.setCurrentUser(testUser)
        val tripRepository = TripRepository(mockTripDataSource)
        val userRepository = UserRepository(mockUserDataSource)
        
        // When: ViewModel is created
        val viewModel = HomeViewModel(tripRepository, userRepository)
        
        // Wait for user to load
        kotlinx.coroutines.delay(200)
        
        // Then: Current user should be loaded
        val state = viewModel.uiState.value
        assertNotNull(state.currentUser, "Should have current user")
        assertEquals("John Doe", state.currentUser?.name, "User name should match")
    }
}


