import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

repositories {
    google()
    mavenCentral()
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.decompose)
            implementation(libs.decompose.extensions.compose)
            // Mobile-specific Compass implementations
            implementation(libs.compass.geocoder.mobile)
            implementation(libs.compass.geolocation.mobile)
            implementation(libs.compass.autocomplete.mobile)
            implementation(libs.compass.permissions.mobile)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation(projects.shared)
            implementation(libs.decompose)
            implementation(libs.decompose.extensions.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.cio)
            // Geocoding
            implementation(libs.compass.geocoder)

            // To use geocoding you need to use one or more of the following

            // Optional - Geocoder support for all platforms, but requires an API key from the service
            implementation(libs.compass.geocoder.web.googlemaps)
            implementation(libs.compass.geocoder.web.mapbox)
            implementation(libs.compass.geocoder.web.opencage)

            // Optional - If you want to create your own geocoder implementation
            implementation(libs.compass.geocoder.web)
            
            // Geolocation
            implementation(libs.compass.geolocation)

            // Autocomplete
            implementation(libs.compass.autocomplete)

            // Optional - Autocomplete support for all platforms, using services Geocoder APIs
            implementation(libs.compass.autocomplete.geocoder.googlemaps)
            implementation(libs.compass.autocomplete.geocoder.mapbox)

            // Optional - If you want to create your own geocoder implementation
            implementation(libs.compass.autocomplete.web)
        }
        iosMain.dependencies {
            // Mobile-specific Compass implementations
            implementation(libs.compass.geocoder.mobile)
            implementation(libs.compass.geolocation.mobile)
            implementation(libs.compass.autocomplete.mobile)
            implementation(libs.compass.permissions.mobile)
        }
        webMain.dependencies {
            // Browser-specific Compass implementation
            implementation(libs.compass.geolocation.browser)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.kamel.image)
            implementation(libs.ktor.client.cio)
            implementation("ch.qos.logback:logback-classic:1.5.18")  //SLF4J provider error for networkImage
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}
