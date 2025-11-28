package org.example.project.utils

import kotlin.random.Random
import kotlinx.datetime.Clock

fun generateSessionId(): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val randomPart = Random.nextLong().toString(16)
    return "sess-$now-$randomPart"
}