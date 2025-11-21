plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
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
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${libs.versions.ktor.get()}")
    
    // AI Summary Dependencies
    implementation("io.ktor:ktor-client-core-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${libs.versions.ktor.get()}")
    implementation(libs.kotlinx.serialization.json)
    
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)

}