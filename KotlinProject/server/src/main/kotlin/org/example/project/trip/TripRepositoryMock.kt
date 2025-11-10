package org.example.project.trip

object TripRepositoryMock {

    private val trips = mutableListOf(
        Trip(
            id = "1",
            name = "Toronto Fall Trip",
            owner = "kai",
            users = mutableListOf("kai", "andrew", "toni"),
            events = mutableListOf(
                Event(
                    id = "e1",
                    title = "CN Tower",
                    description = "View deck",
                    location = Location(43.6426, -79.3871),
                    duration = Duration("2025-11-03T10:00:00", "2025-11-03T12:00:00")
                ),
                Event(
                    id = "e2",
                    title = "Lunch at Harbourfront",
                    description = "Seafood",
                    location = Location(43.6385, -79.3810),
                    duration = Duration("2025-11-03T12:30:00", "2025-11-03T13:30:00")
                )
            ),
            duration = Duration("2025-11-03T09:00:00", "2025-11-05T18:00:00")
        ),
        Trip(
            id = "2",
            name = "Niagara Day",
            owner = "kai",
            users = mutableListOf("kai"),
            events = mutableListOf(),
            duration = Duration("2025-11-10T08:00:00", "2025-11-10T20:00:00")
        )
    )

    fun getAllForUser(username: String): List<Trip> =
        trips.filter { it.users.contains(username) || it.owner == username }

    fun getInvited(username: String): List<Trip> =
        trips.filter { it.users.contains(username) && it.owner != username }

    fun getById(tripId: String): Trip? = trips.find { it.id == tripId }


    fun getEvent(tripId: String, eventId: String): Event? {
        return trips.find { it.id == tripId }?.events?.find { it.id == eventId }
    }

    fun addEvent(tripId: String, event: Event): Event? {
        val trip = trips.find { it.id == tripId } ?: return null

        val idx = trip.events.indexOfFirst { it.id == event.id }
        
        if (idx >= 0) {
            trip.events[idx] = event
        } else {
            trip.events.add(event)
        }
        return event
    }

    fun updateEvent(tripId: String, eventId: String, updated: Event): Event? {
        val trip = trips.find { it.id == tripId } ?: return null
        val idx = trip.events.indexOfFirst { it.id == eventId }
        if (idx == -1) return null

        trip.events[idx] = updated.copy(id = eventId)
        return trip.events[idx]
    }

    fun deleteEvent(tripId: String, eventId: String): Boolean {
        val trip = trips.find { it.id == tripId } ?: return false
        return trip.events.removeIf { it.id == eventId }
    }
}