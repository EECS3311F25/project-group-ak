package org.example.project.data.remote

import io.ktor.client.*

expect fun createHttpClient(): HttpClient

object HttpClientProvider {
    val client = createHttpClient()
}
