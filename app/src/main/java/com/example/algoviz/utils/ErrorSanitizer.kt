package com.example.algoviz.utils

object ErrorSanitizer {
    /**
     * Extracts a user-friendly error message from raw SDK/Network exceptions.
     * Prevents bubbling up raw URLs or API keys to the UI.
     */
    fun sanitize(exception: Exception?): String {
        if (exception == null) return "An unknown error occurred."
        val rawMessage = exception.message ?: "An unknown error occurred."

        // 1. Generic Network Errors
        if (rawMessage.contains("Unable to resolve host", ignoreCase = true) || 
            rawMessage.contains("Failed to connect", ignoreCase = true) ||
            rawMessage.contains("Network is unreachable", ignoreCase = true)) {
            return "Please check your internet connection and try again."
        }

        // 2. Supabase specific masking
        // Ktor/Supabase often throw errors like:
        // "Failed to construct request: https://[YOUR_INSTANCE].supabase.co/rest/v1/... (401 Unauthorized)"
        if (rawMessage.contains("supabase.co", ignoreCase = true) || rawMessage.contains("http", ignoreCase = true)) {
            
            // Try to extract the HTTP status code description if available
            if (rawMessage.contains("401", ignoreCase = true)) {
                return "Authentication failed. Please log in again."
            }
            if (rawMessage.contains("403", ignoreCase = true)) {
                return "You do not have permission to perform this action."
            }
            if (rawMessage.contains("404", ignoreCase = true)) {
                return "The requested resource could not be found."
            }
            if (rawMessage.contains("422", ignoreCase = true)) {
                return "Invalid data provided. Please check your inputs."
            }
            
            // Generic fallback for leaked API URLs
            return "A network error occurred while communicating with the server."
        }

        // 3. Known clean errors
        if (rawMessage.contains("Invalid login credentials", ignoreCase = true)) {
            return "Invalid email or password."
        }

        // If it looks relatively clean (no URLs, reasonable length), let it pass
        if (rawMessage.length < 150 && !rawMessage.contains("http")) {
            return rawMessage
        }

        return "An unexpected error occurred. Please try again later."
    }
}
