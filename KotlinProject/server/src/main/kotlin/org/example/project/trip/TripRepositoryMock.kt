package org.example.project.trip

object TripRepositoryMock {

    private val trips = listOf(
        Trip(
            id = "1",
            name = "Toronto Fall Trip",
            owner = "kai",
            users = listOf("kai", "andrew", "toni"),
            events = listOf(
                Event(
                    id = "e1",
                    title = "CN Tower",
                    description = "View deck",
                    location = Location(43.6426, -79.3871),
                    duration = Duration("2025-11-03T10:00:00", "2025-11-03T12:00:00")
                )
            ),
            duration = Duration("2025-11-03T09:00:00", "2025-11-05T18:00:00")
        ),
        Trip(
            id = "2",
            name = "Niagara Day",
            owner = "andrew",
            users = listOf("kai", "andrew"),
            events = emptyList(),
            duration = Duration("2025-11-10T08:00:00", "2025-11-10T22:00:00")
        )
    )

    fun getAllForUser(user: String): List<Trip> =
        trips.filter { it.owner == user || user in it.users }

    fun getInvited(user: String): List<Trip> =
        trips.filter { it.owner != user && user in it.users }

    fun getById(id: String): Trip? =
        trips.find { it.id == id }
}