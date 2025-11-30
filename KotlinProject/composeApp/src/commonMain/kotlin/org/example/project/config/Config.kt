package org.example.project.config

/**
 * Configuration values loaded from build-time environment
 * 
 * Common interface for accessing configuration across platforms
 */
expect object Config {
    val MAPBOX_ACCESS_TOKEN: String
}
