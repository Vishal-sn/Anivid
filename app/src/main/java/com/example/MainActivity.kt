package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.VideoProjectViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: VideoProjectViewModel = viewModel()
                val navController = rememberNavController()

                val projects by viewModel.allProjects.collectAsState()
                
                val selectedImageUri by viewModel.selectedImageUri.collectAsState()
                val motionStyle by viewModel.motionStyle.collectAsState()
                val speedMultiplier by viewModel.speedMultiplier.collectAsState()
                val overlayEffect by viewModel.overlayEffect.collectAsState()
                val aspectRatio by viewModel.aspectRatio.collectAsState()
                val durationSeconds by viewModel.durationSeconds.collectAsState()
                val projectTitle by viewModel.projectTitle.collectAsState()
                
                val isAnalyzing by viewModel.isAnalyzing.collectAsState()
                val aiBlueprint by viewModel.aiBlueprint.collectAsState()
                val analysisError by viewModel.analysisError.collectAsState()
                
                val isExporting by viewModel.isExporting.collectAsState()
                val exportProgress by viewModel.exportProgress.collectAsState()
                val exportStatusText by viewModel.exportStatusText.collectAsState()
                val currentExportedProject by viewModel.currentExportedProject.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = "dashboard",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("dashboard") {
                        DashboardScreen(
                            projects = projects,
                            onProjectSelect = { project ->
                                // Prepopulate editor parameters
                                viewModel.selectImage(project.imageUriString?.let { android.net.Uri.parse(it) })
                                viewModel.updateMotionStyle(project.motionStyle)
                                viewModel.updateSpeed(project.speedMultiplier)
                                viewModel.updateOverlayEffect(project.overlayEffect)
                                viewModel.updateAspectRatio(project.aspectRatio)
                                viewModel.updateDuration(project.durationSeconds)
                                viewModel.updateTitle(project.title)
                                navController.navigate("workspace")
                            },
                            onDeleteProject = { id ->
                                viewModel.deleteProject(id)
                            },
                            onStartNewProject = {
                                // Reset editor parameters for fresh creation
                                viewModel.selectImage(null)
                                viewModel.updateMotionStyle("Cinematic Zoom")
                                viewModel.updateSpeed(1.0f)
                                viewModel.updateOverlayEffect("None")
                                viewModel.updateAspectRatio("16:9")
                                viewModel.updateDuration(5)
                                viewModel.updateTitle("New Animation")
                                navController.navigate("workspace")
                            }
                        )
                    }

                    composable("workspace") {
                        WorkspaceScreen(
                            selectedImageUri = selectedImageUri,
                            motionStyle = motionStyle,
                            speedMultiplier = speedMultiplier,
                            overlayEffect = overlayEffect,
                            aspectRatio = aspectRatio,
                            durationSeconds = durationSeconds,
                            projectTitle = projectTitle,
                            isAnalyzing = isAnalyzing,
                            aiBlueprint = aiBlueprint,
                            analysisError = analysisError,
                            isExporting = isExporting,
                            exportProgress = exportProgress,
                            exportStatusText = exportStatusText,
                            currentExportedProject = currentExportedProject,
                            onImageSelected = { uri -> viewModel.selectImage(uri) },
                            onMotionStyleChanged = { style -> viewModel.updateMotionStyle(style) },
                            onSpeedChanged = { speed -> viewModel.updateSpeed(speed) },
                            onOverlayEffectChanged = { effect -> viewModel.updateOverlayEffect(effect) },
                            onAspectRatioChanged = { ratio -> viewModel.updateAspectRatio(ratio) },
                            onDurationChanged = { secs -> viewModel.updateDuration(secs) },
                            onTitleChanged = { title -> viewModel.updateTitle(title) },
                            onTriggerAI = { viewModel.triggerAIAnalysis() },
                            onExportVideo = { viewModel.compileAndExportVideo() },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
