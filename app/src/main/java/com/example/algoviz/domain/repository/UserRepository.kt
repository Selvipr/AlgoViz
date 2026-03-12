package com.example.algoviz.domain.repository

import com.example.algoviz.domain.model.User

interface UserRepository {
    suspend fun getUserProfile(userId: String): Result<User>
    suspend fun updateUserProfile(user: User): Result<User>
    suspend fun updateStreak(userId: String): Result<Int>
    suspend fun addXp(userId: String, amount: Int): Result<Int>
}
