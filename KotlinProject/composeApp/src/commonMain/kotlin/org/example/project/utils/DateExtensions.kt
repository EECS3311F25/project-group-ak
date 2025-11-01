package org.example.project.utils

import kotlinx.datetime.*

fun LocalDate.rangeUntil(end: LocalDate): List<LocalDate> {
    require(this <= end)
    val days = this.daysUntil(end)
    return (0..days).map { this.plus(it, DateTimeUnit.DAY) }
}

