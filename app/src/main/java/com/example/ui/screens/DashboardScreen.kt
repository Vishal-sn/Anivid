package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.entity.VideoProject
import com.example.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Beautiful multi-color gradient corresponding to the startup mockup's visual language
val LogoGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFF7B30), // Orange/peach
        Color(0xFFFF4E7B), // Warm pink
        Color(0xFFB37BFF), // Purple
        Color(0xFF5FF3FF)  // Glowing cyan
    )
)

@Composable
fun SparkleLogo(
    modifier: Modifier = Modifier,
    brush: Brush = LogoGradient
) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            
            moveTo(centerX, 0f)
            quadraticTo(centerX, centerY, size.width, centerY)
            quadraticTo(centerX, centerY, centerX, size.height)
            quadraticTo(centerX, centerY, 0f, centerY)
            quadraticTo(centerX, centerY, centerX, 0f)
            close()
        }
        drawPath(path = path, brush = brush)
    }
}

@Composable
fun DashboardScreen(
    projects: List<VideoProject>,
    onProjectSelect: (VideoProject) -> Unit,
    onDeleteProject: (Int) -> Unit,
    onStartNewProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Explore/Feed, 1: Profile oliver_bennet
    var showNotificationSnack by remember { mutableStateOf(false) }
    var snackerText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = SlateDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp) // customized bottom bar is inset-aware itself
        ) {
            // Screen contents based on selected tab
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "dashboard_tabs"
            ) { tab ->
                when (tab) {
                    0 -> ExploreTabContent(
                        projects = projects,
                        onProjectSelect = onProjectSelect,
                        onDeleteProject = onDeleteProject,
                        onStartNewProject = onStartNewProject,
                        onNotify = { msg ->
                            snackerText = msg
                            showNotificationSnack = true
                        }
                    )
                    1 -> ProfileTabContent(
                        onNotify = { msg ->
                            snackerText = msg
                            showNotificationSnack = true
                        }
                    )
                }
            }

            // Glassmorphic Custom Floating Bottom Navigation Bar (Mockup Style)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .navigationBarsPadding() // Respect device gestural pill/system nav height
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color.Black.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Menu Icon
                    IconButton(
                        onClick = {
                            snackerText = "Menu settings and configurations customized successfully."
                            showNotificationSnack = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Main menu",
                            tint = if (activeTab == 0) TextPrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Home (Feed) icon with custom visual indicator matching mockup selection
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { activeTab = 0 }
                    ) {
                        Icon(
                            imageVector = if (activeTab == 0) Icons.Default.Home else Icons.Default.Home,
                            contentDescription = "Explore",
                            tint = if (activeTab == 0) NeonCyan else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Center Primary: Colorful Glowing Four-pointed Star / Sparkle FAB Button (Triggers Creation flow)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(16.dp, CircleShape, spotColor = NeonCyan)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .clickable { onStartNewProject() }
                            .testTag("start_new_project_fab")
                    ) {
                        SparkleLogo(
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }

                    // Search Pill/Icon
                    IconButton(
                        onClick = {
                            snackerText = "Search for outstanding motion creators or models."
                            showNotificationSnack = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search content",
                            tint = TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Notification bell / Profile Tab Icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { activeTab = 1 }
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&q=80",
                            contentDescription = "Profile Page",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (activeTab == 1) 2.dp else 1.dp,
                                    color = if (activeTab == 1) NeonPurple else BorderColor,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            // High aesthetic HUD notifications toast
            if (showNotificationSnack) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SparkleLogo(modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = snackerText,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Dismiss",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showNotificationSnack = false }
                            )
                        }
                    }
                }
                
                // Auto dismiss helper
                LaunchedEffect(snackerText) {
                    kotlinx.coroutines.delay(3500)
                    showNotificationSnack = false
                }
            }
        }
    }
}

