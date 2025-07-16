package com.example.tasklist.domain.usecase

import com.example.tasklist.data.repository.TaskRepository
import com.example.tasklist.domain.model.Task
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(filter: TaskFilter = TaskFilter.All): Flow<List<Task>> =
        when (filter) {
            TaskFilter.All -> repository.getAllTasks()
            TaskFilter.Incomplete -> repository.getIncompleteTasks()
            is TaskFilter.ByCategory -> repository.getTasksByCategory(filter.category)
        }
}

sealed class TaskFilter {
    data object All : TaskFilter()
    data object Incomplete : TaskFilter()
    data class ByCategory(val category: String) : TaskFilter()
}
