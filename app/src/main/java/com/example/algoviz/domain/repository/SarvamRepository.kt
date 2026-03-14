package com.example.algoviz.domain.repository

import com.example.algoviz.data.remote.ChatCompletionMessage
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SarvamRepository {
    suspend fun generateChat(messages: List<ChatCompletionMessage>): Result<String>
    suspend fun synthesizeSpeech(text: String): Result<String> // Returns Base64 Audio string
    suspend fun transcribeAudio(audioFile: File): Result<String> // Returns Transcribed text
}
