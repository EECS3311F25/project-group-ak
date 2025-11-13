package org.example.project.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Duration(
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
    val endTime: LocalTime
) {
    // Get start as LocalDateTime
    fun getStartDateTime(): LocalDateTime {
        return LocalDateTime(startDate, startTime)
    }
    
    // Get end as LocalDateTime
    fun getEndDateTime(): LocalDateTime {
        return LocalDateTime(endDate, endTime)
    }
    
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
    
    // Check if this duration conflicts/overlaps with another duration
    fun conflictsWith(other: Duration): Boolean {
        val thisStart = getStartDateTime()
        val thisEnd = getEndDateTime()
        val otherStart = other.getStartDateTime()
        val otherEnd = other.getEndDateTime()
        
        // Two durations conflict if:
        // 1. This starts before other ends AND this ends after other starts
        // OR in simpler terms: they overlap if one starts before the other ends
        return thisStart < otherEnd && thisEnd > otherStart
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
            // Move to next day
            currentDate = LocalDate(
                currentDate.year,
                currentDate.monthNumber,
                currentDate.dayOfMonth + 1
            )
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
        val dayStart = LocalDateTime(date, LocalTime(0, 0))
        // end of day: set to the last possible nanosecond of the day
        val dayEnd = LocalDateTime(date, LocalTime(23, 59, 59, 999_999_999))

        // Overlap exists when the event starts before or at dayEnd and ends after or at dayStart
        return thisStart <= dayEnd && thisEnd >= dayStart
    }
}