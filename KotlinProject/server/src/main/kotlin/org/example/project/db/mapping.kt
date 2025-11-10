package org.example.project.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.Transaction
//  refer to: https://www.jetbrains.com/help/exposed/migration-guide-1-0-0.html
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

import org.example.project.User


/*  References:
-   https://ktor.io/docs/server-integrate-database.html#create-mapping
-   https://www.jetbrains.com/help/exposed/working-with-tables.html#nullable
*/

//  TODO: improve logic of field nullability


//  Table / IntIdTable(table_name)
object UserTable: IntIdTable("user") {
    val userName = varchar("userName", 50)
    val userEmail = varchar("userEmail", 50)
    val userPassword = varchar("userEmail", 50)
}

//  maps UserTable to DB user table
class UserDAO(userid: EntityID<Int>): IntEntity(userid) {
    companion object : IntEntityClass<UserDAO>(UserTable)

    var userName by UserTable.userName
    var userEmail by UserTable.userEmail
    var userPassword by UserTable.userPassword
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

fun daoToModel(dao: UserDAO) = User(
    dao.userName,
    dao.userEmail,
    dao.userPassword
)