package com.example.algoviz.ui.screens.visualize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.ui.screens.visualize.components.ArrayCanvas
import com.example.algoviz.ui.screens.visualize.components.GraphCanvas
import com.example.algoviz.ui.screens.visualize.components.TreeCanvas
import com.example.algoviz.ui.theme.DeepNavy
import com.example.algoviz.ui.theme.InfoBlue
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizationPlayerScreen(
    vizId: String,
    onNavigateBack: () -> Unit,
    viewModel: VisualizationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showInfoSheet by remember { mutableStateOf(false) }

    LaunchedEffect(vizId) {
        viewModel.initialize(vizId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showInfoSheet = true },
                containerColor = DeepNavy,
                contentColor = MintAccent
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Algorithm Info")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState.currentStep?.state) {
                    is VisualizationState.ArrayState -> {
                        ArrayCanvas(
                            step = uiState.currentStep,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is VisualizationState.GraphState -> {
                        GraphCanvas(
                            step = uiState.currentStep,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is VisualizationState.TreeState -> {
                        TreeCanvas(
                            step = uiState.currentStep,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    null -> {
                        Text("Loading Algorithm...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Explanation Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = uiState.currentStep?.explanation ?: "Loading...",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            // Pseudo-Code Sync View
            val pseudoCode = uiState.algorithmInfo?.pseudoCode
            if (!pseudoCode.isNullOrEmpty()) {
                val lines = pseudoCode.lines()
                val activeLineIndex = uiState.currentStep?.activeLine?.minus(1) // 1-indexed to 0-indexed

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    val listState = rememberLazyListState()

                    LaunchedEffect(activeLineIndex) {
                        if (activeLineIndex != null && activeLineIndex in lines.indices) {
                            listState.animateScrollToItem(activeLineIndex)
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(12.dp)
                    ) {
                        itemsIndexed(lines) { index, line ->
                            val isActive = index == activeLineIndex
                            Text(
                                text = line,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = if (isActive) DeepNavy else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isActive) MintAccent else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Controls Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Playback Speed Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Speed:", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = uiState.playbackSpeed,
                        onValueChange = { viewModel.setPlaybackSpeed(it) },
                        valueRange = 0.25f..4.0f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MintAccent,
                            activeTrackColor = MintAccent
                        )
                    )
                    Text(
                        text = "${String.format("%.2f", uiState.playbackSpeed)}x",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                // Media Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.stepBackward() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Step Backward", modifier = Modifier.size(32.dp))
                    }

                    FloatingActionButton(
                        onClick = { viewModel.togglePlayPause() },
                        containerColor = MintAccent,
                        contentColor = DeepNavy,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.stepForward() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Step Forward", modifier = Modifier.size(32.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Timeline Scrubber
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Step", style = MaterialTheme.typography.labelSmall)
                    
                    val maxSteps = maxOf(uiState.steps.size - 1, 0).toFloat()
                    Slider(
                        value = uiState.currentStepIndex.toFloat(),
                        onValueChange = { viewModel.scrubTo(it.toInt()) },
                        valueRange = 0f..maxSteps,
                        steps = maxOf(uiState.steps.size - 2, 0),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = OrangeAccent,
                            activeTrackColor = OrangeAccent
                        )
                    )
                    
                    Text(
                        text = "${uiState.currentStepIndex + 1}/${maxOf(uiState.steps.size, 1)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }

    if (showInfoSheet && uiState.algorithmInfo != null) {
        val info = uiState.algorithmInfo!!
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = info.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MintAccent
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = info.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        color = DeepNavy.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Time", style = MaterialTheme.typography.labelSmall, color = MintAccent)
                            Text(text = info.timeComplexity, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }
                    Surface(
                        color = DeepNavy.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Space", style = MaterialTheme.typography.labelSmall, color = OrangeAccent)
                            Text(text = info.spaceComplexity, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Algorithm Steps", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                info.steps.forEachIndexed { index, step ->
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MintAccent,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(text = step, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Use Cases", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = info.useCases, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
