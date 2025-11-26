package org.example.project.config

/**
 * Web/JS implementation of Config
 * 
 * Reads configuration from webpack-injected environment variables
 * or falls back to window object
 */
actual object Config {
    actual val MAPBOX_ACCESS_TOKEN: String
        get() {
            console.log("Config: Attempting to read MAPBOX_ACCESS_TOKEN")
            
            // Direct access to window object (set from localStorage in index.html)
            val token = js("window.MAPBOX_ACCESS_TOKEN || ''").unsafeCast<String>()
            
            console.log("Config: window.MAPBOX_ACCESS_TOKEN =", js("window.MAPBOX_ACCESS_TOKEN"))
            console.log("Config: Token type =", js("typeof window.MAPBOX_ACCESS_TOKEN"))
            console.log("Config: Final token length =", token.length)
            console.log("Config: Token value (first 10 chars) =", if (token.length > 0) token.take(10) else "EMPTY")
            
            if (token.isEmpty()) {
                console.error("❌ MAPBOX_ACCESS_TOKEN is not set!")
                console.error("Set it in browser console with:")
                console.error("localStorage.setItem('MAPBOX_ACCESS_TOKEN', 'pk.your_token_here')")
                console.error("Then refresh the page")
            }
            
            return token
        }
}
