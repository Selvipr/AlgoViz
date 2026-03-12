package com.example.algoviz.ui.screens.arena

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.algoviz.ui.theme.DifficultyEasy
import com.example.algoviz.ui.theme.DifficultyHard
import com.example.algoviz.ui.theme.DifficultyMedium
import com.example.algoviz.ui.theme.MintAccent

data class ProblemItem(
    val title: String,
    val difficulty: String,
    val topics: List<String>,
    val acceptanceRate: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaScreen(
    onNavigateToProblem: (String) -> Unit = {},
) {
    val difficulties = listOf("All", "Easy", "Medium", "Hard")
    var selectedDifficulty by remember { mutableStateOf("All") }

    val problems = remember {
        listOf(
            ProblemItem("Two Sum", "Easy", listOf("Array", "Hash Map"), "78%"),
            ProblemItem("Add Two Numbers", "Medium", listOf("Linked List"), "42%"),
            ProblemItem("Longest Substring", "Medium", listOf("String", "Sliding Window"), "35%"),
            ProblemItem("Median of Two Arrays", "Hard", listOf("Binary Search"), "38%"),
            ProblemItem("Valid Parentheses", "Easy", listOf("Stack"), "82%"),
            ProblemItem("Merge Intervals", "Medium", listOf("Array", "Sorting"), "48%"),
            ProblemItem("Binary Tree Inorder", "Easy", listOf("Tree", "Stack"), "75%"),
            ProblemItem("LRU Cache", "Medium", listOf("Hash Map", "Linked List"), "41%"),
            ProblemItem("Trapping Rain Water", "Hard", listOf("Stack", "Two Pointer"), "60%"),
            ProblemItem("Coin Change", "Medium", listOf("DP"), "43%"),
        )
    }

    val filteredProblems = if (selectedDifficulty == "All") problems
        else problems.filter { it.difficulty == selectedDifficulty }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Practice Arena",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Leaderboard")
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Difficulty filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(difficulties) { diff ->
                        FilterChip(
                            selected = selectedDifficulty == diff,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(diff, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (diff) {
                                    "Easy" -> DifficultyEasy.copy(alpha = 0.2f)
                                    "Medium" -> DifficultyMedium.copy(alpha = 0.2f)
                                    "Hard" -> DifficultyHard.copy(alpha = 0.2f)
                                    else -> MintAccent.copy(alpha = 0.2f)
                                },
                                selectedLabelColor = when (diff) {
                                    "Easy" -> DifficultyEasy
                                    "Medium" -> DifficultyMedium
                                    "Hard" -> DifficultyHard
                                    else -> MintAccent
                                },
                            ),
                        )
                    }
                }
            }

            // Problem count
            item {
                Text(
                    text = "${filteredProblems.size} Problems",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Problem list
            items(filteredProblems) { problem ->
                ProblemCard(problem = problem, onClick = { onNavigateToProblem("") })
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ProblemCard(
    problem: ProblemItem,
    onClick: () -> Unit,
) {
    val difficultyColor = when (problem.difficulty) {
        "Easy" -> DifficultyEasy
        "Medium" -> DifficultyMedium
        "Hard" -> DifficultyHard
        else -> MintAccent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = problem.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(difficultyColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = problem.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = difficultyColor,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                problem.topics.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${problem.acceptanceRate} acc.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
