package com.example.tasklist.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasklist.ui.TaskFilter
import com.example.tasklist.ui.TaskSortOrder
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterSort(
    currentFilter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    currentSortOrder: TaskSortOrder,
    onSortOrderChange: (TaskSortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerMode by remember { mutableStateOf<DatePickerMode>(DatePickerMode.Start) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Date Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = currentFilter == TaskFilter.All,
                onClick = { onFilterChange(TaskFilter.All) },
                label = { Text("All") }
            )
            FilterChip(
                selected = currentFilter == TaskFilter.Today,
                onClick = { onFilterChange(TaskFilter.Today) },
                label = { Text("Today") }
            )
            FilterChip(
                selected = currentFilter == TaskFilter.ThisWeek,
                onClick = { onFilterChange(TaskFilter.ThisWeek) },
                label = { Text("This Week") }
            )
            FilterChip(
                selected = currentFilter == TaskFilter.ThisMonth,
                onClick = { onFilterChange(TaskFilter.ThisMonth) },
                label = { Text("This Month") }
            )
            FilterChip(
                selected = currentFilter is TaskFilter.Custom,
                onClick = {
                    datePickerMode = DatePickerMode.Start
                    showDatePicker = true
                },
                label = { Text("Custom Range") }
            )
        }

        // Sort Order Menu
        var showSortMenu by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = showSortMenu,
            onExpandedChange = { showSortMenu = it }
        ) {
            OutlinedTextField(
                value = when(currentSortOrder) {
                    TaskSortOrder.DateCreated -> "Date Created"
                    TaskSortOrder.DateModified -> "Date Modified"
                    TaskSortOrder.DueDate -> "Due Date"
                    TaskSortOrder.Title -> "Title"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Sort By") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSortMenu) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                TaskSortOrder.values().forEach { sortOrder ->
                    DropdownMenuItem(
                        text = {
                            Text(when(sortOrder) {
                                TaskSortOrder.DateCreated -> "Date Created"
                                TaskSortOrder.DateModified -> "Date Modified"
                                TaskSortOrder.DueDate -> "Due Date"
                                TaskSortOrder.Title -> "Title"
                            })
                        },
                        onClick = {
                            onSortOrderChange(sortOrder)
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        // Date Range Display (if custom range selected)
        if (currentFilter is TaskFilter.Custom) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        datePickerMode = DatePickerMode.Start
                        showDatePicker = true
                    }
                ) {
                    Text(startDate?.toString() ?: "Start Date")
                }
                OutlinedButton(
                    onClick = {
                        datePickerMode = DatePickerMode.End
                        showDatePicker = true
                    }
                ) {
                    Text(endDate?.toString() ?: "End Date")
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            SimpleDatePicker(
                onDateSelected = { date ->
                    when (datePickerMode) {
                        DatePickerMode.Start -> {
                            startDate = date
                            if (endDate == null) {
                                datePickerMode = DatePickerMode.End
                            } else {
                                showDatePicker = false
                                onFilterChange(TaskFilter.Custom(startDate, endDate))
                            }
                        }
                        DatePickerMode.End -> {
                            endDate = date
                            showDatePicker = false
                            if (startDate != null) {
                                onFilterChange(TaskFilter.Custom(startDate, endDate))
                            }
                        }
                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

private enum class DatePickerMode {
    Start, End
}
