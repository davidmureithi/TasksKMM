package com.example.tasklist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Composable
fun SimpleDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var year by remember { mutableStateOf(selectedDate?.year ?: today.year) }
    var month by remember { mutableStateOf(selectedDate?.monthNumber ?: today.monthNumber) }
    var day by remember { mutableStateOf(selectedDate?.dayOfMonth ?: today.dayOfMonth) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick Due Date") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                // Year Picker
                var yearMenuExpanded by remember { mutableStateOf(false) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Year: $year", modifier = Modifier.padding(end = 8.dp))
                    Button(onClick = { yearMenuExpanded = true }) { Text("Select") }
                    DropdownMenu(
                        expanded = yearMenuExpanded,
                        onDismissRequest = { yearMenuExpanded = false }
                    ) {
                        (today.year..today.year+5).forEach { y ->
                            DropdownMenuItem(
                                onClick = {
                                    year = y
                                    yearMenuExpanded = false
                                },
                                text = { Text(y.toString()) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Month Picker
                var monthMenuExpanded by remember { mutableStateOf(false) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Month: $month", modifier = Modifier.padding(end = 8.dp))
                    Button(onClick = { monthMenuExpanded = true }) { Text("Select") }
                    DropdownMenu(
                        expanded = monthMenuExpanded,
                        onDismissRequest = { monthMenuExpanded = false }
                    ) {
                        (1..12).forEach { m ->
                            DropdownMenuItem(
                                onClick = {
                                    month = m
                                    monthMenuExpanded = false
                                },
                                text = { Text(m.toString()) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Day Picker
                var dayMenuExpanded by remember { mutableStateOf(false) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Day: $day", modifier = Modifier.padding(end = 8.dp))
                    Button(onClick = { dayMenuExpanded = true }) { Text("Select") }
                    DropdownMenu(
                        expanded = dayMenuExpanded,
                        onDismissRequest = { dayMenuExpanded = false }
                    ) {
                        (1..31).forEach { d ->
                            DropdownMenuItem(
                                onClick = {
                                    day = d
                                    dayMenuExpanded = false
                                },
                                text = { Text(d.toString()) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDateSelected(LocalDate(year, month, day))
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (title: String, description: String?, category: String?, tags: List<String>, dueDate: Instant?) -> Unit,
    initialTitle: String = "",
    initialDescription: String = "",
    initialCategory: String? = null,
    initialTags: List<String> = emptyList(),
    initialDueDate: Instant? = null
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var category by remember { mutableStateOf(initialCategory) }
    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(initialTags) }
    var dueDateText by remember { mutableStateOf(initialDueDate?.toString() ?: "") }
    var dueDatePickerVisible by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf(initialDueDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.date) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "Add Task" else "Edit Task") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                
                CategorySelect(
                    selectedCategory = category,
                    onCategorySelected = { category = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("Add Tags (press Enter)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                        .onKeyEvent { event ->
                            if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                                if (tagInput.isNotBlank()) {
                                    tags = tags + tagInput.trim()
                                    tagInput = ""
                                }
                                true
                            } else {
                                false
                            }
                        }
                )
                
                if (tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        tags.forEach { tag ->
                            AssistChip(
                                onClick = { tags = tags - tag },
                                label = { Text(tag) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove tag"
                                    )
                                }
                            )
                        }
                    }
                }

                TextButton(onClick = { dueDatePickerVisible = true }) {
                    Text(
    if (dueDate != null)
        "Due Date: " + dueDate.toString()
    else
        "Pick Due Date"
)
                }
                if (dueDatePickerVisible) {
                    SimpleDatePicker(
                        selectedDate = dueDate,
                        onDateSelected = { selectedDate ->
                            dueDate = selectedDate
                        },
                        onDismiss = { dueDatePickerVisible = false }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskAdded(
                            title,
                            description.takeIf { it.isNotBlank() },
                            category,
                            tags,
                            dueDate?.atStartOfDayIn(TimeZone.currentSystemDefault())
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(if (initialTitle.isEmpty()) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
