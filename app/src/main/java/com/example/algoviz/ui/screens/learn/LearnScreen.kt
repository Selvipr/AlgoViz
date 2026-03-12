package com.example.algoviz.ui.screens.learn

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.algoviz.ui.theme.InfoBlue
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent

data class AlgorithmCategory(
    val name: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val algorithms: List<String> // List of topicIds (e.g. "bubble_sort", "bfs")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onNavigateToTopic: (String) -> Unit = {},
) {
    val categories = remember {
        listOf(
            AlgorithmCategory(
                name = "Sorting Algorithms", 
                icon = Icons.Filled.Analytics, 
                color = OrangeAccent,
                algorithms = listOf("bubble_sort", "merge_sort", "quick_sort", "heap_sort")
            ),
            AlgorithmCategory(
                name = "Searching Algorithms", 
                icon = Icons.Filled.Search, 
                color = MintAccent,
                algorithms = listOf("binary_search", "linear_search")
            ),
            AlgorithmCategory(
                name = "Graph Algorithms", 
                icon = Icons.Filled.Hub, 
                color = InfoBlue,
                algorithms = listOf("bfs", "dfs", "dijkstra")
            ),
            AlgorithmCategory(
                name = "Tree Algorithms", 
                icon = Icons.Filled.AccountTree, 
                color = OrangeAccent,
                algorithms = listOf("bst_insert")
            )
        )
    }

    val tracks = listOf("All", "Beginner", "Intermediate", "Advanced")
    var selectedTrack by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredCategories = categories.mapNotNull { category ->
        val filteredAlgos = category.algorithms.filter { algoId ->
            val info = com.example.algoviz.domain.engine.AlgorithmDataProvider.algorithmInfoMap[algoId]
            info != null && (searchQuery.isBlank() || 
                info.title.contains(searchQuery, ignoreCase = true) || 
                category.name.contains(searchQuery, ignoreCase = true))
        }
        if (filteredAlgos.isEmpty()) null else category.copy(algorithms = filteredAlgos)
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
                        placeholder = { Text("Search topics...") },
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
                        text = "Learn DSA",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Track filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(tracks) { track ->
                        FilterChip(
                            selected = selectedTrack == track,
                            onClick = { selectedTrack = track },
                            label = {
                                Text(
                                    text = track,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MintAccent.copy(alpha = 0.2f),
                                selectedLabelColor = MintAccent,
                            ),
                        )
                    }
                }
            }

            // Topic Categories
            item {
                Text(
                    text = "Topic Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(filteredCategories) { category ->
                CategoryBlock(category = category, onNavigateToTopic = onNavigateToTopic)
            }

            // Bottom spacer
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CategoryBlock(
    category: AlgorithmCategory,
    onNavigateToTopic: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        // Category Header
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.color,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Algorithm Cards
        category.algorithms.forEach { algoId ->
            val info = com.example.algoviz.domain.engine.AlgorithmDataProvider.algorithmInfoMap[algoId]
            if (info != null) {
                AlgorithmCard(
                    title = info.title,
                    description = info.timeComplexity,
                    onClick = { onNavigateToTopic(algoId) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AlgorithmCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Time: $description",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Learn",
                tint = MintAccent
            )
        }
    }
}
