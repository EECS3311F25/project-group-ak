// package org.example.project.model

// /**
//  * Trip model containing essential information and an array of events.
//  * The events property is an Array<Event> as requested; helper functions return a new Trip
//  * when events are added/removed (arrays are fixed-size in Kotlin).
//  */
// data class Trip(
//     val id: String,
//     val name: String,
//     // TODO: will contain multiple users with multiple forms of access
//     val description: String? = null,
//     val startIso: String? = null,
//     val endIso: String? = null,
//     val events: Array<Event> = emptyArray()
// ) {
//     val eventCount: Int
//         get() = events.size

//     val eventsList: List<Event>
//         get() = events.toList()

//     /**
//      * Returns a new Trip with the given event appended.
//      */
//     fun addingEvent(event: Event): Trip =
//         copy(events = events + event)

//     /**
//      * Returns a new Trip with the given events appended.
//      */
//     fun addingEvents(vararg newEvents: Event): Trip =
//         copy(events = events + newEvents)

//     /**
//      * Returns a new Trip with the event with matching id removed.
//      */
//     fun removingEventById(eventId: String): Trip =
//         copy(events = events.filter { it.id != eventId }.toTypedArray())

//     /**
//      * Returns a new Trip with the event replaced if ids match; otherwise returns the same Trip.
//      */
//     fun updatingEvent(updatedEvent: Event): Trip {
//         val index = events.indexOfFirst { it.id == updatedEvent.id }
//         return if (index >= 0) {
//             val copyArr = events.copyOf()
//             copyArr[index] = updatedEvent
//             copy(events = copyArr)
//         } else this
//     }

//     /**
//      * Find an event by id or null if not found.
//      */
//     fun findEventById(eventId: String): Event? =
//         events.firstOrNull { it.id == eventId }
// }