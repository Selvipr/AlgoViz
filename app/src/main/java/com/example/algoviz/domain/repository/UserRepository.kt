package com.example.algoviz.domain.repository

import com.example.algoviz.domain.model.User

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(userId: String): Result<User>
    fun getUserProfileFlow(userId: String): Flow<Result<User>>
    fun getTopUsersFlow(limit: Int = 100): Flow<Result<List<User>>>
    suspend fun updateUserProfile(user: User): Result<User>
    suspend fun updateStreak(userId: String): Result<Int>
    suspend fun addXp(userId: String, amount: Int): Result<Int>
}
