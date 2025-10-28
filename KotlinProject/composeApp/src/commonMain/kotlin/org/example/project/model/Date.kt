// package org.example.project.model

// data class Date(
//     val year: Int,
//     val month: Int,
//     val day: Int,
//     val hour: Int = 0,
//     val minute: Int = 0,
//     val second: Int = 0
// ) {
//     init {
//         require(month in 1..12) { "month must be in 1..12" }
//         require(day in 1..31) { "day must be in 1..31" }
//         require(hour in 0..23) { "hour must be in 0..23" }
//         require(minute in 0..59) { "minute must be in 0..59" }
//         require(second in 0..59) { "second must be in 0..59" }
//     }

//     // explicit getters (properties already provide getters, these are convenience methods)
//     fun getYear(): Int = year
//     fun getMonth(): Int = month
//     fun getDay(): Int = day
//     fun getHour(): Int = hour
//     fun getMinute(): Int = minute
//     fun getSecond(): Int = second

//     fun toIsoString(): String {
//         val y = year.toString().padStart(4, '0')
//         val m = month.toString().padStart(2, '0')
//         val d = day.toString().padStart(2, '0')
//         val h = hour.toString().padStart(2, '0')
//         val min = minute.toString().padStart(2, '0')
//         val s = second.toString().padStart(2, '0')
//         return "$y-$m-$dT$h:$min:$s"
//     }
// }
