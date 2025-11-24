package org.example.project.user

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//  docs: https://ktor.io/docs/server-integrate-database.html#switch-repo
//  example: https://github.com/ktorio/ktor-documentation/blob/3.3.2/codeSnippets/snippets/tutorial-server-db-integration/src/main/kotlin/com/example/Serialization.kt

/**
 * User HTTP routes.
 *
 * Flow:
 *  - Application.module() creates PostgresUserRepository
 *  - Application.configureUserSerialization(userRepository)
 *  - These handlers use repository only (validation is in UserService).

 * Endpoints:
 *  GET    /user         -> get user by user ID
 *  POST   /user/register           -> create new user
 *  PUT    /user/{id}/password         -> update user password
 *  DELETE /user/{id}/delete  -> delete user by username
 */
fun Application.configureUserSerialization(userRepository: PostgresUserRepository) {

    routing {

        route("/user") {

            //  GET "/user/{id}"  ->  get user by id
            get("/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                val user = userRepository.getUserById(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@get
                }
                call.respond(HttpStatusCode.OK,
                    UserRetrieveResponse("User retrieved successfully", user))
            }

            //  POST "/user/register"   ->  create new user
            post("/register") {
                try {
                    val userDto = call.receive<UserCreateRequest>()
                    val addResult = userRepository.addUser(userDto)
                    val user = addResult.getOrNull()

                    if (user != null) {
                        call.respond(
                            HttpStatusCode.Created,
                            UserRetrieveResponse("User registered successfully", user)
                        )
                    }
                    else {
                        call.respond(HttpStatusCode.BadRequest, "User registration failed")
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError, "Internal server error, registration failed"
                    )
                }
            }

            //  PUT "/user/{id}" - update user password
            put("/{id}") {
                try {
                    val user = call.receive<User>()
                    val userId = call.parameters["id"]?.toIntOrNull()

                    userRepository.updateUserPassword(userId, user.userPassword)
                        .onSuccess {
                            call.respond(
                                HttpStatusCode.OK,
                                "Password updated successfully"
                            )
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                "Password update failed"
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Password update failed: ${e.message}"
                    )
                }
            }

            //  DELETE "/user/{id}/delete" - delete user by username
            delete("/{id}/delete") {
                val userId = call.parameters["id"]?.toIntOrNull()
                userRepository.deleteUserById(userId)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.NoContent,
                            "User deleted successfully"
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.NotFound,
                            "User does not exist"
                        )
                    }
            }
        }
    }
}