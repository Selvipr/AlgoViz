package com.example.algoviz.data.repository

import com.example.algoviz.domain.repository.AuthRepository
import com.example.algoviz.domain.repository.BrowserHistoryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import javax.inject.Inject

class BrowserHistoryRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository
) : BrowserHistoryRepository {

    override suspend fun recordHistory(url: String): Result<Unit> {
        return try {
            val user = authRepository.getCurrentUser()
                ?: return Result.failure(Exception("User not logged in to record history"))

            val historyDto = BrowserHistoryDto(
                user_id = user.id,
                url = url
            )

            supabaseClient.postgrest["browser_history"]
                .insert(historyDto)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
private data class BrowserHistoryDto(
    val user_id: String,
    val url: String
)