// ==========================================
// 1. EXPLORE CHANNEL TAB CONTENT (Left Screen)
// ==========================================
@Composable
fun ExploreTabContent(
    projects: List<VideoProject>,
    onProjectSelect: (VideoProject) -> Unit,
    onDeleteProject: (Int) -> Unit,
    onStartNewProject: () -> Unit,
    onNotify: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Sample Creators (Circular Avatars with online green bubble markers)
    val creators = listOf(
        Pair("Marina", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=120&q=80"),
        Pair("Vince", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=120&q=80"),
        Pair("Katia", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80"),
        Pair("Oliver", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80"),
        Pair("Evelyn", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80"),
        Pair("Dario", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=120&q=80")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 120.dp) // Provide spacing so content doesn't get hidden behind bottom bar
    ) {
        // Aesthetic Top Header (Matching mockup Adverse/Anigen layout)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SparkleLogo(
                    modifier = Modifier
                        .size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Anigen",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bell Notifier
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .clickable { onNotify("Checked in. No new system notifications available.") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Professional User Head
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&q=80",
                    contentDescription = "Your account profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onNotify("Signed in as Elena Juni.") }
                )
            }
        }

        // Section 1: Co-Creators Horizontal Strip (Bubble rings with green notification indicators)
        Text(
            text = "Your Creators",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(creators) { (name, url) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onNotify("Browsing animation streams from $name.") }
                ) {
                    Box(modifier = Modifier.size(60.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(1.dp, BorderColor, CircleShape)
                        )
                        // Online Green Dot Status Marker (Mockup spec)
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (-2).dp, y = (-2).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CD964))
                                .border(1.5.dp, SlateDark, CircleShape)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Casino's/Creations on Live (Stunning cards with profile overlays and huge bold titles)
        Text(
            text = "Creators on Live",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                LiveCardItem(
                    title = "WINDS OF DESTINY",
                    creatorName = "Marina",
                    creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&q=80",
                    coverUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=400&q=80",
                    viewCount = "86.54K",
                    onFollowClick = { onNotify("Followed Marina!") },
                    onCardClick = { onNotify("Interacting with 'WINDS OF DESTINY' live showcase.") }
                )
            }
            item {
                LiveCardItem(
                    title = "COSMIC TURBULENCE",
                    creatorName = "Oliver B.",
                    creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&q=80",
                    coverUrl = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&w=400&q=80",
                    viewCount = "112.9K",
                    onFollowClick = { onNotify("Followed Oliver B.!") },
                    onCardClick = { onNotify("Interacting with 'COSMIC TURBULENCE' live showcase.") }
                )
            }
            item {
                LiveCardItem(
                    title = "LIQUID DIMENSION",
                    creatorName = "Katia",
                    creatorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&q=80",
                    coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=400&q=80",
                    viewCount = "45.10K",
                    onFollowClick = { onNotify("Followed Katia!") },
                    onCardClick = { onNotify("Interacting with 'LIQUID DIMENSION' live showcase.") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Studio Saved Creation History (Displays actual database records elegantly)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your AI Masterpieces",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "${projects.size} Animations",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (projects.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "No local projects",
                        tint = TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Static Images Await Movement",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your creative generations history is completely secure. Rendered models save here instantly.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onStartNewProject,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = IndigoAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Animate Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Lazy row of real projects loaded dynamically
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(projects, key = { it.id }) { project ->
                    var showDeleteConfirmation by remember { mutableStateOf(false) }

                    if (showDeleteConfirmation) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirmation = false },
                            title = { Text("Delete Masterpiece?", color = TextPrimary) },
                            text = { Text("Are you specific you wish to delete '${project.title}' permanently?", color = TextSecondary) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onDeleteProject(project.id)
                                        showDeleteConfirmation = false
                                        onNotify("Project '${project.title}' deleted successfully.")
                                    }
                                ) {
                                    Text("Delete", color = LiveRed)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirmation = false }) {
                                    Text("Keep", color = TextPrimary)
                                }
                            },
                            containerColor = SlateSurface
                        )
                    }

                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .height(180.dp)
                            .testTag("project_item_${project.id}")
                            .clickable { onProjectSelect(project) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (project.imageUriString != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(project.imageUriString)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = project.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(SlateSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = TextSecondary
                                    )
                                }
                            }

                            // Linear darker gradient overlay at bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f)),
                                            startY = 50f
                                        )
                                    )
                            )

                            // Play Button overlaid
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Permanent trash toggle overlay
                            IconButton(
                                onClick = { showDeleteConfirmation = true },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete project",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }

                            // Card caption metadata text
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = project.title,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${project.motionStyle} • ${project.durationSeconds}s",
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom Glassmorphic featured component built precisely matching mockup Left Frame
@Composable
fun LiveCardItem(
    title: String,
    creatorName: String,
    creatorAvatar: String,
    coverUrl: String,
    viewCount: String,
    onFollowClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(300.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // High fidelity cover artwork from Unsplash
            AsyncImage(
                model = coverUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic bottom dark shadow backdrop to keep text legible
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 120f
                        )
                    )
            )

            // Top Header Overlay inside the card: glassmorphic status row (Creator Head + Follow Pill)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = creatorAvatar,
                        contentDescription = creatorName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = creatorName,
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Live Stream",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            maxLines = 1
                        )
                    }
                }
                
                // Follow pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { onFollowClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Follow",
                        color = TextPrimary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Big elegant uppercase Display Typography overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .padding(bottom = 36.dp) // space for LIVE footer row
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    lineHeight = 22.sp
                )
            }

            // Bottom Footer Row overlay matching mockup
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Badge pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(LiveRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                // Viewcount meta tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = viewCount,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Small "+15" indicator overlay
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+15",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ==========================================
// 2. PROFILE TAB CONTENT (Right Screen Mockup)
// ==========================================
@Composable
fun ProfileTabContent(
    onNotify: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    // Secondary gallery creations for Oliver Bennet matching his profile mockup strip
    val galleryCreations = listOf(
        Pair("Forest Reflection", "https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?auto=format&fit=crop&w=200&q=80"),
        Pair("Water Ripples", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=200&q=80"),
        Pair("Futuristic Neon", "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=200&q=80"),
        Pair("Nebula Gas", "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&w=200&q=80")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 120.dp) // padding so bottom custom nav is completely visible
    ) {
        // Top Header Row for Profile: Back Arrow icon + @oliver_bennet + Edit Profile pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Circular Back icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(0.5.dp, BorderColor, CircleShape)
                    .clickable { onNotify("Navigating back to main channel.") }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back icon",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Handle title
            Text(
                text = "@oliver_bennet",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // Edit Profile Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(0.5.dp, BorderColor, RoundedCornerShape(12.dp))
                    .clickable { onNotify("Oliver Bennet profile editor modal activated.") }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Edit Profile",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Frosted / Dark glass card outlining main profile info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Name & Social Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Oliver\nBennet",
                            color = TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 36.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "@oliver_bennent",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Circle buttons in mockup: star icon with colorful aura + message circle icon
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sparkle Aura Action
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                                .clickable { onNotify("Sparking Oliver's verified content space!") }
                        ) {
                            SparkleLogo(modifier = Modifier.size(22.dp))
                        }

                        // Message Action
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                .clickable { onNotify("Initiating message thread with Oliver Bennet.") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mail,
                                contentDescription = "Mail",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bio Description matching text of mockup
                Text(
                    text = "Designer focused on creating impactful, user-centered digital motion experiences and branding.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Row: Followers, Following, Creations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileStatItem(number = "521", label = "Followers")
                    ProfileStatItem(number = "345", label = "Following")
                    ProfileStatItem(number = "566", label = "Creations")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Scrollable pill tags in the profile: bookreader, foodie, traveler, hiker, designer
                val profileTags = listOf("bookreader", "foodie", "traveler", "hiker", "designer")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(profileTags) { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(0.5.dp, BorderColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "@$tag",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal visual showcase strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showcase Gallery",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(galleryCreations) { (name, url) ->
                Card(
                    modifier = Modifier
                        .width(96.dp)
                        .height(96.dp)
                        .clickable { onNotify("Opening high-definition preview for '$name'.") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant)
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Review/Testimonial Card matching bottom of mockup screen 2
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Author's circular thumbnail
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&q=80",
                    contentDescription = "Elena Juni",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Thanks for the great recipe recommendation for this greek salad. Had so much fun making it with my family. Greetings from USA!",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Elena Juni",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@elena.juni",
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(number: String, label: String) {
    Column {
        Text(
            text = number,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
