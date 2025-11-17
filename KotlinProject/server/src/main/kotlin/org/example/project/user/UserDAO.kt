package org.example.project.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.Transaction
//  refer to: https://www.jetbrains.com/help/exposed/migration-guide-1-0-0.html
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction


/**
    References:
-   https://ktor.io/docs/server-integrate-database.html#create-mapping
-   https://www.jetbrains.com/help/exposed/working-with-tables.html#nullable

    TL;DR: the DAO API provides abstractions for defining database tables, and performing CRUD ops on them
 */


/**
    Table / IntIdTable(db_table_name)
    -   IntIdTable corresponds to a table with an auto column for entry id (i.e surrogate key)
 */

//  TODO: revise UserTable, in correspondence to the database table's entity relations
//  TODO: improve logic of field nullability

object UserTable: IntIdTable("user") {
    val userName = varchar("user_name", 50)
    val userEmail = varchar("user_email", 50)
    val userPassword = varchar("user_password", 50)
}

/**
    Entity object maps Trip type's fields to columns in the database's Trip table
 */
class UserDAO(userId: EntityID<Int>): IntEntity(userId) {
    companion object : IntEntityClass<UserDAO>(UserTable)

    var userName by UserTable.userName
    var userEmail by UserTable.userEmail
    var userPassword by UserTable.userPassword
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

fun daoToUserModel(dao: UserDAO) = User(
    dao.userName,
    dao.userEmail,
    dao.userPassword
)