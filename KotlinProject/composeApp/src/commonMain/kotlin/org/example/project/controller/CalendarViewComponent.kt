package org.example.project.controller

import org.example.project.model.Event
import kotlinx.datetime.LocalDate

interface CalendarViewComponent {
    fun onDateSelected(date: LocalDate)
    fun onEventSelected(event: Event)
    fun onBack()
}

sealed class CalendarViewEvent {
    data class SelectDate(val date: LocalDate) : CalendarViewEvent()
    data class SelectEvent(val event: Event) : CalendarViewEvent()
    object Back : CalendarViewEvent()
}