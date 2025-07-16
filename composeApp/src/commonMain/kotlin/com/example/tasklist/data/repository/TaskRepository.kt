package com.example.tasklist.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.tasklist.db.TaskDatabase
import com.example.tasklist.domain.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import com.example.tasklist.data.mappers.toDomainModel

class TaskRepository(
    private val database: TaskDatabase
) {
    private val queries = database.taskQueries
    
    fun getAllTasks(): Flow<List<Task>> =
        queries.getAllTasks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { tasks -> tasks.map { it.toDomainModel() } }
            
    fun getIncompleteTasks(): Flow<List<Task>> =
        queries.getIncompleteTasks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { tasks -> tasks.map { it.toDomainModel() } }
            
    fun getTasksByCategory(category: String): Flow<List<Task>> =
        queries.getTasksByCategory(category)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { tasks -> tasks.map { it.toDomainModel() } }
    
    suspend fun addTask(
        title: String,
        description: String? = null,
        dueDate: Instant? = null,
        category: String? = null,
        tags: List<String> = emptyList()
    ) {
        queries.insertTask(
            title = title,
            description = description,
            dueDate = dueDate?.toEpochMilliseconds(),
            isCompleted = false,
            category = category,
            tags = if (tags.isEmpty()) null else tags.joinToString(","),
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = null
        )
    }
    
    suspend fun updateTask(task: Task) {
        queries.updateTask(
            title = task.title,
            description = task.description,
            dueDate = task.dueDate?.toEpochMilliseconds(),
            isCompleted = task.isCompleted,
            category = task.category,
            tags = if (task.tags.isEmpty()) null else task.tags.joinToString(","),
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            id = task.id
        )
    }
    
    suspend fun deleteTask(taskId: Long) {
        queries.deleteTask(taskId)
    }
    
    suspend fun toggleTaskCompletion(taskId: Long) {
        val originalTask = queries.getTaskById(taskId)
            .executeAsOne()
            .toDomainModel()
        val updatedTask = originalTask.copy(isCompleted = !originalTask.isCompleted)
        updateTask(updatedTask)
    }
}
