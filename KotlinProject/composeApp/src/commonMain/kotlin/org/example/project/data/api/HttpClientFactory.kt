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
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


object  HttpClientFactory{
    private val baseUrl = "http://localhost:8080" // TODO: Move to config

    fun create() : HttpClient {
        return HttpClient(CIO){
            install(ContentNegotiation){
                json(Json{
                    ignoreUnknownKeys= true
                    isLenient= true
                    prettyPrint=false
                })
            }
        }
    }









}



