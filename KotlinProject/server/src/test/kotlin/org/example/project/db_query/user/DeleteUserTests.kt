package org.example.project.db_query.user

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

import org.example.project.module
import org.example.project.user.User


class DeleteUserTests {

    /**
     *  User exists (-> valid)
     *  @result Account will be deleted without any errors and exceptions + returns No Content status code
     */
    @Test
    fun userDelete_TestSuccess_1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.delete("/user/user1/delete") {
            contentType(ContentType.Application.Json)
            setBody(User("user1", "fotBranch@gmail.com", "password1"))
        }
        assertEquals(HttpStatusCode.NoContent, response.status)
        assertEquals("User deleted successfully", response.bodyAsText())
    }

    /**
     *  User exists (-> valid)
     *  @result Account will be deleted without any errors and exceptions + returns No Content status code
     */
    @Test
    fun userDelete_TestSuccess_2() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.delete("/user/user2/delete") {
            contentType(ContentType.Application.Json)
            setBody(User("user2", "user2@gmail.com", "password2"))
        }
        assertEquals(HttpStatusCode.NoContent, response.status)
        assertEquals("User deleted successfully", response.bodyAsText())
    }

    /**
     *  User not found (-> invalid)
     *  @result Account will not be deleted + returns Not Found status code
     */
    @Test
    fun userDelete_TestUserNotFound() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.delete("/user/user2/delete") {
            contentType(ContentType.Application.Json)
            setBody(User("user2", "user2@gmail.com", "password2"))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("User does not exist", response.bodyAsText())
    }
}