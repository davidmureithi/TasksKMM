package com.example.tasklist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasklist.ui.components.AddTaskDialog
import com.example.tasklist.ui.components.TaskFilterMenu
import com.example.tasklist.ui.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    TaskFilterMenu(
                        currentFilter = state.filter,
                        onFilterSelected = { filter ->
                            viewModel.onEvent(TaskEvent.SetFilter(filter))
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = {
                                viewModel.onEvent(TaskEvent.ToggleTask(task.id))
                            },
                            onDelete = {
                                viewModel.onEvent(TaskEvent.DeleteTask(task.id))
                            }
                        )
                    }
                }
            }

            if (showAddDialog) {
                AddTaskDialog(
                    onDismiss = { showAddDialog = false },
                    onTaskAdded = { title, description, category, tags ->
                        viewModel.onEvent(TaskEvent.AddTask(
                            title = title,
                            description = description,
                            category = category,
                            tags = tags
                        ))
                        showAddDialog = false
                    }
                )
            }

            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(error)
                }
            }
        }
    }
}
