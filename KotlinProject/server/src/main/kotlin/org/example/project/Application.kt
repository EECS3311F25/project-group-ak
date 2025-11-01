package org.example.project

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/trip") {
            val json = """
                {
                  "title": "Summer Getaway",
                  "duration": {
                    "startDate": "2025-07-01",
                    "startTime": "09:00",
                    "endDate": "2025-07-10",
                    "endTime": "17:00"
                  },
                  "description": "Road trip across Ontario",
                  "location": "Toronto to Ottawa",
                  "users": [
                    { "name": "Klodiana", "pfpUrl": null },
                    { "name": "Alex", "pfpUrl": null },
                    { "name": "Sam", "pfpUrl": null }
                  ],
                  "events": [
                    {
                      "title": "Niagara Falls Stop",
                      "duration": {
                        "startDate": "2025-07-01",
                        "startTime": "09:00",
                        "endDate": "2025-07-01",
                        "endTime": "17:00"
                      },
                      "description": "",
                      "location": ""
                    },
                    {
                      "title": "Ottawa Parliament Tour",
                      "duration": {
                        "startDate": "2025-07-03",
                        "startTime": "10:00",
                        "endDate": "2025-07-03",
                        "endTime": "12:00"
                      },
                      "description": "",
                      "location": ""
                    }
                  ],
                  "imageHeaderUrl": null
                }
            """.trimIndent()

            call.respondText(json, ContentType.Application.Json)
        }
    }
}