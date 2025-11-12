package org.example.project.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database


//  TODO: store DB config (to prevent app leaks)
//  reference: https://www.jetbrains.com/help/exposed/working-with-database.html#connecting-to-a-database
fun Application.configureDatabases() {
    val postgresqldb = Database.connect(
        "jdbc:postgresql://localhost:5432/navi_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = DBConnectionConstants.POSTGRES_USER_PSW
    )
}

//  TODO: write connection .close() function
//  example: https://github.com/ktorio/ktor-documentation/blob/3.3.2/codeSnippets/snippets/tutorial-server-db-integration/src/main/kotlin/com/example/Databases.kt