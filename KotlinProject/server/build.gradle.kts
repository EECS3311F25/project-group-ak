plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "org.example.project"
version = "1.0.0"

application {
    mainClass.set("org.example.project.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.server.cors.jvm)

    //  H2 driver for JDBC
    //  refer to: https://www.jetbrains.com/help/exposed/working-with-database.html#choosing-between-jdbc-and-r2dbc
    implementation(libs.h2)
    implementation(libs.exposed.jdbc)
    //  postgres dependency
    implementation(libs.postgresql)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.h2)
    
    // Flyway for database migrations
    implementation("org.flywaydb:flyway-core:10.0.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.0.0")
    
    // Kotlin Coroutines for async database operations
    implementation(libs.kotlinx.coroutines.core)
}