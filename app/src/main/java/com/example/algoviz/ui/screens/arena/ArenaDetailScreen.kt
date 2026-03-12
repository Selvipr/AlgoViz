package com.example.algoviz.ui.screens.arena

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.algoviz.ui.theme.DeepNavy
import com.example.algoviz.ui.theme.MintAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaDetailScreen(
    problemId: String,
    onNavigateBack: () -> Unit,
    viewModel: ArenaViewModel = hiltViewModel()
) {
    val currentProblem by viewModel.currentProblem.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val results by viewModel.executionResults.collectAsState()
    val userCode by viewModel.userCode.collectAsState()

    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var initialSyncDone by remember { mutableStateOf(false) }

    LaunchedEffect(problemId) {
        viewModel.loadProblem(problemId)
    }

    LaunchedEffect(userCode) {
        if (!initialSyncDone && userCode.isNotEmpty()) {
            textState = TextFieldValue(userCode)
            initialSyncDone = true
        }
    }

    if (currentProblem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MintAccent)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentProblem!!.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.updateCode(textState.text)
                            viewModel.executeCode()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintAccent, contentColor = DeepNavy),
                        modifier = Modifier.padding(end = 8.dp),
                        enabled = !isExecuting
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Run", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run Code", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Problem Description (Top Half)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val diffColor = when (currentProblem!!.difficulty) {
                        "Easy" -> Color(0xFF4CAF50)
                        "Medium" -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                    Text(
                        text = currentProblem!!.difficulty,
                        color = diffColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(diffColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentProblem!!.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // Code Editor (Middle Section)
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E))
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = textState,
                    onValueChange = { newValue ->
                        textState = newValue
                    },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = Color(0xFFD4D4D4)
                    ),
                    cursorBrush = SolidColor(MintAccent),
                    visualTransformation = remember {
                        androidx.compose.ui.text.input.VisualTransformation { text ->
                            val pattern = Regex("\\b(public|class|int|return|boolean|String|new|if|else|for|while|true|false)\\b")
                            val annotatedString = buildAnnotatedString {
                                var lastIndex = 0
                                pattern.findAll(text.text).forEach { matchResult ->
                                    val keyword = matchResult.value
                                    val start = matchResult.range.first
                                    val end = matchResult.range.last + 1
                                    
                                    append(text.text.substring(lastIndex, start))
                                    withStyle(style = SpanStyle(color = Color(0xFF569CD6))) {
                                        append(keyword)
                                    }
                                    lastIndex = end
                                }
                                append(text.text.substring(lastIndex))
                            }
                            androidx.compose.ui.text.input.TransformedText(annotatedString, androidx.compose.ui.text.input.OffsetMapping.Identity)
                        }
                    }
                )
            }

            // Results Console (Bottom Section)
            if (results.isNotEmpty()) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Test Results", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    results.forEachIndexed { index, result ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (result.passed) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                        contentDescription = null,
                                        tint = if (result.passed) Color(0xFF4CAF50) else Color(0xFFF44336),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Test Case ${index + 1} " + if (result.testCase.isHidden) "(Hidden)" else "",
                                        fontWeight = FontWeight.Bold,
                                        color = if (result.passed) Color(0xFF4CAF50) else Color(0xFFF44336)
                                    )
                                }
                                if (!result.testCase.isHidden || !result.passed) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Expected: ${result.testCase.expectedOutput}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (result.errorMessage != null) {
                                        Text("Error: ${result.errorMessage}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF44336))
                                    } else {
                                        Text("Actual: ${result.actualOutput}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
