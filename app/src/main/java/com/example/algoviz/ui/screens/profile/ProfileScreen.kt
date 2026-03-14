package com.example.algoviz.ui.screens.profile

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent
import com.example.algoviz.ui.theme.StreakFlame
import com.example.algoviz.ui.theme.TierNovice
import com.example.algoviz.ui.theme.XPGold
import com.example.algoviz.ui.theme.XPGold
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algoviz.domain.model.User
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.clickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import com.example.algoviz.utils.LocationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isEditing by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            },
            actions = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    androidx.compose.material3.CircularProgressIndicator(color = MintAccent)
                }
                is ProfileUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.material3.Button(
                            onClick = { viewModel.loadUserProfile() },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MintAccent)
                        ) {
                            Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
                is ProfileUiState.Success -> {
                    val user = state.user
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Avatar + Info
                        item {
                            ProfileHeader(user)
                        }

                        // Stats Cards
                        item {
                            ProfileStats(user)
                        }

                        // XP Progress
                        item {
                            XPProgressCard(user)
                        }

                        // Achievements
                        item {
                            AchievementsSection()
                        }

                        // Extended Profile Details
                        item {
                            ProfileDetailsSection(
                                user = user,
                                isEditing = isEditing,
                                onEditModeChanged = { isEditing = it },
                                onSaveProfile = { updatedUser ->
                                    viewModel.updateProfile(updatedUser)
                                    isEditing = false
                                }
                            )
                        }

                        // Logout Button
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.OutlinedButton(
                                onClick = { 
                                    viewModel.signOut() 
                                    onLogoutSuccess()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                )
                            ) {
                                Icon(
                                    androidx.compose.material.icons.Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Log Out",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: User) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MintAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Avatar",
                    tint = MintAccent,
                    modifier = Modifier.size(40.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = user.fullName ?: user.username.ifBlank { "AlgoViz User" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val infiniteTransition = rememberInfiniteTransition(label = "badge_anim")
            val badgeScale by infiniteTransition.animateFloat(
                initialValue = 0.97f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "badge_scale"
            )

            Box(
                modifier = Modifier
                    .scale(badgeScale)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TierNovice.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "⭐ ${user.tier.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TierNovice,
                )
            }
        }
    }
}

@Composable
private fun ProfileStats(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProfileStatCard(
            modifier = Modifier.weight(1f),
            value = (user.xp / 20).toString(),
            label = "Solved",
            icon = Icons.Filled.Star,
            color = MintAccent,
        )
        ProfileStatCard(
            modifier = Modifier.weight(1f),
            value = user.streak.toString(),
            label = "Streak",
            icon = Icons.Filled.LocalFireDepartment,
            color = StreakFlame,
        )
        ProfileStatCard(
            modifier = Modifier.weight(1f),
            value = "#-", // TODO: Compute rank dynamically later
            label = "Rank",
            icon = Icons.Filled.EmojiEvents,
            color = XPGold,
        )
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
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
private fun XPProgressCard(user: User) {
    // Basic calculation for next tier requirement (can be moved to Utils or ViewModel later)
    val nextTierXp = when (user.tier.lowercase()) {
        "novice" -> 500
        "learner" -> 1500
        "practitioner" -> 5000
        "expert" -> 15000
        "master" -> 50000
        else -> 100000 // Grandmaster +
    }
    
    val nextTierName = when (user.tier.lowercase()) {
        "novice" -> "Learner"
        "learner" -> "Practitioner"
        "practitioner" -> "Expert"
        "expert" -> "Master"
        "master" -> "Grandmaster"
        else -> "Max Level"
    }

    val progressVal = if (user.xp >= nextTierXp) 1f else (user.xp.toFloat() / nextTierXp.toFloat())

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Level Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${user.xp} / $nextTierXp XP",
                    style = MaterialTheme.typography.labelMedium,
                    color = MintAccent,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progressVal },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MintAccent,
                trackColor = MintAccent.copy(alpha = 0.12f),
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            val xpNeeded = nextTierXp - user.xp
            if (xpNeeded > 0) {
                Text(
                    text = "$xpNeeded XP to reach $nextTierName tier",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "Ready to rank up!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MintAccent,
                )
            }
        }
    }
}

