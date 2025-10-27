package org.example.project.model

data class Date(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0
) {
    init {
        require(month in 1..12) { "month must be in 1..12" }
        require(day in 1..31) { "day must be in 1..31" }
        require(hour in 0..23) { "hour must be in 0..23" }
        require(minute in 0..59) { "minute must be in 0..59" }
        require(second in 0..59) { "second must be in 0..59" }
    }

    // explicit getters (properties already provide getters, these are convenience methods)
    fun getYear(): Int = year
    fun getMonth(): Int = month
    fun getDay(): Int = day
    fun getHour(): Int = hour
    fun getMinute(): Int = minute
    fun getSecond(): Int = second

    fun toIsoString(): String =
        "%04d-%02d-%02dT%02d:%02d:%02d".format(year, month, day, hour, minute, second)
}