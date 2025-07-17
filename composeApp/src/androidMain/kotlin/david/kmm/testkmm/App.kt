package david.kmm.testkmm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tasklist.ui.components.AddTaskDialog
import com.example.tasklist.ui.components.TaskListItem
import com.example.tasklist.ui.TaskViewModel
import com.example.tasklist.ui.TaskEvent
import com.example.tasklist.ui.TaskState
import com.example.tasklist.domain.usecase.*
import com.example.tasklist.data.repository.TaskRepository
import com.example.tasklist.db.TaskDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.tasklist.domain.model.Task
import com.example.tasklist.ui.TaskFilter
import com.example.tasklist.ui.components.predefinedCategories
import com.example.tasklist.ui.components.TaskFilterSort
import com.example.tasklist.ui.TaskSortOrder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.DarkMode
//import androidx.compose.material.icons.filled.LightMode
import com.example.tasklist.ui.theme.TaskTheme
import com.example.tasklist.data.preferences.PreferencesManager
import androidx.compose.foundation.isSystemInDarkTheme

enum class Screen {
    TASK_LIST, ADD_TASK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterSection(
    currentFilter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    selectedCategory: String?,
    onCategoryChange: (String?) -> Unit,
    totalTasks: Int,
    completedTasks: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Tasks: $completedTasks completed of $totalTasks total",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = currentFilter == TaskFilter.All,
                onClick = { onFilterChange(TaskFilter.All) },
                label = { Text("All") }
            )
            FilterChip(
                selected = currentFilter == TaskFilter.Active,
                onClick = { onFilterChange(TaskFilter.Active) },
                label = { Text("Active") }
            )
            FilterChip(
                selected = currentFilter == TaskFilter.Completed,
                onClick = { onFilterChange(TaskFilter.Completed) },
                label = { Text("Completed") }
            )
        }
        
        var expanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategory ?: "All Categories",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Categories") },
                    onClick = {
                        onCategoryChange(null)
                        expanded = false
                    }
                )
                predefinedCategories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategoryChange(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    var isDarkTheme by remember { mutableStateOf(preferencesManager.isDarkMode) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    val taskUseCases = remember {
        val driver = AndroidSqliteDriver(
            schema = TaskDatabase.Schema,
            context = context,
            name = "task.db"
        )
        val database = TaskDatabase(driver)
        val repository = TaskRepository(database)
        TaskUseCases(
            getTasks = GetTasksUseCase(repository),
            addTask = AddTaskUseCase(repository),
            updateTask = UpdateTaskUseCase(repository),
            deleteTask = DeleteTaskUseCase(repository)
        )
    }
    val viewModel = remember { TaskViewModel(taskUseCases) }
    val scope = rememberCoroutineScope()

    TaskTheme(darkTheme = isDarkTheme) {
        DisposableEffect(isDarkTheme) {
            onDispose {
                preferencesManager.isDarkMode = isDarkTheme
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddTaskDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val state by viewModel.state.collectAsState()
                if (state.isLoading) {
                    Text("Loading tasks...")
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TaskFilterSort(
                            currentFilter = state.filter,
                            onFilterChange = { filter ->
                                scope.launch {
                                    viewModel.onEvent(TaskEvent.SetFilter(filter))
                                }
                            },
                            currentSortOrder = state.sortOrder,
                            onSortOrderChange = { sortOrder ->
                                scope.launch {
                                    viewModel.onEvent(TaskEvent.SetSortOrder(sortOrder))
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        TaskFilterSection(
                            currentFilter = state.filter,
                            onFilterChange = { filter ->
                                scope.launch {
                                    viewModel.onEvent(TaskEvent.SetFilter(filter))
                                }
                            },
                            selectedCategory = state.selectedCategory,
                            onCategoryChange = { category ->
                                scope.launch {
                                    viewModel.onEvent(TaskEvent.SetCategory(category))
                                }
                            },
                            totalTasks = state.tasks.size,
                            completedTasks = state.tasks.count { it.isCompleted }
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.tasks) { task ->
                                TaskListItem(
                                    task = task,
                                    onEditClick = {
                                        taskToEdit = it
                                        showAddTaskDialog = true
                                    },
                                    onDeleteClick = {
                                        scope.launch {
                                            viewModel.onEvent(TaskEvent.DeleteTask(it.id))
                                        }
                                    },
                                    onToggleComplete = {
                                        scope.launch {
                                            viewModel.onEvent(TaskEvent.ToggleTask(it.id))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Add/Edit Task Dialog
            if (showAddTaskDialog) {
                AddTaskDialog(
                    onDismiss = {
                        showAddTaskDialog = false
                        taskToEdit = null
                    },
                    onTaskAdded = { title, description, category, tags, dueDate ->
                        scope.launch {
                            if (taskToEdit != null) {
                                viewModel.onEvent(TaskEvent.UpdateTask(
                                    taskToEdit!!.copy(
                                        title = title,
                                        description = description,
                                        category = category,
                                        tags = tags,
                                        dueDate = dueDate
                                    )
                                ))
                            } else {
                                viewModel.onEvent(TaskEvent.AddTask(
                                    title = title,
                                    description = description,
                                    category = category,
                                    tags = tags,
                                    dueDate = dueDate
                                ))
                            }
                            showAddTaskDialog = false
                            taskToEdit = null
                        }
                    },
                    initialTitle = taskToEdit?.title ?: "",
                    initialDescription = taskToEdit?.description ?: "",
                    initialCategory = taskToEdit?.category,
                    initialTags = taskToEdit?.tags ?: emptyList()
                )
            }
        }
    }
}