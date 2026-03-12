package com.example.algoviz.ui.screens.visualize

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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.algoviz.ui.theme.DeepNavy
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent

data class AlgorithmItem(
    val name: String,
    val category: String,
    val complexity: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizeScreen(
    onNavigateToVisualization: (String) -> Unit = {},
) {
    val categories = listOf("All", "Sorting", "Searching", "Graph", "Tree", "DP")
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val algorithms = remember {
        listOf(
            AlgorithmItem("Bubble Sort", "Sorting", "O(n²)"),
            AlgorithmItem("Merge Sort", "Sorting", "O(n log n)"),
            AlgorithmItem("Quick Sort", "Sorting", "O(n log n)"),
            AlgorithmItem("Binary Search", "Searching", "O(log n)"),
            AlgorithmItem("BFS", "Graph", "O(V + E)"),
            AlgorithmItem("DFS", "Graph", "O(V + E)"),
            AlgorithmItem("Dijkstra", "Graph", "O(V² / V log V)"),
            AlgorithmItem("BST Insert", "Tree", "O(log n)"),
            AlgorithmItem("Heap Sort", "Sorting", "O(n log n)"),
            AlgorithmItem("Linear Search", "Searching", "O(n)"),
        )
    }

    val filteredAlgorithms = algorithms.filter {
        val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() || 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.category.contains(searchQuery, ignoreCase = true)
        
        matchesCategory && matchesSearch
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
                        placeholder = { Text("Search visualizers...") },
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
                        text = "Visualize",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.CompareArrows, contentDescription = "Compare")
                }
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Featured banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavy),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(DeepNavy, MintAccent.copy(alpha = 0.3f))
                                )
                            )
                            .padding(20.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Animation,
                                contentDescription = null,
                                tint = MintAccent,
                                modifier = Modifier.size(40.dp),
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Interactive Visualizations",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MintAccent,
                                )
                                Text(
                                    "Watch algorithms come alive step by step",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MintAccent.copy(alpha = 0.7f),
                                )
                            }
                        }
                    }
                }
            }

            // Category filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MintAccent.copy(alpha = 0.2f),
                                selectedLabelColor = MintAccent,
                            ),
                        )
                    }
                }
            }

            items(filteredAlgorithms) { algo ->
                val vizId = algo.name.lowercase().replace(" ", "_")
                AlgorithmCard(algorithm = algo, onClick = { onNavigateToVisualization(vizId) })
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun AlgorithmCard(
    algorithm: AlgorithmItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(OrangeAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when (algorithm.category) {
                        "Sorting" -> Icons.Filled.Sort
                        "Searching" -> Icons.Filled.Search
                        "Graph" -> Icons.Filled.BubbleChart
                        else -> Icons.Filled.Animation
                    },
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = algorithm.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = algorithm.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MintAccent.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = algorithm.complexity,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MintAccent,
                )
            }
        }
    }
}
