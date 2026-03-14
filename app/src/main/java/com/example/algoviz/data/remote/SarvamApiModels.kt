package com.example.algoviz.data.remote

import com.google.gson.annotations.SerializedName

// -------------- Text Chat (Completions) --------------

data class ChatCompletionRequest(
    val messages: List<ChatCompletionMessage>,
    val model: String = "sarvam-30b-16k",
    val temperature: Double = 0.2,
    val top_p: Double = 1.0,
    val max_tokens: Int = 4096
)

data class ChatCompletionMessage(
    val role: String, // "system", "user", "assistant"
    val content: Any // String or List<Map<String, Any>> for Vision
)

data class ChatCompletionResponse(
    val id: String?,
    val choices: List<Choice>?
)

data class Choice(
    val message: ChatCompletionMessage?,
    val finish_reason: String?
)

// -------------- Text to Speech --------------

data class TextToSpeechRequest(
    val inputs: List<String>,
    @SerializedName("target_language_code") val targetLanguageCode: String = "en-IN",
    val speaker: String = "meera",
    val pace: Double = 1.0,
    @SerializedName("speech_sample_rate") val speechSampleRate: Int = 22050,
    @SerializedName("enable_preprocessing") val enablePreprocessing: Boolean = true,
    @SerializedName("model") val model: String = "bulbul:v3"
)

data class TextToSpeechResponse(
    val audios: List<String> // Base64 encoded WAV data
)

// -------------- Speech To Text --------------
// (STT usually requires Multipart forms natively in Retrofit)
