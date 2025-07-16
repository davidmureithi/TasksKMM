package com.example.tasklist.data.mappers

import com.example.tasklist.db.Task as DbTask
import com.example.tasklist.domain.model.Task
import kotlinx.datetime.Instant

fun DbTask.toDomainModel(): Task = Task(
    id = id,
    title = title,
    description = description,
    dueDate = dueDate?.let { Instant.fromEpochMilliseconds(it) },
    isCompleted = isCompleted,
    category = category,
    tags = tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = updatedAt?.let { Instant.fromEpochMilliseconds(it) }
)
