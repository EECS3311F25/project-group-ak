package org.example.project.config

/*
Data class :
    Normal class has default implementations of these methods, which is not useful is some of the scenarios
- equals(), hashcode(), toString(), copy(), componentN()

    In data class, we don't need to override them, Kotlin does it automatically
-
*/




data class AIConfig(
    val apiKey: String = System.getenv("AI_API_KEY") ?: "",
    val apiUrl: String = System.getenv("AI_API_URL") ?: "http://localhost:5001/api/generate-summary",
    val model: String = System.getenv("AI_MODEL") ?: "claude-3-haiku-20240307",
    val maxTokens: Int = System.getenv("AI_MAX_TOKENS")?.toIntOrNull() ?: 150,
    val temperature: Double = System.getenv("AI_TEMPERATURE")?.toDoubleOrNull() ?: 0.9
) {
    fun isValid(): Boolean {
        return apiUrl.isNotBlank()
    }
}
