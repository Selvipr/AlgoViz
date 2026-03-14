package com.example.algoviz.domain.repository

import com.example.algoviz.domain.model.AiSession
import kotlinx.coroutines.flow.Flow

interface AiSessionRepository {
    fun getSessionsFlow(userId: String): Flow<List<AiSession>>
    suspend fun getSession(sessionId: String): Result<AiSession>
    suspend fun saveSession(session: AiSession): Result<Unit>
    suspend fun deleteSession(sessionId: String): Result<Unit>
}
