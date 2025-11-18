package org.example.project.model.dataClasses

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.Serializable
import kotlinx.datetime.plus

@Serializable
data class Duration(
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
    val endTime: LocalTime
) {
    // Helper to create LocalDateTime from date and time
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

    // Get start as LocalDateTime
    fun getStartDateTime(): LocalDateTime = toLocalDateTime(startDate, startTime)

    // Get end as LocalDateTime
    fun getEndDateTime(): LocalDateTime = toLocalDateTime(endDate, endTime)

    // Check if a specific date is within this duration
    fun isWithin(date: LocalDate): Boolean {
        return date >= startDate && date <= endDate
    }

    // Check if a specific datetime is within this duration
    fun isWithin(dateTime: LocalDateTime): Boolean {
        val start = getStartDateTime()
        val end = getEndDateTime()
        return dateTime >= start && dateTime <= end
    }

    /**
     * Returns true when this duration is fully contained within [other], boundaries included.
     */
    fun isWithin(other: Duration): Boolean {
        val start = getStartDateTime()
        val end = getEndDateTime()
        return start >= other.getStartDateTime() && end <= other.getEndDateTime()
    }

    /**
     * Returns true when any portion of this duration overlaps with [other].
     * Boundaries touching (end == other.start) are considered non-conflicting.
     */
    fun conflictsWith(other: Duration): Boolean {
        val start = getStartDateTime()
        val end = getEndDateTime()
        val otherStart = other.getStartDateTime()
        val otherEnd = other.getEndDateTime()
        return start < otherEnd && end > otherStart
    }

    // Check if this duration is completely within another duration
    fun isContainedIn(other: Duration): Boolean {
        val thisStart = getStartDateTime()
        val thisEnd = getEndDateTime()
        val otherStart = other.getStartDateTime()
        val otherEnd = other.getEndDateTime()
        return thisStart >= otherStart && thisEnd <= otherEnd
    }

    // Check if this duration completely contains another duration
    fun contains(other: Duration): Boolean {
        return other.isContainedIn(this)
    }

    // Get all dates that this duration spans
    fun getAllDates(): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            dates.add(currentDate)
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }
        return dates
    }

    /**
     * Return true if this duration overlaps any portion of the given date.
     * This is more precise than checking only start/end dates because it
     * compares datetimes and will include events that start before the date
     * and end on it, start on the date and end after it, or span the whole day.
     */
    fun overlapsDate(date: LocalDate): Boolean {
        val thisStart = getStartDateTime()
        val thisEnd = getEndDateTime()
        val dayStart = toLocalDateTime(date, LocalTime(0, 0))
        // end of day: set to the last possible nanosecond of the day
        val dayEnd = toLocalDateTime(date, LocalTime(23, 59, 59, 999_999_999))
        // Overlap exists when the event starts before or at dayEnd and ends after or at dayStart
        return thisStart <= dayEnd && thisEnd >= dayStart
    }
}
