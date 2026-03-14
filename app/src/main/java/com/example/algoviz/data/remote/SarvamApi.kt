package com.example.algoviz.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SarvamApi {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun generateChatCompletion(
        @Header("api-subscription-key") apiKey: String,
        @Body request: ChatCompletionRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("text-to-speech")
    suspend fun synthesizeSpeech(
        @Header("api-subscription-key") apiKey: String,
        @Body request: TextToSpeechRequest
    ): Response<TextToSpeechResponse>

    @Multipart
    @POST("speech-to-text")
    suspend fun transcribeAudio(
        @Header("api-subscription-key") apiKey: String,
        @Part file: MultipartBody.Part,
        @Part model: MultipartBody.Part,
        @Part languageCode: MultipartBody.Part
    ): Response<ResponseBody>
}
