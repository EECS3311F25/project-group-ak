package org.example.project.db_query.user

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.put
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


class UpdateUserTests {

    /**
     *  User exists, password is valid (-> valid)
     *  @result User account password updated + returns OK status code
     */
    @Test
    fun userUpdate_TestSuccess_1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.put("/user/1") {
            contentType(ContentType.Application.Json)
            setBody(User("user1", "user1@gmail.com", "newPassword1"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Password updated successfully", response.bodyAsText())
    }

    /**
     *  User exists, password is valid (-> valid)
     *  @result User account password updated + returns OK status code
     */
    @Test
    fun userUpdate_TestSuccess_2() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.put("/user/2") {
            contentType(ContentType.Application.Json)
            setBody(User("user2", "user2@gmail.com", "newPassword2"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Password updated successfully", response.bodyAsText())
    }

    /**
     *  User exists, new password too weak (-> invalid)
     *  @result User account password updated + returns OK status code
     */
    @Test
    fun userUpdate_TestPasswordTooWeak() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.put("/user/1") {
            contentType(ContentType.Application.Json)
            setBody(User("user1", "user1@gmail.com", "weakpsw"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Password update failed", response.bodyAsText())
    }
}