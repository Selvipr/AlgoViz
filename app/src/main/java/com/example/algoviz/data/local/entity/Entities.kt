package com.example.algoviz.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.algoviz.domain.model.Topic

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val category: String,
    val name: String,
    val description: String?,
    val difficulty: String,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    @ColumnInfo(name = "is_premium") val isPremium: Boolean,
    @ColumnInfo(name = "icon_url") val iconUrl: String?,
    @ColumnInfo(name = "estimated_time_minutes") val estimatedTimeMinutes: Int,
    val track: String?,
) {
    fun toDomain() = Topic(
        id = id,
        category = category,
        name = name,
        description = description,
        difficulty = difficulty,
        orderIndex = orderIndex,
        isPremium = isPremium,
        iconUrl = iconUrl,
        estimatedTimeMinutes = estimatedTimeMinutes,
        track = track,
    )

    companion object {
        fun fromDomain(topic: Topic) = TopicEntity(
            id = topic.id,
            category = topic.category,
            name = topic.name,
            description = topic.description,
            difficulty = topic.difficulty,
            orderIndex = topic.orderIndex,
            isPremium = topic.isPremium,
            iconUrl = topic.iconUrl,
            estimatedTimeMinutes = topic.estimatedTimeMinutes,
            track = topic.track,
        )
    }
}

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "topic_id") val topicId: String,
    @ColumnInfo(name = "lesson_completed") val lessonCompleted: Boolean,
    @ColumnInfo(name = "visualization_watched") val visualizationWatched: Boolean,
    @ColumnInfo(name = "problems_solved") val problemsSolved: Int,
    @ColumnInfo(name = "quiz_score") val quizScore: Float?,
    val score: Int,
)
