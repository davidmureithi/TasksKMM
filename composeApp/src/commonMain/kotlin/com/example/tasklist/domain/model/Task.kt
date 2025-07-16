package com.example.tasklist.domain.model

import kotlinx.datetime.Instant

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val dueDate: Instant?,
    val isCompleted: Boolean,
    val category: String?,
    val tags: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant?
)
