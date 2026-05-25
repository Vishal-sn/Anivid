package com.example.data.repository

import com.example.data.dao.VideoProjectDao
import com.example.data.entity.VideoProject
import kotlinx.coroutines.flow.Flow

class VideoProjectRepository(private val videoProjectDao: VideoProjectDao) {
    val allProjects: Flow<List<VideoProject>> = videoProjectDao.getAllProjects()

    suspend fun getProjectById(id: Int): VideoProject? {
        return videoProjectDao.getProjectById(id)
    }

    suspend fun insertProject(project: VideoProject): Long {
        return videoProjectDao.insertProject(project)
    }

    suspend fun updateProject(project: VideoProject) {
        videoProjectDao.updateProject(project)
    }

    suspend fun deleteProject(project: VideoProject) {
        videoProjectDao.deleteProject(project)
    }

    suspend fun deleteProjectById(id: Int) {
        videoProjectDao.deleteProjectById(id)
    }
}
