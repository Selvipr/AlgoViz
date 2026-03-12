package com.example.algoviz.data.repository

import com.example.algoviz.domain.model.User
import com.example.algoviz.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
) : AuthRepository {

    override val sessionStatus: Flow<Boolean> = supabaseClient.auth.sessionStatus.map {
        it is SessionStatus.Authenticated
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            if (idToken.isNotEmpty()) {
                supabaseClient.auth.signInWith(IDToken) {
                    provider = Google
                    this.idToken = idToken
                }
            } else {
                supabaseClient.auth.signInWith(Google)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val currentUser = getCurrentUser()
                ?: return Result.failure(Exception("Failed to get user after sign in"))
            Result.success(currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, username: String): Result<User> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("username", username)
                }
            }
            val currentUser = getCurrentUser()
                ?: return Result.failure(Exception("Failed to get user after sign up"))
            Result.success(currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val session = supabaseClient.auth.currentSessionOrNull() ?: return null
        val userId = session.user?.id ?: return null

        return try {
            val result = supabaseClient.postgrest["users"]
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<UserDto>()
            result.toDomain()
        } catch (e: Exception) {
            // User might not have a profile yet
            User(
                id = userId,
                email = session.user?.email ?: "",
            )
        }
    }

    override fun isLoggedIn(): Boolean {
        return supabaseClient.auth.currentSessionOrNull() != null
    }
}

@kotlinx.serialization.Serializable
private data class UserDto(
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
