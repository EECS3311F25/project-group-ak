package org.example.project.trip

object TripRepositoryMock {


    private val tripsForKai = listOf(
        Trip(
            id = "1",
            title = "Summer Getaway",
            duration = TripDuration(
                startDate = "2025-07-01",
                startTime = "09:00",
                endDate = "2025-07-10",
                endTime = "17:00"
            ),
            description = "Road trip across Ontario",
            location = "Toronto to Ottawa",
            users = listOf(
                TripUser("Klodiana", null),
                TripUser("Alex", null),
                TripUser("Sam", null)
            ),
            events = listOf(
                TripEvent(
                    title = "Niagara Falls Stop",
                    duration = TripDuration(
                        startDate = "2025-07-01",
                        startTime = "09:00",
                        endDate = "2025-07-01",
                        endTime = "17:00"
                    ),
                    description = "",
                    location = ""
                ),
                TripEvent(
                    title = "Ottawa Parliament Tour",
                    duration = TripDuration(
                        startDate = "2025-07-03",
                        startTime = "10:00",
                        endDate = "2025-07-03",
                        endTime = "12:00"
                    ),
                    description = "",
                    location = ""
                )
            ),
            imageHeaderUrl = null
        )
    )


    private val invitedForKai = listOf(
        Trip(
            id = "2",
            title = "Weekend in Montreal",
            duration = TripDuration(
                startDate = "2025-08-02",
                startTime = "08:00",
                endDate = "2025-08-04",
                endTime = "18:00"
            ),
            description = "Short trip with friends",
            location = "Montreal",
            users = listOf(
                TripUser("Andrew", null),
                TripUser("Kai", null)
            ),
            events = emptyList(),
            imageHeaderUrl = null
        )
    )

    fun getAllForUser(userId: String): List<Trip> {

        return tripsForKai
    }

    fun getInvited(userId: String): List<Trip> {
        return invitedForKai
    }

    fun getById(id: String): Trip? {
        return (tripsForKai + invitedForKai).firstOrNull { it.id == id }
    }
}