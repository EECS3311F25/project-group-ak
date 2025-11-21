package org.example.project.trip

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object TripRepositoryMock {

    // In-memory store
    private val idGenTrip = AtomicInteger(100)
    private val idGenEvent = AtomicInteger(1000)

    // Map<tripId, Trip>
    private val trips = ConcurrentHashMap<String, Trip>().apply {
        put(
            "1",
            Trip(
                id = "1",
                name = "Toronto Fall Trip",
                owner = "kai",
                users = listOf("kai", "Clod", "andrew", "toni", ),
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
            )
        )
        put(
            "2",
            Trip(
                id = "2",
                name = "Niagara Day",
                owner = "kai",
                users = listOf("kai"),
                events = emptyList(),
                duration = Duration("2025-12-10T08:00:00", "2025-12-10T20:00:00")
            )
        )
        put(
            "trip_mountain_retreat",
            Trip(
                id = "trip_mountain_retreat",
                name = "Mountain Retreat",
                owner = "Charlie",
                users = listOf("Charlie", "Diana"),
                events = listOf(
                    Event(
                        id = "e1001",
                        title = "Arrival & Check-in",
                        description = "Check into mountain lodge and settle in",
                        location = null,
                        duration = Duration("2025-09-05T15:00:00", "2025-09-05T17:00:00")
                    ),
                    Event(
                        id = "e1002",
                        title = "Lake Louise Hike",
                        description = "Morning hike around the stunning turquoise lake",
                        location = null,
                        duration = Duration("2025-09-06T08:00:00", "2025-09-06T14:00:00")
                    ),
                    Event(
                        id = "e1003",
                        title = "Gondola Ride",
                        description = "Banff Gondola for breathtaking mountain views",
                        location = null,
                        duration = Duration("2025-09-07T10:00:00", "2025-09-07T13:00:00")
                    )
                ),
                duration = Duration("2025-09-05T08:00:00", "2025-09-12T16:00:00")
            )
        )
        put(
            "trip_summer_getaway",
            Trip(
                id = "trip_summer_getaway",
                name = "Summer Getaway",
                owner = "Klodiana",
                users = listOf("Klodiana", "Alex"),
                events = listOf(
                    Event(
                        id = "e2001",
                        title = "Departure from Toronto",
                        description = "Start our journey from downtown Toronto",
                        location = null,
                        duration = Duration("2025-07-01T09:00:00", "2025-07-01T10:00:00")
                    ),
                    Event(
                        id = "e2002",
                        title = "Niagara Falls Stop",
                        description = "Visit the famous Niagara Falls and take photos",
                        location = null,
                        duration = Duration("2025-07-01T12:00:00", "2025-07-01T16:00:00")
                    ),
                    Event(
                        id = "e2003",
                        title = "Niagara Boat Tour",
                        description = "Experience the falls up close on the Maid of the Mist",
                        location = null,
                        duration = Duration("2025-07-02T10:00:00", "2025-07-02T11:30:00")
                    ),
                    Event(
                        id = "e2004",
                        title = "Ottawa Parliament Tour",
                        description = "Guided tour of Canada's Parliament buildings",
                        location = null,
                        duration = Duration("2025-07-04T10:00:00", "2025-07-04T12:00:00")
                    ),
                    Event(
                        id = "e2005",
                        title = "ByWard Market Dinner",
                        description = "Farewell dinner at famous ByWard Market",
                        location = null,
                        duration = Duration("2025-07-04T18:00:00", "2025-07-04T20:00:00")
                    )
                ),
                duration = Duration("2025-07-01T09:00:00", "2025-07-10T17:00:00")
            )
        )
        put(
            "trip_european_adventure",
            Trip(
                id = "trip_european_adventure",
                name = "European Adventure",
                owner = "Alice",
                users = listOf("Alice", "Bob"),
                events = listOf(
                    Event(
                        id = "e3001",
                        title = "Eiffel Tower Visit",
                        description = "Climb the iconic Eiffel Tower and enjoy panoramic views",
                        location = null,
                        duration = Duration("2025-08-15T14:00:00", "2025-08-15T17:00:00")
                    ),
                    Event(
                        id = "e3002",
                        title = "Louvre Museum",
                        description = "See the Mona Lisa and other masterpieces",
                        location = null,
                        duration = Duration("2025-08-16T10:00:00", "2025-08-16T15:00:00")
                    ),
                    Event(
                        id = "e3003",
                        title = "Seine River Walk",
                        description = "Leisurely walk along the Seine",
                        location = null,
                        duration = Duration("2025-08-16T16:30:00", "2025-08-16T17:30:00")
                    ),
                    Event(
                        id = "e3004",
                        title = "Canal Tour Amsterdam",
                        description = "Explore Amsterdam's famous canals by boat",
                        location = null,
                        duration = Duration("2025-08-18T11:00:00", "2025-08-18T13:00:00")
                    ),
                    Event(
                        id = "e3005",
                        title = "Van Gogh Museum",
                        description = "World's largest collection of Van Gogh artworks",
                        location = null,
                        duration = Duration("2025-08-19T10:00:00", "2025-08-19T14:00:00")
                    ),
                    Event(
                        id = "e3006",
                        title = "Colosseum Tour",
                        description = "Explore ancient Roman architecture and history",
                        location = null,
                        duration = Duration("2025-08-21T09:00:00", "2025-08-21T12:00:00")
                    ),
                    Event(
                        id = "e3007",
                        title = "Vatican City Visit",
                        description = "Sistine Chapel and St. Peter's Basilica",
                        location = null,
                        duration = Duration("2025-08-22T10:00:00", "2025-08-22T16:00:00")
                    )
                ),
                duration = Duration("2025-08-15T10:00:00", "2025-08-30T18:00:00")
            )
        )
    }

    /* ----------------- Trip queries ----------------- */
    fun getAllForUser(user: String): List<Trip> =
        trips.values.filter { it.owner == user || it.users.contains(user) }.sortedBy { it.id }

    fun getById(id: String): Trip? = trips[id]

    fun createTrip(req: TripCreateRequest): Trip {
        val newId = idGenTrip.incrementAndGet().toString()
        val trip = Trip(
            id = newId,
            name = req.name,
            owner = req.owner,
            users = req.users,
            events = emptyList(),
            duration = req.duration
        )
        trips[newId] = trip
        return trip
    }

    fun updateTrip(id: String, req: TripUpdateRequest): Trip? {
        val cur = trips[id] ?: return null
        val updated = cur.copy(
            name = req.name ?: cur.name,
            users = req.users ?: cur.users,
            duration = req.duration ?: cur.duration
        )
        trips[id] = updated
        return updated
    }

    fun deleteTrip(id: String): Boolean = trips.remove(id) != null

    /* ----------------- Event queries (under a Trip) ----------------- */
    fun listEvents(tripId: String): List<Event>? =
        trips[tripId]?.events

    fun createEvent(tripId: String, req: EventCreateRequest): Event? {
        val cur = trips[tripId] ?: return null
        val newEvent = Event(
            id = "e" + idGenEvent.incrementAndGet().toString(),
            title = req.title,
            description = req.description,
            location = req.location,
            duration = req.duration
        )
        val updated = cur.copy(events = cur.events + newEvent)
        trips[tripId] = updated
        return newEvent
    }

    fun updateEvent(tripId: String, eventId: String, req: EventUpdateRequest): Event? {
        val cur = trips[tripId] ?: return null
        val idx = cur.events.indexOfFirst { it.id == eventId }
        if (idx == -1) return null
        val ev = cur.events[idx]
        val newEv = ev.copy(
            title = req.title ?: ev.title,
            description = req.description ?: ev.description,
            location = req.location ?: ev.location,
            duration = req.duration ?: ev.duration
        )
        val newList = cur.events.toMutableList().also { it[idx] = newEv }
        trips[tripId] = cur.copy(events = newList)
        return newEv
    }

    fun deleteEvent(tripId: String, eventId: String): Boolean {
        val cur = trips[tripId] ?: return false
        val newList = cur.events.filterNot { it.id == eventId }
        if (newList.size == cur.events.size) return false
        trips[tripId] = cur.copy(events = newList)
        return true
    }
}