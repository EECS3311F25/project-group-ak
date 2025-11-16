package org.example.project.user

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


//  TODO: write HTTPS routing in this module
//  docs: https://ktor.io/docs/server-integrate-database.html#switch-repo
//  example: https://github.com/ktorio/ktor-documentation/blob/3.3.2/codeSnippets/snippets/tutorial-server-db-integration/src/main/kotlin/com/example/Serialization.kt


fun Application.configureUserSerialization(repository: PostgresUserRepository) {

    routing {

        //  TODO: decide whether there allUsers() should be routed

        route("/user") {

            //  GET "/user/{userName}" - get user by username
            get("/{userName}") {
                val userName = call.parameters["userName"]
                if (userName == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val user = repository.getUserByName(userName)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond("User retrieved successfully")
            }

            post("/register") {
                try {
                    val user = call.receive<User>()
                    repository.addUser(user)
                        .onSuccess {
                            call.respond(
                                HttpStatusCode.Created,
                                "User successfully registered"
                            )
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                "User registration failed: ${error.message}"
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError
                    )
                }
            }

            //  PUT "/user/password" - update user password
            put("/password") {
                try {
                    val user = call.receive<User>()
                    repository.updateUserPassword(user.userName, user.userPassword)
                        .onSuccess {
                            call.respond(
                                HttpStatusCode.OK,
                                "Password updated successfully"
                            )
                        }
                        .onFailure {
                            call.respond(
                                HttpStatusCode.BadRequest,
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Password update failed: ${e.message}"
                    )
                }
            }

            delete("/{userName}/delete") {
                val userName = call.parameters["userName"]
                if (userName == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                repository.deleteUserByUsername(userName)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.NoContent,
                            "User deleted successfully"
                        )
                    }
                    .onFailure {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "User deletion failed"
                        )
                    }
            }
        }
    }
}