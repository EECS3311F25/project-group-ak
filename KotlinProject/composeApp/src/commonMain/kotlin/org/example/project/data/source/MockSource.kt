package org.example.project.data.source

import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.User
import org.example.project.model.dataClasses.Duration
import org.example.project.model.dataClasses.Event
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class LocalTripDataSource : TripDataSource {
    
    // Mock in-memory storage (replace with Room/SQLDelight later)
    private val trips = mutableListOf<Trip>(
        Trip(
            id = "trip_summer_getaway",
            title = "Summer Getaway",
            description = "Road trip across Ontario",
            location = "Toronto to Ottawa",
            duration = Duration(
                startDate = LocalDate(2025, 7, 1),
                startTime = LocalTime(9, 0),
                endDate = LocalDate(2025, 7, 10),
                endTime = LocalTime(17, 0)
            ),
            users = listOf(User(name = "Klodiana"), User(name = "Alex")),
            events = listOf(
                Event(
                    title = "Departure from Toronto",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 1),
                        startTime = LocalTime(9, 0),
                        endDate = LocalDate(2025, 7, 1),
                        endTime = LocalTime(10, 0)
                    ),
                    description = "Start our journey from downtown Toronto",
                    location = "Toronto, ON"
                ),
                Event(
                    title = "Niagara Falls Stop",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 1),
                        startTime = LocalTime(12, 0),
                        endDate = LocalDate(2025, 7, 1),
                        endTime = LocalTime(16, 0)
                    ),
                    description = "Visit the famous Niagara Falls and take photos",
                    location = "Niagara Falls, ON"
                ),
                Event(
                    title = "Niagara Boat Tour",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 2),
                        startTime = LocalTime(10, 0),
                        endDate = LocalDate(2025, 7, 2),
                        endTime = LocalTime(11, 30)
                    ),
                    description = "Experience the falls up close on the Maid of the Mist",
                    location = "Niagara Falls, ON"
                ),
                Event(
                    title = "Table Rock Lunch",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 2),
                        startTime = LocalTime(12, 30),
                        endDate = LocalDate(2025, 7, 2),
                        endTime = LocalTime(14, 0)
                    ),
                    description = "Traditional Canadian lunch with a view",
                    location = "Table Rock, Niagara Falls"
                ),
                Event(
                    title = "Drive to Kingston",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 3),
                        startTime = LocalTime(8, 0),
                        endDate = LocalDate(2025, 7, 3),
                        endTime = LocalTime(12, 0)
                    ),
                    description = "Scenic drive along Lake Ontario",
                    location = "Highway 401"
                ),
                Event(
                    title = "Kingston Market Square",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 3),
                        startTime = LocalTime(14, 0),
                        endDate = LocalDate(2025, 7, 3),
                        endTime = LocalTime(17, 0)
                    ),
                    description = "Browse local crafts and food vendors",
                    location = "Kingston, ON"
                ),
                Event(
                    title = "Ottawa Parliament Tour",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 4),
                        startTime = LocalTime(10, 0),
                        endDate = LocalDate(2025, 7, 4),
                        endTime = LocalTime(12, 0)
                    ),
                    description = "Guided tour of Canada's Parliament buildings",
                    location = "Parliament Hill, Ottawa"
                ),
                Event(
                    title = "ByWard Market Dinner",
                    duration = Duration(
                        startDate = LocalDate(2025, 7, 4),
                        startTime = LocalTime(18, 0),
                        endDate = LocalDate(2025, 7, 4),
                        endTime = LocalTime(20, 0)
                    ),
                    description = "Farewell dinner at famous ByWard Market",
                    location = "ByWard Market, Ottawa"
                )
            ),
            imageHeaderUrl = "https://images.pexels.com/photos/1285625/pexels-photo-1285625.jpeg",
            createdDate = LocalDate(2025, 6, 1)
        ),
        Trip(
            id = "trip_european_adventure",
            title = "European Adventure",
            description = "Backpacking through Europe",
            location = "Paris to Rome",
            duration = Duration(
                startDate = LocalDate(2025, 8, 15),
                startTime = LocalTime(10, 0),
                endDate = LocalDate(2025, 8, 30),
                endTime = LocalTime(18, 0)
            ),
            users = listOf(User(name = "Alice"), User(name = "Bob")),
            events = listOf(
                Event(
                    title = "Eiffel Tower Visit",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 15),
                        startTime = LocalTime(14, 0),
                        endDate = LocalDate(2025, 8, 15),
                        endTime = LocalTime(17, 0)
                    ),
                    description = "Climb the iconic Eiffel Tower and enjoy panoramic views",
                    location = "Paris, France"
                ),
                Event(
                    title = "Louvre Museum",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 16),
                        startTime = LocalTime(10, 0),
                        endDate = LocalDate(2025, 8, 16),
                        endTime = LocalTime(15, 0)
                    ),
                    description = "See the Mona Lisa and other masterpieces",
                    location = "Paris, France"
                ),
                Event(
                    title = "Train to Amsterdam",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 17),
                        startTime = LocalTime(9, 0),
                        endDate = LocalDate(2025, 8, 17),
                        endTime = LocalTime(13, 0)
                    ),
                    description = "High-speed train through European countryside",
                    location = "Paris to Amsterdam"
                ),
                Event(
                    title = "Canal Tour Amsterdam",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 18),
                        startTime = LocalTime(11, 0),
                        endDate = LocalDate(2025, 8, 18),
                        endTime = LocalTime(13, 0)
                    ),
                    description = "Explore Amsterdam's famous canals by boat",
                    location = "Amsterdam, Netherlands"
                ),
                Event(
                    title = "Van Gogh Museum",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 19),
                        startTime = LocalTime(10, 0),
                        endDate = LocalDate(2025, 8, 19),
                        endTime = LocalTime(14, 0)
                    ),
                    description = "World's largest collection of Van Gogh artworks",
                    location = "Amsterdam, Netherlands"
                ),
                Event(
                    title = "Flight to Rome",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 20),
                        startTime = LocalTime(8, 0),
                        endDate = LocalDate(2025, 8, 20),
                        endTime = LocalTime(12, 0)
                    ),
                    description = "Morning flight to the Eternal City",
                    location = "Amsterdam to Rome"
                ),
                Event(
                    title = "Colosseum Tour",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 21),
                        startTime = LocalTime(9, 0),
                        endDate = LocalDate(2025, 8, 21),
                        endTime = LocalTime(12, 0)
                    ),
                    description = "Explore ancient Roman architecture and history",
                    location = "Rome, Italy"
                ),
                Event(
                    title = "Vatican City Visit",
                    duration = Duration(
                        startDate = LocalDate(2025, 8, 22),
                        startTime = LocalTime(10, 0),
                        endDate = LocalDate(2025, 8, 22),
                        endTime = LocalTime(16, 0)
                    ),
                    description = "Sistine Chapel and St. Peter's Basilica",
                    location = "Vatican City"
                )
            ),
            imageHeaderUrl = "https://images.pexels.com/photos/532826/pexels-photo-532826.jpeg",
            createdDate = LocalDate(2025, 6, 15) // Middle trip
        ),
        Trip(
            id = "trip_mountain_retreat",
            title = "Mountain Retreat",
            description = "Peaceful getaway in the mountains",
            location = "Banff National Park",
            duration = Duration(
                startDate = LocalDate(2025, 9, 5),
                startTime = LocalTime(8, 0),
                endDate = LocalDate(2025, 9, 12),
                endTime = LocalTime(16, 0)
            ),
            users = listOf(User(name = "Charlie"), User(name = "Diana")),
            events = listOf(
                Event(
                    title = "Arrival & Check-in",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 5),
                        startTime = LocalTime(15, 0),
                        endDate = LocalDate(2025, 9, 5),
                        endTime = LocalTime(17, 0)
                    ),
                    description = "Check into mountain lodge and settle in",
                    location = "Banff Lodge"
                ),
                Event(
                    title = "Lake Louise Hike",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 6),
                        startTime = LocalTime(8, 0),
                        endDate = LocalDate(2025, 9, 6),
                        endTime = LocalTime(14, 0)
                    ),
                    description = "Morning hike around the stunning turquoise lake",
                    location = "Lake Louise, Banff"
                ),
                Event(
                    title = "Gondola Ride",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 7),
                        startTime = LocalTime(10, 0),
                        endDate = LocalDate(2025, 9, 7),
                        endTime = LocalTime(13, 0)
                    ),
                    description = "Banff Gondola for breathtaking mountain views",
                    location = "Sulphur Mountain, Banff"
                ),
                Event(
                    title = "Hot Springs Relaxation",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 7),
                        startTime = LocalTime(16, 0),
                        endDate = LocalDate(2025, 9, 7),
                        endTime = LocalTime(19, 0)
                    ),
                    description = "Soak in natural hot springs after hiking",
                    location = "Banff Upper Hot Springs"
                ),
                Event(
                    title = "Johnston Canyon Walk",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 8),
                        startTime = LocalTime(9, 0),
                        endDate = LocalDate(2025, 9, 8),
                        endTime = LocalTime(13, 0)
                    ),
                    description = "Easy walk to see beautiful waterfalls and canyon",
                    location = "Johnston Canyon, Banff"
                ),
                Event(
                    title = "Moraine Lake Sunrise",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 9),
                        startTime = LocalTime(6, 0),
                        endDate = LocalDate(2025, 9, 9),
                        endTime = LocalTime(10, 0)
                    ),
                    description = "Early morning visit to catch the perfect sunrise",
                    location = "Moraine Lake, Banff"
                ),
                Event(
                    title = "Wildlife Safari",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 10),
                        startTime = LocalTime(7, 0),
                        endDate = LocalDate(2025, 9, 10),
                        endTime = LocalTime(12, 0)
                    ),
                    description = "Guided tour to spot elk, bears, and mountain goats",
                    location = "Bow Valley, Banff"
                ),
                Event(
                    title = "Farewell Dinner",
                    duration = Duration(
                        startDate = LocalDate(2025, 9, 11),
                        startTime = LocalTime(18, 0),
                        endDate = LocalDate(2025, 9, 11),
                        endTime = LocalTime(21, 0)
                    ),
                    description = "Final dinner with mountain views and local cuisine",
                    location = "Banff Town"
                )
            ),
            imageHeaderUrl = null,
            createdDate = LocalDate(2025, 7, 1) // Newest trip
        )    
    )

    override suspend fun getAllTrips(): List<Trip> {
        return trips.sortedByDescending { it.createdDate }
    }
    
    override suspend fun getTripById(id: String): Trip? {
        return trips.find { it.id == id }
    }
    
    override suspend fun insertTrip(trip: Trip): Trip {
        trips.add(trip)
        return trip
    }
    
    override suspend fun updateTrip(trip: Trip): Trip {
        val index = trips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            trips[index] = trip
        }
        return trip
    }
    
    override suspend fun deleteTrip(tripId: String) {
        trips.removeAll { it.id == tripId }
    }

    override suspend fun addEventToTrip(tripId: String, event: Event) {
        mutateTrip(tripId) { trip -> trip.copy(events = trip.events + event) }
    }

    override suspend fun deleteEventFromTrip(tripId: String, eventId: String) {
        mutateTrip(tripId) { trip ->
            trip.copy(events = trip.events.filterNot { it.title == eventId })
        }
    }

    override suspend fun updateEventInTrip(tripId: String, eventId: String, updated: Event) {
        mutateTrip(tripId) { trip ->
            val newEvents = trip.events.map { if (it.title == eventId) updated else it }
            trip.copy(events = newEvents)
        }
    }

    override suspend fun addMemberToTrip(tripId: String, userId: String) {
        mutateTrip(tripId) { trip ->
            if (trip.users.any { it.name == userId }) {
                trip
            } else {
                trip.copy(users = trip.users + User(name = userId))
            }
        }
    }

    override suspend fun removeMemberFromTrip(tripId: String, userId: String) {
        mutateTrip(tripId) { trip ->
            trip.copy(users = trip.users.filterNot { it.name == userId })
        }
    }

    private fun mutateTrip(tripId: String, transform: (Trip) -> Trip) {
        val index = trips.indexOfFirst { it.id == tripId }
        if (index == -1) {
            throw IllegalArgumentException("Trip not found: $tripId")
        }
        trips[index] = transform(trips[index])
    }
}

class LocalUserDataSource : UserDataSource {
    
    // Mock current user (the logged-in user)
    private val currentUser = User(
        name = "Aga Khan",
        pfpUrl = null
    )
    
    // Mock other users (for adding to trips)
    private val allUsers = listOf(
        currentUser, // Current user
        User(name = "Klodiana", pfpUrl = null),
        User(name = "Alex", pfpUrl = null),
        User(name = "Sam", pfpUrl = null),
        User(name = "Priya", pfpUrl = null),
        User(name = "Diego", pfpUrl = null),
        User(name = "Mei", pfpUrl = null),
        User(name = "Fatima", pfpUrl = null),
        User(name = "John", pfpUrl = null),
        User(name = "Maria", pfpUrl = null),
        User(name = "Chen", pfpUrl = null),
        User(name = "Liam", pfpUrl = null),
        User(name = "Zoe", pfpUrl = null)
    )
    
    override suspend fun getCurrentUser(): User {
        return currentUser
    }
    
    override suspend fun getAllUsers(): List<User> {
        return allUsers
    }
    
    override suspend fun getUserById(id: String): User? {
        return allUsers.find { it.name == id } // Using name as ID for now
    }
}
