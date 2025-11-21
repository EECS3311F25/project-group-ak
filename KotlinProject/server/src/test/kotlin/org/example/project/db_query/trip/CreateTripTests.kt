package org.example.project.db_query.trip

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import org.example.project.module
import org.example.project.user.User
import kotlin.test.Test
import kotlin.test.assertEquals


class CreateTripTests {
    /**
     *  Valid trip creation
     *  @result Trip associated with account will be created without any errors and exceptions + returns Created status code
     */
    @Test
    fun userCreate_TestSuccess_1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("user1", "user1@gmail.com", "password1"))
        }
        println(response.bodyAsText())

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("User successfully registered", response.bodyAsText())
    }
}