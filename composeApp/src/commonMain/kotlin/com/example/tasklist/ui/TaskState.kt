package com.example.tasklist.ui

import com.example.tasklist.domain.model.Task
import kotlinx.datetime.*

data class TaskState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: TaskFilter = TaskFilter.All,
    val selectedCategory: String? = null,
    val sortOrder: TaskSortOrder = TaskSortOrder.DateCreated
)

sealed interface TaskEvent {
    data class AddTask(
        val title: String,
        val description: String? = null,
        val category: String? = null,
        val tags: List<String> = emptyList()
    ) : TaskEvent
    data class UpdateTask(val task: Task) : TaskEvent
    data class ToggleTask(val taskId: Long) : TaskEvent
    data class DeleteTask(val taskId: Long) : TaskEvent
    data class SetFilter(val filter: TaskFilter) : TaskEvent
    data class SetCategory(val category: String?) : TaskEvent
    data class SetSortOrder(val sortOrder: TaskSortOrder) : TaskEvent
}

sealed interface TaskFilter {
    data object All : TaskFilter
    data object Active : TaskFilter
    data object Completed : TaskFilter
    data object Today : TaskFilter
    data object ThisWeek : TaskFilter
    data object ThisMonth : TaskFilter
    data class Custom(val startDate: LocalDate?, val endDate: LocalDate?) : TaskFilter
}

enum class TaskSortOrder {
    DateCreated,
    DateModified,
    DueDate,
    Title
}
