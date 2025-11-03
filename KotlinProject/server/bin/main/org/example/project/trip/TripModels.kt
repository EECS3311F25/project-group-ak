package org.example.project.trip

data class TripDuration(
    val startDate: String,
    val startTime: String?,
    val endDate: String,
    val endTime: String?
)

data class TripUser(
    val name: String,
    val pfpUrl: String?
)

data class TripEvent(
    val title: String,
    val duration: TripDuration,
    val description: String?,
    val location: String?
)

data class Trip(
    val id: String,
    val title: String,
    val duration: TripDuration,
    val description: String?,
    val location: String?,
    val users: List<TripUser>,
    val events: List<TripEvent>,
    val imageHeaderUrl: String?
)