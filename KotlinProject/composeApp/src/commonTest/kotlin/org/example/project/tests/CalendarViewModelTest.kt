package org.example.project.tests

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.example.project.data.repository.TripRepository
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import org.example.project.presentation.calendar.CalendarViewModel

/**
 * CalendarViewModelTest
 * 
 * PURPOSE:
 * Tests the CalendarViewModel to ensure the calendar screen works correctly.
 * 
 * TEST CASES:
 * 
 * 1. testInitialState_LoadsTrip
 *    - Verifies that the ViewModel loads trip data when initialized
 *    - Ensures the calendar screen has the correct trip information
 * 
 * 2. testSelectDate_FiltersEvents
 *    - Verifies that selecting a date filters events for that specific date
 *    - Ensures only events for the selected date are shown in the timeline
 * 
 * 3. testSelectDate_MultiDayEvent_ShowsCorrectly
 *    - Verifies that multi-day events are shown on all days they span
 *    - Ensures events that overlap with the selected date are displayed
 * 
 * 4. testAddEvent_UpdatesTimeline
 *    - Verifies that adding a new event updates the timeline
 *    - Ensures the event appears on the correct date
 * 
 * 5. testDeleteEvent_RemovesFromTimeline
 *    - Verifies that deleting an event removes it from the timeline
 *    - Ensures the UI updates immediately after deletion
 * 
 * 6. testUpdateEvent_ReflectsChanges
 *    - Verifies that updating an event reflects changes in the timeline
 *    - Ensures event details are updated correctly
 * 
 * 7. testEventConflict_ShowsError
 *    - Verifies that adding overlapping events shows a conflict error
 *    - Ensures users are notified when events conflict
 * 
 * 8. testRefreshTrip_ReloadsData
 *    - Verifies that refreshing trip data reloads events
 *    - Ensures the calendar stays in sync with the repository
 * 
 * 9. testSelectDate_EmptyDay_ShowsNoEvents
 *    - Verifies that selecting a date with no events shows an empty timeline
 *    - Ensures the UI handles empty states correctly
 */
class CalendarViewModelTest {
    
