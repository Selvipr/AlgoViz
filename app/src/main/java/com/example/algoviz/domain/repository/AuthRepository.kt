package com.example.algoviz.domain.repository

import com.example.algoviz.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessionStatus: Flow<Boolean>
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, username: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
}