@Composable
private fun AchievementsSection() {
    Column {
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val badges = listOf(
                "🎯" to "First Steps",
                "🔥" to "7-Day Streak",
                "💯" to "Century Club",
                "⚡" to "Speed Demon",
                "🌍" to "Polyglot",
            )
            items(badges.size) { index ->
                val (emoji, name) = badges[index]
                AchievementBadge(emoji = emoji, name = name, locked = true)
            }
        }
    }
}

@Composable
private fun AchievementBadge(
    emoji: String,
    name: String,
    locked: Boolean,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (locked)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .width(90.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = if (locked) Modifier else Modifier,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (locked)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ProfileDetailsSection(
    user: User,
    isEditing: Boolean,
    onEditModeChanged: (Boolean) -> Unit,
    onSaveProfile: (User) -> Unit
) {
    var fullName by remember(user) { mutableStateOf(user.fullName ?: "") }
    var bio by remember(user) { mutableStateOf(user.bio ?: "") }
    var college by remember(user) { mutableStateOf(user.college ?: "") }
    var location by remember(user) { mutableStateOf(user.location ?: "") }
    var githubUrl by remember(user) { mutableStateOf(user.githubUrl ?: "") }
    var linkedinUrl by remember(user) { mutableStateOf(user.linkedinUrl ?: "") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoadingLocation by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isLoadingLocation = true
            coroutineScope.launch {
                val result = LocationHelper.getCurrentLocationAsText(context)
                result.onSuccess { loc ->
                    location = loc
                }
                isLoadingLocation = false
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "About Me",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (!isEditing) {
                    IconButton(onClick = { onEditModeChanged(true) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = MintAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintAccent,
                        focusedLabelColor = MintAccent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintAccent,
                        focusedLabelColor = MintAccent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / University") },
                    leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintAccent,
                        focusedLabelColor = MintAccent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintAccent,
                            focusedLabelColor = MintAccent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    ) {
                        if (isLoadingLocation) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MintAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.LocationOn, contentDescription = "Get GPS Location", tint = MintAccent)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = githubUrl,
                    onValueChange = { githubUrl = it },
                    label = { Text("GitHub URL") },
                    leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintAccent,
                        focusedLabelColor = MintAccent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = linkedinUrl,
                    onValueChange = { linkedinUrl = it },
                    label = { Text("LinkedIn URL") },
                    leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintAccent,
                        focusedLabelColor = MintAccent
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { 
                        // Reset fields by re-initializing from user object
                        fullName = user.fullName ?: ""
                        bio = user.bio ?: ""
                        college = user.college ?: ""
                        location = user.location ?: ""
                        githubUrl = user.githubUrl ?: ""
                        linkedinUrl = user.linkedinUrl ?: ""
                        onEditModeChanged(false) 
                    }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updated = user.copy(
                                fullName = fullName,
                                bio = bio,
                                college = college,
                                location = location,
                                githubUrl = githubUrl,
                                linkedinUrl = linkedinUrl
                            )
                            onSaveProfile(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Details", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // VIEW MODE
                val uriHandler = LocalUriHandler.current

                if (!user.bio.isNullOrBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (!user.college.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.Filled.Business, contentDescription = "College", tint = MintAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.college, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                if (!user.location.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                        Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = MintAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.location, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                if (!user.githubUrl.isNullOrBlank() || !user.linkedinUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (!user.githubUrl.isNullOrBlank()) {
                            Text(
                                text = "GitHub",
                                color = MintAccent,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    try { uriHandler.openUri(user.githubUrl) } catch (e: Exception) {}
                                }.padding(vertical = 4.dp)
                            )
                        }
                        if (!user.linkedinUrl.isNullOrBlank()) {
                            Text(
                                text = "LinkedIn",
                                color = MintAccent,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    try { uriHandler.openUri(user.linkedinUrl) } catch (e: Exception) {}
                                }.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
                
                // If completely empty, show placeholder
                if (user.bio.isNullOrBlank() && user.college.isNullOrBlank() && user.location.isNullOrBlank() 
                    && user.githubUrl.isNullOrBlank() && user.linkedinUrl.isNullOrBlank()) {
                    Text(
                        text = "Your profile is looking a bit empty! Click Edit Profile to add college, location and socials.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
