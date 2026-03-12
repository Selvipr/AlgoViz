package com.example.algoviz.ui.screens.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.algoviz.domain.engine.AlgorithmDataProvider
import com.example.algoviz.ui.theme.DeepNavy
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent
import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnDetailScreen(
    topicId: String,
    onNavigateBack: () -> Unit,
    onNavigateToVisualize: (String) -> Unit
) {
    val algorithmInfo = remember(topicId) { AlgorithmDataProvider.algorithmInfoMap[topicId] }
    val lifecycleOwner = LocalLifecycleOwner.current

    if (algorithmInfo == null) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Text("Topic not found", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = algorithmInfo.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToVisualize(topicId) },
                containerColor = MintAccent,
                contentColor = DeepNavy,
                icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Visualize") },
                text = { Text("Open Visualizer", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // YouTube Player
            val context = androidx.compose.ui.platform.LocalContext.current
            val webView = remember {
                WebView(context).apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    webViewClient = android.webkit.WebViewClient()
                    webChromeClient = WebChromeClient()
                    
                    val html = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <style>
                                body { margin: 0; padding: 0; background-color: #000000; }
                                iframe { width: 100%; height: 100vh; border: none; }
                            </style>
                        </head>
                        <body>
                            <iframe src="https://www.youtube.com/embed/${algorithmInfo.youtubeVideoId}?autoplay=0&rel=0&modestbranding=1" 
                                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                                    allowfullscreen>
                            </iframe>
                        </body>
                        </html>
                    """.trimIndent()
                    
                    loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
                }
            }

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_PAUSE -> webView.onPause()
                        Lifecycle.Event.ON_RESUME -> webView.onResume()
                        Lifecycle.Event.ON_DESTROY -> webView.destroy()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    // The WebView is destroyed when the screen is fully disposed
                }
            }

            AndroidView(
                factory = { webView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            // Content Body
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = algorithmInfo.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Complexities
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ComplexityCard(
                        title = "Time Complexity",
                        value = algorithmInfo.timeComplexity,
                        color = MintAccent,
                        modifier = Modifier.weight(1f)
                    )
                    ComplexityCard(
                        title = "Space Complexity",
                        value = algorithmInfo.spaceComplexity,
                        color = OrangeAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "How It Works",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                algorithmInfo.steps.forEachIndexed { index, step ->
                    Row(modifier = Modifier.padding(bottom = 12.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DeepNavy,
                            modifier = Modifier.padding(end = 12.dp).size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    color = MintAccent,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Use Cases",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = algorithmInfo.useCases,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
            }
        }
    }
}

@Composable
private fun ComplexityCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
