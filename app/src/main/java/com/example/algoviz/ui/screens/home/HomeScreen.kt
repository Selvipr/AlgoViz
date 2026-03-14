package com.example.algoviz.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algoviz.domain.model.User
import com.example.algoviz.ui.theme.DeepNavy
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent
import com.example.algoviz.ui.theme.StreakFlame
import com.example.algoviz.ui.theme.XPGold

@Composable
fun HomeScreen(
    onNavigateToTopic: (String) -> Unit = {},
    onNavigateToArena: () -> Unit = {},
    onNavigateToCompare: () -> Unit = {},
    onNavigateToAssistant: () -> Unit = {},
    onNavigateToProblem: (String) -> Unit = {},
    onNavigateToVisualize: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                androidx.compose.material3.CircularProgressIndicator(
                    color = MintAccent,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Button(
                        onClick = { viewModel.loadHomeData() },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MintAccent)
                    ) {
                        Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            is HomeUiState.Success -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomeContent(
                        user = state.user,
                        onNavigateToTopic = onNavigateToTopic,
                        onNavigateToArena = onNavigateToArena,
                        onNavigateToCompare = onNavigateToCompare,
                        onNavigateToProblem = onNavigateToProblem,
                        onNavigateToVisualize = onNavigateToVisualize
                    )
                    
                    // Chatbot Floating entry point
                    androidx.compose.material3.FloatingActionButton(
                        onClick = onNavigateToAssistant,
                        containerColor = MintAccent,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 90.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.AutoAwesome,
                            contentDescription = "Talk to Sarvam AI",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    user: User,
    onNavigateToTopic: (String) -> Unit,
    onNavigateToArena: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToProblem: (String) -> Unit,
    onNavigateToVisualize: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Greeting Header
        item {
            GreetingSection(user.fullName ?: user.username.ifBlank { "Solver" })
        }

        // Streak & XP Stats
        item {
            StatsRow(
                streak = user.streak,
                xp = user.xp,
                tier = user.tier.replaceFirstChar { it.uppercase() }
            )
        }

        // Daily Challenge Card
        item {
            DailyChallengeCard(
                onClick = { onNavigateToProblem("two_sum") }
            )
        }

        // Quick Actions
        item {
            QuickActionsSection(
                onResumeLesson = { onNavigateToTopic("searching") },
                onTodayContest = onNavigateToArena,
                onCompare = onNavigateToCompare,
                onRandomProblem = {
                    val randomProblemId = listOf("binary_search", "bubble_sort", "valid_parentheses", "single_number").random()
                    onNavigateToProblem(randomProblemId)
                }
            )
        }

        // Recommended Topics
        item {
            RecommendedSection(
                onTopicClick = onNavigateToVisualize
            )
        }

        // Bottom spacer for nav bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun GreetingSection(name: String) {
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greeting = when (currentHour) {
        in 0..11 -> "Good morning 🌅"
        in 12..16 -> "Good afternoon ☀️"
        in 17..20 -> "Good evening 🌆"
        else -> "Good night 🌙"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ready to learn, $name?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun StatsRow(streak: Int, xp: Int, tier: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.LocalFireDepartment,
            value = streak.toString(),
            label = "Day Streak",
            iconColor = StreakFlame,
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.TrendingUp,
            value = xp.toString(),
            label = "Total XP",
            iconColor = XPGold,
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.EmojiEvents,
            value = tier,
            label = "Tier",
            iconColor = MintAccent,
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    iconColor: androidx.compose.ui.graphics.Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DailyChallengeCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            DeepNavy,
                            DeepNavy.copy(alpha = 0.8f),
                        )
                    )
                )
                .padding(20.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "🎯",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Challenge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MintAccent,
                        )
                        Text(
                            text = "Solve today's problem for bonus XP!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MintAccent.copy(alpha = 0.7f),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Two Sum",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Chip(text = "Easy", color = MintAccent)
                    Chip(text = "Array", color = OrangeAccent)
                    Chip(text = "+25 XP", color = XPGold)
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}

@Composable
private fun QuickActionsSection(
    onResumeLesson: () -> Unit,
    onTodayContest: () -> Unit,
    onCompare: () -> Unit,
    onRandomProblem: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.PlayArrow,
                title = "Resume\nLesson",
                color = MintAccent,
                onClick = onResumeLesson
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.EmojiEvents,
                title = "Today's\nContest",
                color = OrangeAccent,
                onClick = onTodayContest
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.CompareArrows,
                title = "Compare\nAlgos",
                color = androidx.compose.ui.graphics.Color(0xFF8A2BE2),
                onClick = onCompare
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Shuffle,
                title = "Random\nProblem",
                color = MaterialTheme.colorScheme.tertiary,
                onClick = onRandomProblem
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun RecommendedSection(onTopicClick: (String) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recommended for You",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(5) { index ->
                val topics = listOf(
                    Triple("binary_search", "Binary Search", "Searching"),
                    Triple("merge_sort", "Merge Sort", "Sorting"),
                    Triple("bfs", "Graph BFS", "Graph"),
                    Triple("linear_search", "Linear Search", "Searching"),
                    Triple("quick_sort", "Quick Sort", "Sorting"),
                )
                val (id, name, category) = topics[index]
                TopicCard(
                    name = name, 
                    category = category, 
                    progress = 0f,
                    onClick = { onTopicClick(id) }
                )
            }
        }
    }
}

@Composable
private fun TopicCard(
    name: String,
    category: String,
    progress: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(160.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall,
                color = MintAccent,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MintAccent,
                trackColor = MintAccent.copy(alpha = 0.15f),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}% complete",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
