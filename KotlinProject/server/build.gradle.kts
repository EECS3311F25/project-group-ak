plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.flyway)
    application
    java
}

group = "org.example.project"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))  // Lock to Java 21 LTS
    }
    
    // Ensure source and target compatibility
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
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
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.sessions)

    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.encoding)


    //  H2 driver for JDBC
    //  refer to: https://www.jetbrains.com/help/exposed/working-with-database.html#choosing-between-jdbc-and-r2dbc
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.flyway.core)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.logback.classic)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
}