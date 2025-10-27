/**
 * A simple event model for a trip.
 * Dates/times are represented as ISO-8601 strings (e.g. "2025-10-27T15:30:00Z") to keep this file multiplatform-friendly.
 */
data class Event(
    val id: String,
    val title: String,
    val description: String? = null,
    val startIso: String? = null,
    val endIso: String? = null,
    val location: String? = null
)