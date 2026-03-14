package com.example.algoviz.data.repository

import com.example.algoviz.domain.model.User
import com.example.algoviz.domain.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
) : UserRepository {

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val result = supabaseClient.postgrest["users"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeSingle<UserProfileDto>()
            Result.success(result.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserProfileFlow(userId: String): Flow<Result<User>> = flow {
        while (true) {
            emit(getUserProfile(userId))
            delay(5000) // Poll every 5 seconds to simulate realtime
        }
    }

    override fun getTopUsersFlow(limit: Int): Flow<Result<List<User>>> = flow {
        while (true) {
            try {
                val result = supabaseClient.postgrest["users"]
                    .select {
                        order("xp", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                        limit(count = limit.toLong())
                    }
                    .decodeList<UserProfileDto>()
                emit(Result.success(result.map { it.toDomain() }))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
            delay(10000) // Poll every 10 seconds for leaderboard
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            supabaseClient.postgrest["users"]
                .update(
                    UserProfileUpdateDto(
                        username = user.username,
                        full_name = user.fullName,
                        bio = user.bio,
                        college = user.college,
                        location = user.location,
                        github_url = user.githubUrl,
                        linkedin_url = user.linkedinUrl,
                        sarvam_language = user.sarvamLanguage,
                        ui_language = user.uiLanguage,
                        daily_xp_goal = user.dailyXpGoal,
                        visualization_speed = user.visualizationSpeed,
                    )
                ) {
                    filter { eq("id", user.id) }
                }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStreak(userId: String): Result<Int> {
        return try {
            val profile = getUserProfile(userId).getOrThrow()
            val newStreak = profile.streak + 1
            val newMaxStreak = maxOf(newStreak, profile.maxStreak)
            supabaseClient.postgrest["users"]
                .update(mapOf("streak" to newStreak, "max_streak" to newMaxStreak)) {
                    filter { eq("id", userId) }
                }
            Result.success(newStreak)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addXp(userId: String, amount: Int): Result<Int> {
        return try {
            val profile = getUserProfile(userId).getOrThrow()
            val newXp = profile.xp + amount
            val newTier = calculateTier(newXp)
            supabaseClient.postgrest["users"]
                .update(mapOf("xp" to newXp, "tier" to newTier)) {
                    filter { eq("id", userId) }
                }
            Result.success(newXp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateTier(xp: Int): String {
        return when {
            xp >= 50000 -> "grandmaster"
            xp >= 15000 -> "master"
            xp >= 5000 -> "expert"
            xp >= 1500 -> "practitioner"
            xp >= 500 -> "learner"
            else -> "novice"
        }
    }
}

@Serializable
private data class UserProfileDto(
    val id: String = "",
    val email: String? = null,
    val username: String? = null,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val bio: String? = null,
    val college: String? = null,
    val location: String? = null,
    val github_url: String? = null,
    val linkedin_url: String? = null,
    val streak: Int = 0,
    val max_streak: Int = 0,
    val xp: Int = 0,
    val tier: String = "novice",
    val sarvam_language: String = "en",
    val ui_language: String = "en",
    val is_premium: Boolean = false,
    val daily_xp_goal: Int = 50,
    val visualization_speed: Float = 1.0f,
) {
    fun toDomain() = User(
        id = id,
        email = email ?: "",
        username = username ?: "",
        fullName = full_name,
        avatarUrl = avatar_url,
        bio = bio,
        college = college,
        location = location,
        githubUrl = github_url,
        linkedinUrl = linkedin_url,
        streak = streak,
        maxStreak = max_streak,
        xp = xp,
        tier = tier,
        sarvamLanguage = sarvam_language,
        uiLanguage = ui_language,
        isPremium = is_premium,
        dailyXpGoal = daily_xp_goal,
        visualizationSpeed = visualization_speed,
    )
}

@Serializable
private data class UserProfileUpdateDto(
    val username: String? = null,
    val full_name: String? = null,
    val bio: String? = null,
    val college: String? = null,
    val location: String? = null,
    val github_url: String? = null,
    val linkedin_url: String? = null,
    val sarvam_language: String? = null,
    val ui_language: String? = null,
    val daily_xp_goal: Int? = null,
    val visualization_speed: Float? = null,
)
