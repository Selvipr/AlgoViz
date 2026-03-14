package com.example.algoviz.data.repository

import com.example.algoviz.data.remote.ChatCompletionMessage
import com.example.algoviz.data.remote.ChatCompletionRequest
import com.example.algoviz.data.remote.SarvamApi
import com.example.algoviz.data.remote.TextToSpeechRequest
import com.example.algoviz.domain.repository.SarvamRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import org.json.JSONArray
import java.io.File
import javax.inject.Inject

class SarvamRepositoryImpl @Inject constructor(
    private val api: SarvamApi
) : SarvamRepository {

    // Inject your Sarvam API Key here during instantiation via DI
    private val apiKey = "sk_0uz2qvf6_bYBF3qsdKPHhbZLDlB0zCIKq"

    override suspend fun generateChat(messages: List<ChatCompletionMessage>): Result<String> {
        return try {
            // Add system prompt to prevent the model from wasting tokens on reasoning
            val systemMsg = ChatCompletionMessage(
                role = "system",
                content = "You are AlgoViz AI, a helpful coding and algorithm tutor. Always respond directly with useful content. Keep responses concise and focused."
            )
            val allMessages = listOf(systemMsg) + messages
            val request = ChatCompletionRequest(messages = allMessages)
            val response = api.generateChatCompletion(apiKey, request)
            if (response.isSuccessful && response.body() != null) {
                val rawJson = response.body()!!.string()
                val jsonObj = JSONObject(rawJson)
                val choices = jsonObj.optJSONArray("choices")
                val firstChoice = choices?.optJSONObject(0)
                val message = firstChoice?.optJSONObject("message")

                // Try content first, then reasoning_content as fallback
                var content = message?.optString("content", "") ?: ""
                if (content.isBlank() || content == "null") {
                    val reasoning = message?.optString("reasoning_content", "") ?: ""
                    if (reasoning.isNotBlank() && reasoning != "null") {
                        // Extract a useful summary from reasoning (first 500 chars)
                        content = "I processed your input but couldn't generate a direct response. Please try rephrasing your question more specifically."
                    }
                }

                Result.success(content)
            } else {
                Result.failure(Exception("Sarvam Chat Error: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun synthesizeSpeech(text: String): Result<String> {
        return try {
            val request = TextToSpeechRequest(inputs = listOf(text))
            val response = api.synthesizeSpeech(apiKey, request)
            if (response.isSuccessful && response.body() != null) {
                val audioBase64 = response.body()?.audios?.firstOrNull() ?: ""
                Result.success(audioBase64)
            } else {
                Result.failure(Exception("Sarvam TTS Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun transcribeAudio(audioFile: File): Result<String> {
        return try {
            val requestBody = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
            val audioPart = MultipartBody.Part.createFormData("file", audioFile.name, requestBody)
            val modelPart = MultipartBody.Part.createFormData("model", "saaras:v3")
            val langPart = MultipartBody.Part.createFormData("language_code", "unknown")

            val response = api.transcribeAudio(apiKey, audioPart, modelPart, langPart)
            if (response.isSuccessful && response.body() != null) {
                val jsonStr = response.body()!!.string()
                val transcript = JSONObject(jsonStr).optString("transcript", "")
                Result.success(transcript)
            } else {
                val errBody = response.errorBody()?.string()
                Result.failure(Exception("HTTP ${response.code()}: $errBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
