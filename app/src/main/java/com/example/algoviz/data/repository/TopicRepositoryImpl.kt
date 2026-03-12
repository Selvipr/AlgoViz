package com.example.algoviz.data.repository

import com.example.algoviz.data.local.dao.TopicDao
import com.example.algoviz.data.local.entity.TopicEntity
import com.example.algoviz.domain.model.Topic
import com.example.algoviz.domain.repository.TopicRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val topicDao: TopicDao,
) : TopicRepository {

    override fun getTopics(): Flow<List<Topic>> {
        return topicDao.getAllTopics().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTopicsByCategory(category: String): Flow<List<Topic>> {
        return topicDao.getTopicsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTopicsByTrack(track: String): Flow<List<Topic>> {
        return topicDao.getTopicsByTrack(track).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTopicById(id: String): Topic? {
        return topicDao.getTopicById(id)?.toDomain()
    }

    override suspend fun refreshTopics() {
        try {
            val remoteDtos = supabaseClient.postgrest["topics"]
                .select()
                .decodeList<TopicDto>()

            val entities = remoteDtos.map { dto ->
                TopicEntity(
                    id = dto.id,
                    category = dto.category,
                    name = dto.name,
                    description = dto.description,
                    difficulty = dto.difficulty,
                    orderIndex = dto.order_index,
                    isPremium = dto.is_premium,
                    iconUrl = dto.icon_url,
                    estimatedTimeMinutes = dto.estimated_time_minutes,
                    track = dto.track,
                )
            }
            topicDao.deleteAll()
            topicDao.insertAll(entities)
        } catch (_: Exception) {
            // Offline — use cached data
        }
    }
}

@Serializable
private data class TopicDto(
    val id: String = "",
    val category: String = "",
    val name: String = "",
    val description: String? = null,
    val difficulty: String = "beginner",
    val order_index: Int = 0,
    val is_premium: Boolean = false,
    val icon_url: String? = null,
    val estimated_time_minutes: Int = 30,
    val track: String? = null,
)
