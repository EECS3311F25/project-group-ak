package org.example.project.data.api

/*
client - create a client like
--> client is used to make the API calls (Get, Post etc)

Ktor need an engine to actually send HTTP requests
CIO (Coroutine-based I/O)

ContentNegotiation plugin for Ktor Client
    -> Plugin automatically converts Kotlin object to json and vice-versa

kotlinx.serialization - it's tell Ktor that developer wants to use Kotlin's default library for Serialization
*/


import io.ktor.client.*
import org.example.project.data.remote.HttpClientProvider

object HttpClientFactory {
    private val baseUrl = "http://localhost:8080" // TODO: Move to config

    fun create(): HttpClient {
        // Use HttpClientProvider which handles platform-specific engines (Js for web, OkHttp for JVM, etc.)
        return HttpClientProvider.client
    }









}



