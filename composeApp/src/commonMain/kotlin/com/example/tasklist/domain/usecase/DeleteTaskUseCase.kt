package com.example.tasklist.domain.usecase

import com.example.tasklist.data.repository.TaskRepository

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) {
        repository.deleteTask(taskId)
    }
}
