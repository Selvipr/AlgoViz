package com.example.algoviz.domain.repository

import com.example.algoviz.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    fun getTopics(): Flow<List<Topic>>
    fun getTopicsByCategory(category: String): Flow<List<Topic>>
    fun getTopicsByTrack(track: String): Flow<List<Topic>>
    suspend fun getTopicById(id: String): Topic?
    suspend fun refreshTopics()
}
