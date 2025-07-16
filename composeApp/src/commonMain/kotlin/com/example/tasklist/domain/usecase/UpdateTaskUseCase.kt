package com.example.tasklist.domain.usecase

import com.example.tasklist.data.repository.TaskRepository
import com.example.tasklist.domain.model.Task

class UpdateTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        require(task.title.isNotBlank()) { "Title cannot be empty" }
        repository.updateTask(task)
    }
}
