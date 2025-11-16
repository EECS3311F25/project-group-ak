package org.example.project

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlin.test.*

import org.example.project.user.User


class UserCreateTest {

    @Test
    fun testUser_1() = testApplication {
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
//            setBody(User("fottBranch", "fottBranch@gmail.com", "1234567890"))
        }
        println(response.bodyAsText())

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("User account has successfully been created", response.bodyAsText())
    }
}