package org.example.project.model.dataClasses

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class Duration(
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
    val endTime: LocalTime
) {
    private fun startDateTime(): LocalDateTime = toLocalDateTime(startDate, startTime)
    private fun endDateTime(): LocalDateTime = toLocalDateTime(endDate, endTime)

    /**
     * Returns true when this duration is fully contained within [other], boundaries included.
     */
    fun isWithin(other: Duration): Boolean {
        val start = startDateTime()
        val end = endDateTime()
        return start >= other.startDateTime() && end <= other.endDateTime()
    }

    /**
     * Returns true when any portion of this duration overlaps with [other].
     * Boundaries touching (end == other.start) are considered non-conflicting.
     */
    fun conflictsWith(other: Duration): Boolean {
        val start = startDateTime()
        val end = endDateTime()
        val otherStart = other.startDateTime()
        val otherEnd = other.endDateTime()
        return start < otherEnd && otherStart < end
    }

    private fun toLocalDateTime(date: LocalDate, time: LocalTime): LocalDateTime =
        LocalDateTime(
            year = date.year,
            monthNumber = date.monthNumber,
            dayOfMonth = date.dayOfMonth,
            hour = time.hour,
            minute = time.minute,
            second = time.second,
            nanosecond = time.nanosecond
        )
}
