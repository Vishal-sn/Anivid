package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.text.BasicTextField
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    selectedImageUri: Uri?,
    motionStyle: String,
    speedMultiplier: Float,
    overlayEffect: String,
    aspectRatio: String,
    durationSeconds: Int,
    projectTitle: String,
    isAnalyzing: Boolean,
    aiBlueprint: String?,
    analysisError: String?,
    isExporting: Boolean,
    exportProgress: Float,
    exportStatusText: String,
    currentExportedProject: VideoProject?,
    onImageSelected: (Uri?) -> Unit,
    onMotionStyleChanged: (String) -> Unit,
    onSpeedChanged: (Float) -> Unit,
    onOverlayEffectChanged: (String) -> Unit,
    onAspectRatioChanged: (String) -> Unit,
    onDurationChanged: (Int) -> Unit,
    onTitleChanged: (String) -> Unit,
    onTriggerAI: () -> Unit,
    onExportVideo: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onImageSelected(uri)
            }
        }
    )

    // Parse the AI Blueprint JSON if available
    val blueprintObj = remember(aiBlueprint) {
        if (!aiBlueprint.isNullOrEmpty()) {
            try {
                JSONObject(aiBlueprint)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Header
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicTextField(
                            value = projectTitle,
                            onValueChange = onTitleChanged,
                            textStyle = LocalTextStyle.current.copy(
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(NeonCyan),
                            modifier = Modifier
                                .widthIn(max = 200.dp)
                                .testTag("project_title_input")
                        )
                        Text(
                            text = "Aesthetic Compiler Active",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            photoLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(SlateSurfaceVariant)
                            .testTag("import_media_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Import photo",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            if (selectedImageUri == null) {
                // Initial state when no photo is loaded
                ImportMediaPlaceholder(
                    onPickMedia = {
                        photoLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    onSampleSelect = { uri ->
                        onImageSelected(uri)
                    }
                )
            } else {
                // Secondary columns for screen layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(bottom = 80.dp) // space for bottom sticky compile button
                ) {
                    // Renderer Preview Panel
                    PreviewPanel(
                        imageUri = selectedImageUri,
                        motionStyle = motionStyle,
                        speedMultiplier = speedMultiplier,
                        overlayEffect = overlayEffect,
                        aspectRatio = aspectRatio,
                        durationSeconds = durationSeconds
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Insights and Intelligence Panel
                    AIIntelligenceSection(
                        isAnalyzing = isAnalyzing,
                        blueprintObj = blueprintObj,
                        analysisError = analysisError,
                        onTriggerAI = onTriggerAI,
                        selectedImageUri = selectedImageUri
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Customization Toolbox (Tabs and options)
                    CustomizationControlsSection(
                        motionStyle = motionStyle,
                        speedMultiplier = speedMultiplier,
                        overlayEffect = overlayEffect,
                        aspectRatio = aspectRatio,
                        durationSeconds = durationSeconds,
                        onMotionStyleChanged = onMotionStyleChanged,
                        onSpeedChanged = onSpeedChanged,
                        onOverlayEffectChanged = onOverlayEffectChanged,
                        onAspectRatioChanged = onAspectRatioChanged,
                        onDurationChanged = onDurationChanged
                    )
                }
            }
        }

        // Stick Bottom Compile Action
        if (selectedImageUri != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, SlateDark.copy(alpha = 0.95f))
                        )
                    )
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onExportVideo,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoAccent,
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("compile_video_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Compile & Export AI Video",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // 1. Export Progress Screen overlay
        AnimatedVisibility(
            visible = isExporting,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ExportProgressOverlay(
                progress = exportProgress,
                statusText = exportStatusText
            )
        }

        // 2. Export Success Dialog Modal
        if (currentExportedProject != null) {
            ExportSuccessModal(
                project = currentExportedProject,
                onDismiss = onBack,
                onShare = {
                    Toast.makeText(context, "Composition exported. Ready to share!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun ImportMediaPlaceholder(
    onPickMedia: () -> Unit,
    onSampleSelect: (Uri) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add image icon",
                        tint = NeonCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Load Source Image",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select any static photograph from your camera roll or device database, and we will formulate depth fields and motion anchors automatically.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onPickMedia,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = IndigoAccent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("select_source_file_btn")
                ) {
                    Text("Select from Gallery", color = IndigoAccent, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(color = BorderColor, thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "OR START INSTANTLY WITH A PRESET",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                val samplePresets = listOf(
                    Triple("Cyber Neon", "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=400&q=80", "Cyberpunk streets of Neo-Tokyo"),
                    Triple("Cosmic Dust", "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&w=400&q=80", "Swirling gaseous purple nebula"),
                    Triple("Liquid Flow", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=400&q=80", "Elegant ambient violet waves"),
                    Triple("Sunset Peak", "https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?auto=format&fit=crop&w=400&q=80", "Golden light reflection on water")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(samplePresets) { (name, url, desc) ->
                        Card(
                            modifier = Modifier
                                .width(100.dp)
                                .clickable { onSampleSelect(Uri.parse(url)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = desc,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
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
}

@Composable
fun PreviewPanel(
    imageUri: Uri,
    motionStyle: String,
    speedMultiplier: Float,
    overlayEffect: String,
    aspectRatio: String,
    durationSeconds: Int
) {
    var isPlaying by remember { mutableStateOf(true) }

    // Derive ratio float for preview canvas boundary size
    val ratioFloat = when (aspectRatio) {
        "16:9" -> 16f / 9f
        "9:16" -> 9f / 16f
        "1:1" -> 1f
        else -> 16f / 9f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "INTERACTIVE COMPILING PLAYER",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                    contentDescription = "Toggle loop playing",
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Card Container holding animated Canvas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (ratioFloat < 1) 0.85f else ratioFloat) // box contains small ratios smoothly
                .clip(RoundedCornerShape(32.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(32.dp))
                .testTag("motion_preview_frame"),
            colors = CardDefaults.cardColors(containerColor = SlateSurface)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background image subject to coordinate transformations
                MotionRenderCanvas(
                    imageUri = imageUri,
                    motionStyle = if (isPlaying) motionStyle else "None",
                    speedMultiplier = speedMultiplier,
                    overlayEffect = overlayEffect,
                    durationSeconds = durationSeconds,
                    ratioFloat = ratioFloat
                )

                // Aspect ratio bounding guide lines (to show user crop area)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color.White.copy(alpha = 0.15f))
                )

                // Current loop time bar
                if (isPlaying) {
                    val progressTransition = rememberInfiniteTransition(label = "indicator")
                    val progressValue by progressTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween((durationSeconds * 1000 / speedMultiplier).toInt(), easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "progress"
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(progressValue)
                            .height(3.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonCyan, GoldAmber)
                                )
                            )
                    )
                }

                // AI Status Overlay Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val pulseTransition = rememberInfiniteTransition(label = "pulse")
                        val pulseAlpha by pulseTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseAlpha"
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = pulseAlpha))
                        )
                        Text(
                            text = "Ready to Animate",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MotionRenderCanvas(
    imageUri: Uri,
    motionStyle: String,
    speedMultiplier: Float,
    overlayEffect: String,
    durationSeconds: Int,
    ratioFloat: Float
) {
    // Continuous loop state
    val infiniteTransition = rememberInfiniteTransition(label = "motion")
    val cycleMs = (durationSeconds * 1000 / speedMultiplier).coerceAtLeast(100f).toInt()
    val rawProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(cycleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "raw_progress"
    )

    // Calculate transformations
    val scaleVal = when (motionStyle) {
        "Cinematic Zoom" -> 1f + (0.32f * rawProgress)
        "Panoramic Pan" -> 1.35f
        "Vertical Crane" -> 1.35f
        "Circular Orbit" -> 1.25f
        "Helix Spiral" -> 1f + (0.38f * rawProgress)
        "Time-Lapse Zoom" -> 1f + (0.42f * rawProgress)
        else -> 1.0f
    }

    val translationXVal = when (motionStyle) {
        "Panoramic Pan" -> 120f * (rawProgress - 0.5f)
        "Circular Orbit" -> (28f * Math.cos(rawProgress.toDouble() * 2.0 * Math.PI)).toFloat()
        else -> 0f
    }

    val translationYVal = when (motionStyle) {
        "Vertical Crane" -> 120f * (rawProgress - 0.5f)
        "Circular Orbit" -> (28f * Math.sin(rawProgress.toDouble() * 2.0 * Math.PI)).toFloat()
        else -> 0f
    }

    val rotationZVal = when (motionStyle) {
        "Helix Spiral" -> 14f * (rawProgress - 0.5f)
        else -> 0f
    }

    val blurRadius = if (motionStyle == "Time-Lapse Zoom") {
        // Sudden focus shifts of lens
        val focusOffset = Math.abs(rawProgress - 0.75f)
        if (focusOffset < 0.1f) (10f * (1f - (focusOffset / 0.1f))) else 0f
    } else 0f

    // Dust particles container state (completely static initialization, no state mutability)
    val particles = remember {
        List(15) {
            DustParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat(),
                size = Random.nextFloat() * 10f + 3f,
                speedX = (Random.nextFloat() - 0.5f) * 0.15f,
                speedY = -(Random.nextFloat() * 0.2f + 0.05f),
                alpha = Random.nextFloat() * 0.5f + 0.2f
            )
        }
    }

    // Pre-allocated static grain coordinate offsets to avoid heap allocation during DrawScope phase
    val grains = remember {
        List(25) {
            Offset(Random.nextFloat(), Random.nextFloat())
        }
    }

    val context = LocalContext.current
    val imageRequest = remember(imageUri) {
        ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .build()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(blurRadius.dp)
    ) {
        // Background photo subjected to transformation
        AsyncImage(
            model = imageRequest,
            contentDescription = "Animated image preview canvas",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scaleVal
                    scaleY = scaleVal
                    translationX = translationXVal
                    translationY = translationYVal
                    rotationZ = rotationZVal
                    clip = true
                }
        )

        // Overlay visuals
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw Vignette Shadow Layer
            if (overlayEffect == "Vignette" || overlayEffect == "Cinematic Grain" || overlayEffect == "Dust Particles") {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                        center = Offset(width / 2f, height / 2f),
                        radius = Math.max(width, height) * 0.7f
                    )
                )
            }

            // 2. Draw Pulsing Light Leaks (Golden / Magenta amber colors in top left corner)
            if (overlayEffect == "Light Leaks") {
                // Alpha pulses smoothly using trigonometric phase of cycle
                val leakAlpha = 0.1f + 0.25f * (0.5f + 0.5f * Math.sin(rawProgress.toDouble() * 2.0 * Math.PI)).toFloat()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldAmber.copy(alpha = leakAlpha),
                            IndigoAccent.copy(alpha = leakAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.1f, height * 0.1f),
                        radius = width * 0.85f
                    ),
                    radius = width * 0.85f
                )
            }

            // 3. Draw Dynamic Floating Dust Particles (purely stateless offset calculations)
            if (overlayEffect == "Dust Particles" || overlayEffect == "Light Leaks") {
                particles.forEach { p ->
                    // Calculate stateless position offsets driven by rawProgress
                    val currentRawX = p.initialX + p.speedX * rawProgress
                    val currentRawY = p.initialY + p.speedY * rawProgress

                    // Wrap boundaries cleanly [0.0, 1.0]
                    val xPercent = if (currentRawX < 0f) (currentRawX % 1f) + 1f else currentRawX % 1f
                    val yPercent = if (currentRawY < 0f) (currentRawY % 1f) + 1f else currentRawY % 1f

                    drawCircle(
                        color = Color.White.copy(alpha = p.alpha * (0.3f + 0.5f * rawProgress)),
                        radius = p.size,
                        center = Offset(xPercent * width, yPercent * height)
                    )
                }
            }

            // 4. Draw Cinematic Film Grain Noise
            if (overlayEffect == "Cinematic Grain") {
                // Determine a discrete frame seed index from rawProgress
                val seed = (rawProgress * 100).toInt()
                grains.forEachIndexed { index, grain ->
                    // Calculate stateless coordinate shifts derived from seed and index
                    val shiftX = ((seed + index) % 7) * 0.05f
                    val shiftY = ((seed * (index + 3)) % 11) * 0.04f
                    val rX = ((grain.x + shiftX) % 1f) * width
                    val rY = ((grain.y + shiftY) % 1f) * height
                    val sizeGrain = 3f + ((seed + index) % 3)

                    drawRect(
                        color = Color.White.copy(alpha = 0.09f),
                        topLeft = Offset(rX, rY),
                        size = androidx.compose.ui.geometry.Size(sizeGrain, sizeGrain)
                    )
                }
            }
        }
    }
}

// Model class for tracking animated floating dust on Canvas
private class DustParticle(
    val initialX: Float,
    val initialY: Float,
    val size: Float,
    val speedX: Float,
    val speedY: Float,
    val alpha: Float
)

@Composable
fun AIIntelligenceSection(
    isAnalyzing: Boolean,
    blueprintObj: JSONObject?,
    analysisError: String?,
    onTriggerAI: () -> Unit,
    selectedImageUri: Uri
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.BrightnessAuto,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI DIRECTORS STUDIO SCAN",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.weight(1f))

            if (blueprintObj == null && !isAnalyzing) {
                TextButton(
                    onClick = onTriggerAI,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Analyze Now", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (isAnalyzing) {
                    // Glowing progress sequence
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing scene layout & mapping frame vectors...",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (blueprintObj != null) {
                    // Render parsed results in stunning display chips
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = blueprintObj.optString("sceneType", "Natural Composition").uppercase(),
                                    color = NeonCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Detected Atmosphere: ${blueprintObj.optString("mood", "Calm & Deep")}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAmber.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "AI MATCH: ${blueprintObj.optString("recommendedMotionStyle", "Cinematic Zoom")}",
                                    color = GoldAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Justification Text
                        Text(
                            text = "Rationale: ${blueprintObj.optString("motionJustification", "Best framing fits.")}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        HorizontalDivider(color = BorderColor, thickness = 1.dp)

                        // Story script narration
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Spatial Narrative Draft:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = blueprintObj.optString("storyNarrative", "A gorgeous cinematic translation of standard coordinates."),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            )
                        }

                        // Bottom alerts
                        if (!analysisError.isNullOrEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = analysisError,
                                    color = GoldAmber,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else {
                    // Unanalyzed empty state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No AI blueprints generated yet.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onTriggerAI,
                            colors = ButtonDefaults.buttonColors(containerColor = SlateSurfaceVariant),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Scan Scene with Gemini AI", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomizationControlsSection(
    motionStyle: String,
    speedMultiplier: Float,
    overlayEffect: String,
    aspectRatio: String,
    durationSeconds: Int,
    onMotionStyleChanged: (String) -> Unit,
    onSpeedChanged: (Float) -> Unit,
    onOverlayEffectChanged: (String) -> Unit,
    onAspectRatioChanged: (String) -> Unit,
    onDurationChanged: (Int) -> Unit
) {
    val motionStyles = listOf(
        "Cinematic Zoom",
        "Panoramic Pan",
        "Vertical Crane",
        "Circular Orbit",
        "Helix Spiral",
        "Time-Lapse Zoom"
    )

    val overlayEffects = listOf(
        "None",
        "Cinematic Grain",
        "Dust Particles",
        "Light Leaks",
        "Vignette"
    )

    val aspectRatios = listOf(
        "16:9",
        "9:16",
        "1:1"
    )

    val durations = listOf(3, 5, 10)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "DIRECTORS CINEMATIC CONTROLS",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 1. Motion Preset Slider Row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Camera Motion Preset",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(motionStyles) { style ->
                        val isSelected = style == motionStyle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonCyan else SlateSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent else BorderColor,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onMotionStyleChanged(style) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("motion_style_$style")
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) IndigoAccent else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Overlays Option Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Epic Overlay Simulations",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(overlayEffects) { effect ->
                        val isSelected = effect == overlayEffect
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonCyan else SlateSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent else BorderColor,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onOverlayEffectChanged(effect) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("overlay_effect_$effect")
                        ) {
                            Text(
                                text = effect,
                                color = if (isSelected) IndigoAccent else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Choice layout (Speed and Duration)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Speed Choice Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Motion Tempo",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${speedMultiplier}x Speed",
                        color = NeonCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )

                    Slider(
                        value = speedMultiplier,
                        onValueChange = { scale ->
                            val nearest = when {
                                scale < 0.75f -> 0.5f
                                scale < 1.25f -> 1.0f
                                scale < 1.75f -> 1.5f
                                else -> 2.0f
                            }
                            onSpeedChanged(nearest)
                        },
                        valueRange = 0.5f..2.0f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = SlateSurfaceVariant
                        ),
                        modifier = Modifier.testTag("speed_multiplier_slider")
                    )
                }
            }

            // Duration / Aspect ratio card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Temporal Span",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durations.forEach { d ->
                            val isSelected = d == durationSeconds
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan else SlateSurfaceVariant)
                                    .clickable { onDurationChanged(d) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${d}s",
                                    color = if (isSelected) IndigoAccent else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Aspect ratio chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        aspectRatios.forEach { ar ->
                            val isSelected = ar == aspectRatio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan else SlateSurfaceVariant)
                                    .clickable { onAspectRatioChanged(ar) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ar,
                                    color = if (isSelected) IndigoAccent else TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExportProgressOverlay(
    progress: Float,
    statusText: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated glowing loading ring
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(120.dp),
                    color = NeonCyan,
                    strokeWidth = 6.dp,
                    trackColor = SlateSurface
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "INTERPOLATING MOTION VECTORS",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = statusText,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Simulated rendering timeline logger
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (progress >= 0.1f) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (progress >= 0.1f) NeonCyan else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accessing local graphic caching assets", color = if (progress >= 0.1f) TextPrimary else TextSecondary, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (progress >= 0.35f) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (progress >= 0.35f) NeonCyan else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculating neural depth field layers", color = if (progress >= 0.35f) TextPrimary else TextSecondary, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (progress >= 0.65f) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (progress >= 0.65f) NeonCyan else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Flow interpolation (Frame 32/48)", color = if (progress >= 0.65f) TextPrimary else TextSecondary, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (progress >= 1.0f) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (progress >= 1.0f) NeonCyan else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Muxing video & compiling export stream", color = if (progress >= 1.0f) TextPrimary else TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ExportSuccessModal(
    project: VideoProject,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AI Video Exported Completed!",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mini looping preview player
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (project.imageUriString != null) {
                            MotionRenderCanvas(
                                imageUri = Uri.parse(project.imageUriString),
                                motionStyle = project.motionStyle,
                                speedMultiplier = project.speedMultiplier,
                                overlayEffect = project.overlayEffect,
                                durationSeconds = project.durationSeconds,
                                ratioFloat = 1.0f
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Specs Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateSurfaceVariant)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "TITLE: ${project.title}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "RESOLUTION: 1080p Full HD (60 FPS)", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "PARAMETERS: Ratio ${project.aspectRatio} | Style ${project.motionStyle}", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "FILE SIZE: 3.4 MB | Format MP4", color = TextSecondary, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onShare,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                        border = BorderStroke(1.dp, NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Text("Back to Studio", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
