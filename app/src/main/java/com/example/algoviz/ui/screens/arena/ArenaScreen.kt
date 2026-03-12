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
import androidx.compose.material.icons.filled.Close
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
import com.example.algoviz.domain.engine.ArenaDataProvider
import com.example.algoviz.domain.engine.ArenaProblem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaScreen(
    onNavigateToProblem: (String) -> Unit = {},
) {
    val difficulties = listOf("All", "Easy", "Medium", "Hard")
    var selectedDifficulty by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val problems = remember { ArenaDataProvider.problems }

    val filteredProblems = problems.filter { problem ->
        val matchesDifficulty = selectedDifficulty == "All" || problem.difficulty == selectedDifficulty
        val matchesSearch = searchQuery.isBlank() || 
            problem.title.contains(searchQuery, ignoreCase = true) || 
            problem.topics.any { it.contains(searchQuery, ignoreCase = true) }
        
        matchesDifficulty && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = {
                if (isSearchActive) {
                    androidx.compose.material3.TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search problems...") },
                        singleLine = true,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Practice Arena",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            actions = {
                if (isSearchActive) {
                    IconButton(onClick = { 
                        isSearchActive = false
                        searchQuery = ""
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Search")
                    }
                } else {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = "Leaderboard")
                    }
                    IconButton(onClick = { isSearchActive = true }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
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
                ProblemCard(problem = problem, onClick = { onNavigateToProblem(problem.id) })
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ProblemCard(
    problem: ArenaProblem,
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
            }
        }
    }
}
