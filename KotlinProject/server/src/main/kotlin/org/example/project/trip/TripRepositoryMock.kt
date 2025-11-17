// package org.example.project.trip

// import java.util.concurrent.ConcurrentHashMap
// import java.util.concurrent.atomic.AtomicInteger

// object TripRepositoryMock {

//     // In-memory store
//     private val idGenTrip = AtomicInteger(100)
//     private val idGenEvent = AtomicInteger(1000)

//     // Map<tripId, Trip>
//     private val trips = ConcurrentHashMap<String, Trip>().apply {
//         put(
//             "1",
//             Trip(
//                 id = "1",
//                 name = "Toronto Fall Trip",
//                 owner = "kai",
//                 users = listOf("kai", "andrew", "toni"),
//                 events = listOf(
//                     Event(
//                         id = "e1",
//                         title = "CN Tower",
//                         description = "View deck",
//                         location = Location(43.6426, -79.3871),
//                         duration = Duration("2025-11-03T10:00:00", "2025-11-03T12:00:00")
//                     )
//                 ),
//                 duration = Duration("2025-11-03T09:00:00", "2025-11-05T18:00:00")
//             )
//         )
//         put(
//             "2",
//             Trip(
//                 id = "2",
//                 name = "Niagara Day",
//                 owner = "kai",
//                 users = listOf("kai"),
//                 events = emptyList(),
//                 duration = Duration("2025-12-10T08:00:00", "2025-12-10T20:00:00")
//             )
//         )
//     }

//     /* ----------------- Trip queries ----------------- */
//     fun getAllForUser(user: String): List<Trip> =
//         trips.values.filter { it.owner == user || it.users.contains(user) }.sortedBy { it.id }

//     fun getById(id: String): Trip? = trips[id]

//     fun createTrip(req: TripCreateRequest): Trip {
//         val newId = idGenTrip.incrementAndGet().toString()
//         val trip = Trip(
//             id = newId,
//             name = req.name,
//             owner = req.owner,
//             users = req.users,
//             events = emptyList(),
//             duration = req.duration
//         )
//         trips[newId] = trip
//         return trip
//     }

//     fun updateTrip(id: String, req: TripUpdateRequest): Trip? {
//         val cur = trips[id] ?: return null
//         val updated = cur.copy(
//             name = req.name ?: cur.name,
//             users = req.users ?: cur.users,
//             duration = req.duration ?: cur.duration
//         )
//         trips[id] = updated
//         return updated
//     }

//     fun deleteTrip(id: String): Boolean = trips.remove(id) != null

//     /* ----------------- Event queries (under a Trip) ----------------- */
//     fun listEvents(tripId: String): List<Event>? =
//         trips[tripId]?.events

//     fun createEvent(tripId: String, req: EventCreateRequest): Event? {
//         val cur = trips[tripId] ?: return null
//         val newEvent = Event(
//             id = "e" + idGenEvent.incrementAndGet().toString(),
//             title = req.title,
//             description = req.description,
//             location = req.location,
//             duration = req.duration
//         )
//         val updated = cur.copy(events = cur.events + newEvent)
//         trips[tripId] = updated
//         return newEvent
//     }

//     fun updateEvent(tripId: String, eventId: String, req: EventUpdateRequest): Event? {
//         val cur = trips[tripId] ?: return null
//         val idx = cur.events.indexOfFirst { it.id == eventId }
//         if (idx == -1) return null
//         val ev = cur.events[idx]
//         val newEv = ev.copy(
//             title = req.title ?: ev.title,
//             description = req.description ?: ev.description,
//             location = req.location ?: ev.location,
//             duration = req.duration ?: ev.duration
//         )
//         val newList = cur.events.toMutableList().also { it[idx] = newEv }
//         trips[tripId] = cur.copy(events = newList)
//         return newEv
//     }

//     fun deleteEvent(tripId: String, eventId: String): Boolean {
//         val cur = trips[tripId] ?: return false
//         val newList = cur.events.filterNot { it.id == eventId }
//         if (newList.size == cur.events.size) return false
//         trips[tripId] = cur.copy(events = newList)
//         return true
//     }
// }