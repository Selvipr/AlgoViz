package com.example.algoviz.data.repository

import com.example.algoviz.domain.model.AiSession
import com.example.algoviz.domain.repository.AiSessionRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject

class AiSessionRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : AiSessionRepository {

    override fun getSessionsFlow(userId: String): Flow<List<AiSession>> = callbackFlow {
        var isPolling = true
        val pollJob = GlobalScope.launch {
            while (isPolling) {
                try {
                    val sessions = supabase.postgrest["ai_sessions"]
                        .select {
                            filter {
                                eq("user_id", userId)
                            }
                            order("created_at", order = Order.DESCENDING)
                        }
                        .decodeList<AiSession>()
                    trySend(sessions)
                } catch (e: Exception) {
                    // Wait and loop
                }
                delay(3000)
            }
        }

        awaitClose { 
            isPolling = false
            pollJob.cancel()
        }
    }

    override suspend fun getSession(sessionId: String): Result<AiSession> {
        return try {
            val session = supabase.postgrest["ai_sessions"]
                .select {
                    filter { eq("id", sessionId) }
                }
                .decodeSingle<AiSession>()
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveSession(session: AiSession): Result<Unit> {
        return try {
            supabase.postgrest["ai_sessions"].upsert(session)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("AiSessionRepo", "saveSession failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            supabase.postgrest["ai_sessions"]
                .delete {
                    filter { eq("id", sessionId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
