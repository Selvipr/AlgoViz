package com.example.algoviz.ui.screens.ai

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algoviz.domain.model.AiMessage
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.util.ExportUtils
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.AnnotatedString
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.algoviz.util.AudioRecorder
import com.example.algoviz.domain.model.AiArtifact
import java.io.File
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.foundation.layout.Spacer
import com.example.algoviz.util.AudioPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCanvas: (String, Int) -> Unit = { _, _ -> },
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var expandedMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { err ->
            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Sarvam AI Assistant",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export to PDF") },
                            onClick = {
                                expandedMenu = false
                                uiState.session?.let {
                                    val success = ExportUtils.exportChatToPdf(context, it.messages_json, "AlgoViz_Chat_${System.currentTimeMillis()}")
                                    val msg = if (success) "Saved to Downloads!" else "Export Failed"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export to TXT") },
                            onClick = {
                                expandedMenu = false
                                uiState.session?.let {
                                    val success = ExportUtils.exportChatToTxt(context, it.messages_json, "AlgoViz_Chat_${System.currentTimeMillis()}")
                                    val msg = if (success) "Saved to Downloads!" else "Export Failed"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // Chat List
        val listState = rememberLazyListState()
        LaunchedEffect(uiState.session?.messages_json?.size) {
            val size = uiState.session?.messages_json?.size ?: 0
            if (size > 0) {
                listState.animateScrollToItem(size - 1)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.session?.messages_json?.let { messages ->
                items(messages) { message ->
                    ChatMessageBubble(
                        message = message,
                        onArtifactClick = { artifactIndex ->
                            onNavigateToCanvas(message.id, artifactIndex)
                        }
                    )
                }
            }
            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MintAccent, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        // Audio playback indicator
        if (uiState.isPlayingAudio) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MintAccent.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = MintAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI is speaking...", color = MintAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Input Bar
        val audioPlayer = remember { AudioPlayer(context) }
        ChatInputBar(
            onSendText = { text, base64 -> viewModel.sendMessage(text, base64) },
            onProcessAudio = { file, callback -> viewModel.processAudio(file, callback) },
            onSendVoiceMessage = { file -> viewModel.sendVoiceMessage(file, audioPlayer) }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: AiMessage,
    onArtifactClick: (Int) -> Unit = {}
) {
    val isUser = message.role.lowercase() == "user"
    val bubbleColor = if (isUser) MintAccent else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser && message.artifacts.isEmpty()) 16.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else if (message.artifacts.isEmpty()) 16.dp else 0.dp
                        )
                    )
                    .background(bubbleColor)
                    .padding(12.dp)
            ) {
                Text(
                    text = parseMarkdown(message.content),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Canvas artifact cards
            message.artifacts.forEachIndexed { index, artifact ->
                ArtifactCard(
                    artifact = artifact,
                    isLast = index == message.artifacts.lastIndex,
                    onClick = { onArtifactClick(index) }
                )
            }
        }
    }
}

@Composable
fun ArtifactCard(
    artifact: AiArtifact,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = if (isLast) 16.dp else 0.dp,
                    bottomEnd = if (isLast) 16.dp else 0.dp
                )
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Filled.Code,
                contentDescription = "Canvas",
                tint = MintAccent,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artifact.title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Tap to open in Canvas",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
            Text(
                text = artifact.language.uppercase(),
                color = MintAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun parseMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        for ((index, line) in lines.withIndex()) {
            var processedLine = line
            // Handle bullet points natively
            if (processedLine.trimStart().startsWith("* ") || processedLine.trimStart().startsWith("- ")) {
                val indent = processedLine.length - processedLine.trimStart().length
                append(" ".repeat(indent) + "• ")
                processedLine = processedLine.trimStart().substring(2)
            }
            
            var currentIndex = 0
            val regex = Regex("\\*\\*(.*?)\\*\\*|`(.*?)`")
            val matches = regex.findAll(processedLine)

            for (match in matches) {
                val startIndex = match.range.first
                if (startIndex > currentIndex) {
                    append(processedLine.substring(currentIndex, startIndex))
                }
                when {
                    match.value.startsWith("**") -> {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(match.groupValues[1])
                        }
                    }
                    match.value.startsWith("`") -> {
                        withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color.LightGray.copy(alpha = 0.3f))) {
                            append(match.groupValues[2])
                        }
                    }
                }
                currentIndex = match.range.last + 1
            }
            if (currentIndex < processedLine.length) {
                append(processedLine.substring(currentIndex))
            }
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ChatInputBar(
    onSendText: (String, String?) -> Unit,
    onProcessAudio: (File, (String) -> Unit) -> Unit,
    onSendVoiceMessage: (File) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    var attachedImageBase64 by remember { mutableStateOf<String?>(null) }
    var canvasMode by remember { mutableStateOf(false) }
    var showPlusMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Image Picker
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val base64 = uriToBase64(context, it)
            if (base64 != null) {
                attachedImageBase64 = base64
                Toast.makeText(context, "Image Attached", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Audio Recorder
    var isRecording by remember { mutableStateOf(false) }
    val audioRecorder = remember { AudioRecorder(context) }
    
    val audioPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            audioRecorder.startRecording()
            isRecording = true
            Toast.makeText(context, "Recording Started. Tap again to stop.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Canvas mode bar
    if (canvasMode) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E2E))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Code, contentDescription = null, tint = MintAccent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Canvas Mode", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(" — AI will generate code/documents", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { canvasMode = false }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // + Button with dropdown
        Box {
            IconButton(
                onClick = { showPlusMenu = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (canvasMode || attachedImageBase64 != null) MintAccent else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = if (canvasMode || attachedImageBase64 != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(
                expanded = showPlusMenu,
                onDismissRequest = { showPlusMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("📷  Image") },
                    onClick = {
                        showPlusMenu = false
                        canvasMode = false
                        imagePicker.launch("image/*")
                    }
                )
                DropdownMenuItem(
                    text = { Text("✨  Canvas (Generate Code)") },
                    onClick = {
                        showPlusMenu = false
                        attachedImageBase64 = null
                        canvasMode = true
                    }
                )
            }
        }

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = {
                Text(
                    when {
                        canvasMode -> "Describe what to generate..."
                        attachedImageBase64 != null -> "Describe image..."
                        else -> "Ask something..."
                    }
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        if (text.isNotBlank() || attachedImageBase64 != null) {
            IconButton(
                onClick = {
                    val finalText = if (canvasMode && text.isNotBlank()) {
                        "Generate code for the following. Respond ONLY with code in a proper markdown code block with the language specified. No explanations outside the code block.\n\n$text"
                    } else text
                    onSendText(finalText, attachedImageBase64)
                    text = ""
                    attachedImageBase64 = null
                    canvasMode = false
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MintAccent)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
            }
        } else {
            IconButton(
                onClick = {
                    if (isRecording) {
                        val file = audioRecorder.stopRecording()
                        isRecording = false
                        Toast.makeText(context, "Processing voice message...", Toast.LENGTH_SHORT).show()
                        file?.let {
                            onSendVoiceMessage(it)
                        }
                    } else {
                        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            audioRecorder.startRecording()
                            isRecording = true
                            Toast.makeText(context, "🎤 Recording... Tap again to send", Toast.LENGTH_SHORT).show()
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) Color.Red else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = if (isRecording) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
