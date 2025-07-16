package com.example.tasklist.ui

import com.example.tasklist.domain.usecase.TaskUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class TaskViewModel(
    private val taskUseCases: TaskUseCases,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        coroutineScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                taskUseCases.getTasks().collect { allTasks ->
                    val filteredTasks = allTasks
                        .filter { task ->
                            // Apply completion filter
                            when (_state.value.filter) {
                                TaskFilter.All -> true
                                TaskFilter.Active -> !task.isCompleted
                                TaskFilter.Completed -> task.isCompleted
                                TaskFilter.Today -> {
                                    val today = Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                        .date
                                    task.dueDate?.let { it.toLocalDateTime(TimeZone.currentSystemDefault()).date == today } ?: false
                                }
                                TaskFilter.ThisWeek -> {
                                    val now = Clock.System.now()
                                    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    val weekEnd = today.plus(DatePeriod(days = 7))
                                    task.dueDate?.let {
                                        val taskDate = it.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                        taskDate in today..weekEnd
                                    } ?: false
                                }
                                TaskFilter.ThisMonth -> {
                                    val now = Clock.System.now()
                                    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    val monthEnd = today.plus(DatePeriod(months = 1))
                                    task.dueDate?.let {
                                        val taskDate = it.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                        taskDate in today..monthEnd
                                    } ?: false
                                }
                                is TaskFilter.Custom -> {
                                    val filter = _state.value.filter as TaskFilter.Custom
                                    if (filter.startDate != null && filter.endDate != null) {
                                        task.dueDate?.let {
                                            val taskDate = it.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                            taskDate in filter.startDate..filter.endDate
                                        } ?: false
                                    } else true
                                }
                            }
                        }
                        .filter { task ->
                            // Apply category filter
                            _state.value.selectedCategory?.let { category ->
                                task.category == category
                            } ?: true
                        }
                        .let { tasks ->
                            // Apply sorting
                            when (_state.value.sortOrder) {
                                TaskSortOrder.DateCreated -> tasks.sortedBy { it.createdAt }
                                TaskSortOrder.DateModified -> tasks.sortedBy { it.updatedAt }
                                TaskSortOrder.DueDate -> tasks.sortedBy { it.dueDate }
                                TaskSortOrder.Title -> tasks.sortedBy { it.title }
                            }
                        }
                    
                    _state.update { it.copy(
                        tasks = filteredTasks,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.AddTask -> {
                coroutineScope.launch {
                    try {
                        taskUseCases.addTask(
                            title = event.title,
                            description = event.description,
                            category = event.category,
                            tags = event.tags
                        )
                        loadTasks()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TaskEvent.UpdateTask -> {
                coroutineScope.launch {
                    try {
                        taskUseCases.updateTask(event.task)
                        loadTasks()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TaskEvent.ToggleTask -> {
                coroutineScope.launch {
                    try {
                        val task = state.value.tasks.find { it.id == event.taskId }
                        task?.let {
                            taskUseCases.updateTask(it.copy(isCompleted = !it.isCompleted))
                            loadTasks()
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TaskEvent.DeleteTask -> {
                coroutineScope.launch {
                    try {
                        taskUseCases.deleteTask(event.taskId)
                        loadTasks()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TaskEvent.SetFilter -> {
                _state.update { it.copy(filter = event.filter) }
                loadTasks()
            }
            is TaskEvent.SetSortOrder -> {
                _state.update { it.copy(sortOrder = event.sortOrder) }
                loadTasks()
            }
            is TaskEvent.SetCategory -> {
                _state.update { it.copy(selectedCategory = event.category) }
                loadTasks()
            }
        }
    }
}
