package org.example.project

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import org.example.project.db.configureDatabases
import org.example.project.db.configureRouting
import org.example.project.db.configureSerialization

const val SERVER_PORT: Int = 8080

fun main() {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Install CORS
    install(CORS) {
        anyHost() // Allow requests from any host (for development)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }
    
//     // Install ContentNegotiation for JSON serialization
//     install(ContentNegotiation) {
//         json(Json {
//             prettyPrint = true
//             isLenient = true
//             ignoreUnknownKeys = true
//         })
//     }
    
//     routing {
//         get("/") { call.respondText("Ktor: ${Greeting().greet()}") }

//         tripRoutes()
//         mockApiRoutes()
//     }
    //  Serializable to JSON - see Serialization.kt
    configureSerialization()

    //  DB configuration and migration - see DatabaseConnect.kt and Migration.kt
    configureDatabases()

    //  Register all HTTP routes to the database - see Routing.kt
    configureRouting()

}