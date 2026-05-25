package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "video_projects")
data class VideoProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val imageUriString: String?,
    val motionStyle: String,
    val speedMultiplier: Float = 1.0f,
    val overlayEffect: String = "None",
    val aspectRatio: String = "16:9",
    val durationSeconds: Int = 5,
    val aiAnalysisJson: String? = null,
    val isExported: Boolean = false,
    val exportPath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