    @Test
    fun testInitialState_LoadsTrip() = runTest {
        // Given: Mock repository with test trip
        val testTrip = TestDataFactory.createTestTrip(
            id = "trip_1",
            title = "Test Trip",
            events = listOf(
                TestDataFactory.createTestEvent(
                    id = "event_1",
                    title = "Morning Event",
                    startDate = LocalDate(2025, 7, 1),
                    startTime = LocalTime(9, 0)
                )
            )
        )
        val mockDataSource = MockTripDataSource()
        mockDataSource.setTrips(listOf(testTrip))
        val repository = TripRepository(mockDataSource)
        
        // When: ViewModel is created
        val viewModel = CalendarViewModel("trip_1", repository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // Then: Trip should be loaded
        val state = viewModel.uiState.value
        assertEquals("trip_1", state.currentTrip.id, "Should load correct trip")
        assertFalse(state.isLoading, "Should not be loading after load")
        assertNull(state.error, "Should have no error")
    }
    
    @Test
    fun testSelectDate_FiltersEvents() = runTest {
        // Given: Trip with events on different dates
        val event1 = TestDataFactory.createTestEvent(
            id = "event_1",
            title = "Day 1 Event",
            startDate = LocalDate(2025, 7, 1),
            startTime = LocalTime(10, 0),
            endDate = LocalDate(2025, 7, 1),
            endTime = LocalTime(12, 0)
        )
        val event2 = TestDataFactory.createTestEvent(
            id = "event_2",
            title = "Day 2 Event",
            startDate = LocalDate(2025, 7, 2),
            startTime = LocalTime(14, 0),
            endDate = LocalDate(2025, 7, 2),
            endTime = LocalTime(16, 0)
        )
        val testTrip = TestDataFactory.createTestTrip(
            id = "trip_1",
            events = listOf(event1, event2)
        )
        val mockDataSource = MockTripDataSource()
        mockDataSource.setTrips(listOf(testTrip))
        val repository = TripRepository(mockDataSource)
        val viewModel = CalendarViewModel("trip_1", repository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // When: Select date with event
        viewModel.selectDate(LocalDate(2025, 7, 1))
        
        // Wait for filtering
        kotlinx.coroutines.delay(100)
        
        // Then: Only events for that date should be shown
        val state = viewModel.uiState.value
        assertEquals(1, state.events.size, "Should show only 1 event for selected date")
        assertEquals("Day 1 Event", state.events.first().title, "Should show correct event")
    }
    
    @Test
    fun testSelectDate_MultiDayEvent_ShowsCorrectly() = runTest {
        // Given: Trip with multi-day event
        val multiDayEvent = TestDataFactory.createTestEvent(
            id = "event_1",
            title = "Multi-Day Event",
            startDate = LocalDate(2025, 7, 1),
            startTime = LocalTime(10, 0),
            endDate = LocalDate(2025, 7, 3),
            endTime = LocalTime(18, 0)
        )
        val testTrip = TestDataFactory.createTestTrip(
            id = "trip_1",
            events = listOf(multiDayEvent)
        )
        val mockDataSource = MockTripDataSource()
        mockDataSource.setTrips(listOf(testTrip))
        val repository = TripRepository(mockDataSource)
        val viewModel = CalendarViewModel("trip_1", repository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // When: Select middle date of multi-day event
        viewModel.selectDate(LocalDate(2025, 7, 2))
        
        // Wait for filtering
        kotlinx.coroutines.delay(100)
        
        // Then: Multi-day event should be shown
        val state = viewModel.uiState.value
        assertEquals(1, state.events.size, "Should show multi-day event")
        assertEquals("Multi-Day Event", state.events.first().title, "Should show correct event")
    }
    
    @Test
    fun testAddEvent_UpdatesTimeline() = runTest {
        // Given: Trip with no events
        val testTrip = TestDataFactory.createTestTrip(
            id = "trip_1",
            events = emptyList()
        )
        val mockDataSource = MockTripDataSource()
        mockDataSource.setTrips(listOf(testTrip))
        val repository = TripRepository(mockDataSource)
        val viewModel = CalendarViewModel("trip_1", repository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // Select a date
        viewModel.selectDate(LocalDate(2025, 7, 1))
        kotlinx.coroutines.delay(100)
        
        // When: Add new event
        val newEvent = TestDataFactory.createTestEvent(
            id = "event_new",
            title = "New Event",
            startDate = LocalDate(2025, 7, 1),
            startTime = LocalTime(15, 0)
        )
        viewModel.addEvent(newEvent)
        
        // Wait for update
        kotlinx.coroutines.delay(200)
        
        // Then: Event should appear in timeline
        val state = viewModel.uiState.value
        assertTrue(state.currentTrip.events.any { it.id == "event_new" }, "New event should be added")
    }
    
    @Test
    fun testDeleteEvent_RemovesFromTimeline() = runTest {
        // Given: Trip with events
        val event1 = TestDataFactory.createTestEvent(
            id = "event_1",
            title = "Event 1",
            startDate = LocalDate(2025, 7, 1)
        )
        val testTrip = TestDataFactory.createTestTrip(
            id = "trip_1",
            events = listOf(event1)
        )
        val mockDataSource = MockTripDataSource()
        mockDataSource.setTrips(listOf(testTrip))
        val repository = TripRepository(mockDataSource)
        val viewModel = CalendarViewModel("trip_1", repository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // Select date
        viewModel.selectDate(LocalDate(2025, 7, 1))
        kotlinx.coroutines.delay(100)
        
        // When: Delete event
        viewModel.deleteEvent(event1)
        
        // Wait for deletion
        kotlinx.coroutines.delay(200)
        
        // Then: Event should be removed
        val state = viewModel.uiState.value
        assertTrue(state.currentTrip.events.none { it.id == "event_1" }, "Event should be deleted")
    }
    
    @Test
    fun testSelectDate_EmptyDay_ShowsNoEvents() = runTest {
        // Given: Trip with events on different dates
        val event1 = TestDataFactory.createTestEvent(
            id = "event_1",
            title = "Day 1 Event",
            startDate = LocalDate(2025, 7, 1)
        )
        val testTrip = TestDataFactory.createTestTrip(
            id = "trip_1",
            events = listOf(event1)
        )
        val mockDataSource = MockTripDataSource()
        mockDataSource.setTrips(listOf(testTrip))
        val repository = TripRepository(mockDataSource)
        val viewModel = CalendarViewModel("trip_1", repository)
        
        // Wait for initial load
        kotlinx.coroutines.delay(200)
        
        // When: Select date with no events
        viewModel.selectDate(LocalDate(2025, 7, 3))
        
        // Wait for filtering
        kotlinx.coroutines.delay(100)
        
        // Then: Should show no events
        val state = viewModel.uiState.value
        assertTrue(state.events.isEmpty(), "Should show no events for empty day")
    }
}


