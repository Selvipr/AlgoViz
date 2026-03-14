package com.example.algoviz.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.data.remote.ChatCompletionMessage
import com.example.algoviz.domain.model.AiArtifact
import com.example.algoviz.domain.model.AiMessage
import com.example.algoviz.domain.model.AiSession
import com.example.algoviz.domain.repository.AiSessionRepository
import com.example.algoviz.domain.repository.AuthRepository
import com.example.algoviz.domain.repository.SarvamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

data class AiChatUiState(
    val session: AiSession? = null,
    val isLoading: Boolean = false,
    val isPlayingAudio: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val sarvamRepository: SarvamRepository,
    private val aiSessionRepository: AiSessionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        initializeSession()
    }

    private fun initializeSession() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                // Fetch existing sessions once on load
                try {
                    val sessions = aiSessionRepository.getSessionsFlow(user.id).first()
                    if (sessions.isNotEmpty()) {
                        // Load the most recent session
                        val recentSession = sessions.first()
                        _uiState.update { it.copy(session = recentSession) }
                    } else {
                        // Create a new session if none exist
                        val newSession = AiSession(
                            id = UUID.randomUUID().toString(),
                            userId = user.id,
                            title = "New Chat",
                            messages_json = emptyList()
                        )
                        aiSessionRepository.saveSession(newSession)
                        _uiState.update { it.copy(session = newSession) }
                        
                        // Greet user
                        appendMessage(AiMessage(
                            id = UUID.randomUUID().toString(),
                            role = "assistant",
                            content = "Hello ${user.fullName ?: user.username}! How can I assist you with Algorithms or coding today?"
                        ), autoPersist = true)
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to load history") }
                }
            } else {
                _uiState.update { it.copy(error = "User not logged in") }
            }
        }
    }

    fun sendMessage(text: String, base64Image: String? = null) {
        if (text.isBlank() && base64Image == null) return
        
        val displayContent = if (base64Image != null) "$text\n[Image Attached]" else text
        val userMsg = AiMessage(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = displayContent
        )
        appendMessage(userMsg, autoPersist = true)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Map our domain history into Sarvam's expected format
            // Filter out any empty assistant messages that slipped in
            val currentSession = _uiState.value.session ?: return@launch
            val messagesMapped = currentSession.messages_json
                .filter { it.content.isNotBlank() }
                .map { aiMsg ->
                    if (aiMsg.id == userMsg.id && base64Image != null) {
                        val contentList = listOf(
                            mapOf("type" to "text", "text" to text.ifBlank { "Explain this image" }),
                            mapOf("type" to "image_url", "image_url" to mapOf("url" to "data:image/jpeg;base64,$base64Image"))
                        )
                        ChatCompletionMessage(role = aiMsg.role, content = contentList)
                    } else {
                        ChatCompletionMessage(role = aiMsg.role, content = aiMsg.content)
                    }
                }
            
            val result = sarvamRepository.generateChat(messagesMapped)
            
            result.onSuccess { replyTxt ->
                // Only add the message if it has actual content
                if (replyTxt.isNotBlank()) {
                    val artifacts = parseArtifacts(replyTxt)
                    val astMsg = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = replyTxt,
                        artifacts = artifacts
                    )
                    appendMessage(astMsg, autoPersist = true)
                } else {
                    _uiState.update { it.copy(error = "AI returned empty response. Please try again.") }
                }
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to reach Sarvam AI") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun processAudio(audioFile: File, onTranscriptionResult: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = sarvamRepository.transcribeAudio(audioFile)
            result.onSuccess { text ->
                if (text.isNotBlank()) {
                    onTranscriptionResult(text)
                    // Auto-send the transcribed text to the AI
                    sendMessage(text)
                } else {
                    _uiState.update { it.copy(error = "Could not understand audio. Try again.") }
                }
            }.onFailure { err ->
                _uiState.update { it.copy(error = "Audio Error: ${err.message}") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Full talk-to-talk pipeline:
     * Record → STT (Saaras v3) → Chat (Sarvam-30B) → TTS (Bulbul v3) → Playback
     */
    fun sendVoiceMessage(audioFile: File, audioPlayer: com.example.algoviz.util.AudioPlayer) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Step 1: STT — Transcribe audio to text
            val sttResult = sarvamRepository.transcribeAudio(audioFile)
            val userText = sttResult.getOrNull()
            if (userText.isNullOrBlank()) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Could not understand audio: ${sttResult.exceptionOrNull()?.message ?: "empty transcript"}"
                )}
                return@launch
            }

            // Add user message to chat
            val userMsg = AiMessage(
                id = UUID.randomUUID().toString(),
                role = "user",
                content = "🎤 $userText"
            )
            appendMessage(userMsg, autoPersist = true)

            // Step 2: Chat — Send to AI
            val currentSession = _uiState.value.session ?: return@launch
            val messagesMapped = currentSession.messages_json
                .filter { it.content.isNotBlank() }
                .map { ChatCompletionMessage(role = it.role, content = it.content) }

            val chatResult = sarvamRepository.generateChat(messagesMapped)
            val replyText = chatResult.getOrNull()
            if (replyText.isNullOrBlank()) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "AI response failed: ${chatResult.exceptionOrNull()?.message ?: "empty reply"}"
                )}
                return@launch
            }

            // Add assistant response
            val artifacts = parseArtifacts(replyText)
            val astMsg = AiMessage(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = replyText,
                artifacts = artifacts
            )
            appendMessage(astMsg, autoPersist = true)

            // Step 3: TTS — Convert reply to speech
            val ttsResult = sarvamRepository.synthesizeSpeech(replyText)
            ttsResult.onSuccess { base64Audio ->
                if (base64Audio.isNotBlank()) {
                    _uiState.update { it.copy(isLoading = false, isPlayingAudio = true) }
                    audioPlayer.playBase64Audio(base64Audio) {
                        _uiState.update { it.copy(isPlayingAudio = false) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }.onFailure {
                // TTS failed but chat response is still shown — non-critical
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun appendMessage(msg: AiMessage, autoPersist: Boolean = false) {
        _uiState.update { state ->
            val updatedSession = state.session?.let { 
                it.copy(messages_json = it.messages_json + msg)
            }
            if (autoPersist && updatedSession != null) {
                viewModelScope.launch {
                    aiSessionRepository.saveSession(updatedSession)
                }
            }
            state.copy(session = updatedSession)
        }
    }

    private fun parseArtifacts(content: String): List<AiArtifact> {
        val artifacts = mutableListOf<AiArtifact>()
        // Match ```language\n...code...\n```
        val codeBlockRegex = Regex("```(\\w*)\n([\\s\\S]*?)```")
        val matches = codeBlockRegex.findAll(content)
        var index = 0
        for (match in matches) {
            val lang = match.groupValues[1].ifBlank { "text" }
            val code = match.groupValues[2].trimEnd()
            val title = when (lang.lowercase()) {
                "python" -> "Python Script"
                "kotlin" -> "Kotlin Code"
                "java" -> "Java Code"
                "javascript", "js" -> "JavaScript Code"
                "html" -> "HTML Document"
                "css" -> "CSS Stylesheet"
                "sql" -> "SQL Query"
                "c", "cpp" -> "C/C++ Code"
                "swift" -> "Swift Code"
                "dart" -> "Dart Code"
                "json" -> "JSON Data"
                "xml" -> "XML Document"
                "markdown", "md" -> "Markdown Document"
                "bash", "sh" -> "Shell Script"
                else -> "Code Snippet"
            }
            artifacts.add(
                AiArtifact(
                    id = UUID.randomUUID().toString(),
                    title = "$title ${if (index > 0) "(${index + 1})" else ""}".trim(),
                    type = "code",
                    language = lang,
                    content = code
                )
            )
            index++
        }
        return artifacts
    }
}
