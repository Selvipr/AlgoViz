package com.example.algoviz.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiArtifact(
    val id: String,
    val title: String,
    val type: String, // "code", "document", "summary"
    val language: String = "", // e.g. "python", "kotlin", "markdown"
    val content: String
)

@Serializable
data class AiMessage(
    val id: String,
    val role: String, // "system", "user", "assistant"
    val content: String,
    val artifacts: List<AiArtifact> = emptyList(),
    val timestampMs: Long = System.currentTimeMillis()
)

@Serializable
data class AiSession(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("topic_id") val topicId: String? = null,
    val title: String? = null,
    val messages_json: List<AiMessage> = emptyList(),
    val language: String = "en",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

