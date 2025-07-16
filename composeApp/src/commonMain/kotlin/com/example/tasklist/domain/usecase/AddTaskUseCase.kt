package com.example.tasklist.domain.usecase

import com.example.tasklist.data.repository.TaskRepository
import com.example.tasklist.domain.model.Task
import kotlinx.datetime.Instant

class AddTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String? = null,
        dueDate: Instant? = null,
        category: String? = null,
        tags: List<String> = emptyList()
    ) {
        require(title.isNotBlank()) { "Title cannot be empty" }
        repository.addTask(title, description, dueDate, category, tags)
    }
}
