package org.example.project.tests

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.example.project.model.dataClasses.Duration
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.User

/**
 * Test data factory for creating mock data in tests
 */
object TestDataFactory {
    
    fun createTestUser(name: String = "Test User"): User {
        return User(name = name, pfpUrl = null)
    }
    
    fun createTestDuration(
        startDate: LocalDate = LocalDate(2025, 7, 1),
        startTime: LocalTime = LocalTime(9, 0),
        endDate: LocalDate = LocalDate(2025, 7, 5),
        endTime: LocalTime = LocalTime(18, 0)
    ): Duration {
        return Duration(
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime
        )
    }
    
    fun createTestEvent(
        id: String = "event_1",
        title: String = "Test Event",
        description: String = "Test Description",
        location: String = "Test Location",
        startDate: LocalDate = LocalDate(2025, 7, 1),
        startTime: LocalTime = LocalTime(10, 0),
        endDate: LocalDate = LocalDate(2025, 7, 1),
        endTime: LocalTime = LocalTime(12, 0)
    ): Event {
        return Event(
            id = id,
            title = title,
            duration = Duration(
                startDate = startDate,
                startTime = startTime,
                endDate = endDate,
                endTime = endTime
            ),
            description = description,
            location = location
        )
    }
    
    fun createTestTrip(
        id: String = "trip_1",
        title: String = "Test Trip",
        description: String = "Test Description",
        location: String = "Test Location",
        users: List<User> = listOf(createTestUser("Test User")),
        events: List<Event> = emptyList(),
        startDate: LocalDate = LocalDate(2025, 7, 1),
        endDate: LocalDate = LocalDate(2025, 7, 5)
    ): Trip {
        return Trip(
            id = id,
            title = title,
            duration = createTestDuration(startDate = startDate, endDate = endDate),
            description = description,
            location = location,
            users = users,
            events = events,
            createdDate = startDate
        )
    }
    
    fun createTestTrips(count: Int): List<Trip> {
        return (1..count).map { index ->
            createTestTrip(
                id = "trip_$index",
                title = "Test Trip $index"
            )
        }
    }
}

