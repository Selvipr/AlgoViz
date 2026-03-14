package com.example.algoviz.ui.screens.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.algoviz.domain.model.AiArtifact
import com.example.algoviz.ui.theme.MintAccent
import java.io.File

// Dark code theme colors
private val CodeBackground = Color(0xFF1E1E2E)
private val CodeForeground = Color(0xFFCDD6F4)
private val CodeLineNumber = Color(0xFF585B70)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    artifact: AiArtifact?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var showExportMenu by remember { mutableStateOf(false) }

    if (artifact == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Artifact not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = artifact.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        if (artifact.language.isNotBlank()) {
                            Text(
                                text = artifact.language.uppercase(),
                                fontSize = 11.sp,
                                color = MintAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Copy
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("code", artifact.content))
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = MintAccent)
                    }

                    // Share
                    IconButton(onClick = {
                        shareArtifact(context, artifact)
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share", tint = MintAccent)
                    }

                    // Export dropdown
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(Icons.Filled.Download, contentDescription = "Export", tint = MintAccent)
                        }
                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            val ext = getExtension(artifact.language)
                            DropdownMenuItem(
                                text = { Text("📄  Save as .$ext") },
                                onClick = {
                                    showExportMenu = false
                                    saveArtifactToFile(context, artifact, ext)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📝  Save as .txt") },
                                onClick = {
                                    showExportMenu = false
                                    saveArtifactToFile(context, artifact, "txt")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📋  Save as .md (Markdown)") },
                                onClick = {
                                    showExportMenu = false
                                    val mdContent = "# ${artifact.title}\n\n```${artifact.language}\n${artifact.content}\n```"
                                    saveContentToFile(context, artifact.title, mdContent, "md")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🌐  Save as .html") },
                                onClick = {
                                    showExportMenu = false
                                    val htmlContent = buildHtmlExport(artifact)
                                    saveContentToFile(context, artifact.title, htmlContent, "html")
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CodeBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CodeBackground)
        ) {
            // Code content with line numbers
            val lines = artifact.content.split("\n")
            val verticalScroll = rememberScrollState()
            val horizontalScroll = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScroll)
                    .horizontalScroll(horizontalScroll)
                    .padding(8.dp)
            ) {
                Row {
                    // Line numbers column
                    Column(
                        modifier = Modifier.padding(end = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        lines.forEachIndexed { index, _ ->
                            Text(
                                text = "${index + 1}",
                                color = CodeLineNumber,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Separator line
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height((lines.size * 20).dp)
                            .background(CodeLineNumber.copy(alpha = 0.3f))
                    )

                    // Code content
                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        lines.forEach { line ->
                            Text(
                                text = line,
                                color = CodeForeground,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------- Utility functions ----------

private fun getExtension(language: String): String {
    return when (language.lowercase()) {
        "python" -> "py"
        "kotlin" -> "kt"
        "java" -> "java"
        "javascript", "js" -> "js"
        "typescript", "ts" -> "ts"
        "html" -> "html"
        "css" -> "css"
        "sql" -> "sql"
        "c" -> "c"
        "cpp" -> "cpp"
        "swift" -> "swift"
        "dart" -> "dart"
        "json" -> "json"
        "xml" -> "xml"
        "markdown", "md" -> "md"
        "bash", "sh" -> "sh"
        "yaml", "yml" -> "yml"
        "go" -> "go"
        "rust" -> "rs"
        "ruby" -> "rb"
        "php" -> "php"
        else -> "txt"
    }
}

private fun shareArtifact(context: Context, artifact: AiArtifact) {
    val ext = getExtension(artifact.language)
    try {
        // Write to a temporary file for sharing
        val tempFile = File(context.cacheDir, "${artifact.title.replace(" ", "_")}.$ext")
        tempFile.writeText(artifact.content)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, artifact.content)
            putExtra(Intent.EXTRA_SUBJECT, artifact.title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share ${artifact.title}"))
    } catch (e: Exception) {
        // Fallback: share as plain text
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "// ${artifact.title}\n// Language: ${artifact.language}\n\n${artifact.content}")
            putExtra(Intent.EXTRA_SUBJECT, artifact.title)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share ${artifact.title}"))
    }
}

private fun saveArtifactToFile(context: Context, artifact: AiArtifact, extension: String) {
    saveContentToFile(context, artifact.title, artifact.content, extension)
}

private fun saveContentToFile(context: Context, title: String, content: String, extension: String) {
    val fileName = "AlgoViz_${title.replace(" ", "_")}_${System.currentTimeMillis()}.$extension"

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val mimeType = when (extension) {
                "html" -> "text/html"
                "json" -> "application/json"
                "md" -> "text/markdown"
                "xml" -> "application/xml"
                else -> "text/plain"
            }
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { os ->
                    os.write(content.toByteArray())
                }
            }
        }
        Toast.makeText(context, "✅ Saved to Downloads/$fileName", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun buildHtmlExport(artifact: AiArtifact): String {
    val escapedContent = artifact.content
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

    return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${artifact.title} - AlgoViz Canvas</title>
    <style>
        body {
            background: #1E1E2E;
            color: #CDD6F4;
            font-family: 'Courier New', monospace;
            margin: 0;
            padding: 0;
        }
        .header {
            background: #181825;
            padding: 16px 24px;
            border-bottom: 1px solid #313244;
        }
        .header h1 {
            margin: 0;
            font-size: 18px;
            color: #CDD6F4;
        }
        .header .lang {
            color: #A6E3A1;
            font-size: 12px;
            font-weight: bold;
            margin-top: 4px;
        }
        .code-container {
            display: flex;
            padding: 16px;
        }
        .line-numbers {
            text-align: right;
            padding-right: 16px;
            color: #585B70;
            user-select: none;
            border-right: 1px solid #31324440;
            margin-right: 16px;
        }
        pre {
            margin: 0;
            line-height: 1.6;
            font-size: 14px;
        }
        .footer {
            background: #181825;
            padding: 12px 24px;
            text-align: center;
            color: #585B70;
            font-size: 12px;
            border-top: 1px solid #313244;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>${artifact.title}</h1>
        <div class="lang">${artifact.language.uppercase()}</div>
    </div>
    <div class="code-container">
        <div class="line-numbers"><pre>${
        artifact.content.split("\n").indices.joinToString("\n") { "${it + 1}" }
    }</pre></div>
        <div><pre>$escapedContent</pre></div>
    </div>
    <div class="footer">Generated by AlgoViz Canvas</div>
</body>
</html>
    """.trimIndent()
}
