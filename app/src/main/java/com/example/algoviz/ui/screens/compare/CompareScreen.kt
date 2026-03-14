package com.example.algoviz.ui.screens.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algoviz.domain.model.AlgorithmStats
import com.example.algoviz.ui.theme.MintAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    onNavigateBack: () -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val leftAlgo by viewModel.leftAlgorithm.collectAsStateWithLifecycle()
    val rightAlgo by viewModel.rightAlgorithm.collectAsStateWithLifecycle()
    val allAlgos = viewModel.availableAlgorithms

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Compare Algorithms", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Dropdown Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AlgorithmDropdown(
                        selectedOptionText = leftAlgo.name,
                        options = allAlgos,
                        onSelectionChange = { viewModel.setLeftAlgorithm(it) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    AlgorithmDropdown(
                        selectedOptionText = rightAlgo.name,
                        options = allAlgos,
                        onSelectionChange = { viewModel.setRightAlgorithm(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // The vs Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = leftAlgo.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MintAccent,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = rightAlgo.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA500),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stat Table Rows
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    CompareRow("Best Time", leftAlgo.bestTime, rightAlgo.bestTime)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 1.dp)
                    CompareRow("Average Time", leftAlgo.averageTime, rightAlgo.averageTime)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 1.dp)
                    CompareRow("Worst Time", leftAlgo.worstTime, rightAlgo.worstTime)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 1.dp)
                    CompareRow("Space Complexity", leftAlgo.spaceComplexity, rightAlgo.spaceComplexity)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 1.dp)
                    CompareRow("Stable Sort?", if (leftAlgo.isStable) "Yes" else "No", if (rightAlgo.isStable) "Yes" else "No")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Context Verdict Engine
            VerdictBox(leftAlgo, rightAlgo)
        }
    }
}

@Composable
fun CompareRow(label: String, leftVal: String, rightVal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leftVal,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = rightVal,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmDropdown(
    selectedOptionText: String,
    options: List<AlgorithmStats>,
    onSelectionChange: (AlgorithmStats) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text("Algorithm") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption.name) },
                    onClick = {
                        onSelectionChange(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun VerdictBox(left: AlgorithmStats, right: AlgorithmStats) {
    val verdictText = if (left.id == right.id) {
        "These are the exact same algorithms."
    } else if (left.averageTime == right.averageTime && left.spaceComplexity == right.spaceComplexity) {
        "Both ${left.name} and ${right.name} perform remarkably similarly across the board under average constraints."
    } else if (left.averageTime.contains("n²") && !right.averageTime.contains("n²")) {
        "Verdict: ${right.name} scales exponentially better due to avoiding the heavy O(n²) ${left.name} penalty on large datasets."
    } else if (!left.averageTime.contains("n²") && right.averageTime.contains("n²")) {
        "Verdict: ${left.name} handles large datasets much faster than ${right.name} utilizing a superior sub-quadratic time constraint."
    } else {
        "Verdict: Different constraints serve different purposes. ${left.name} requires ${left.spaceComplexity} space, whilst ${right.name} requires ${right.spaceComplexity}."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MintAccent.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Algorithm Verdict",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MintAccent
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = verdictText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
