package com.example.algoviz.domain.repository

interface BrowserHistoryRepository {
    /**
     * Records a URL visit in the user's browser history on Supabase.
     */
    suspend fun recordHistory(url: String): Result<Unit>
}
