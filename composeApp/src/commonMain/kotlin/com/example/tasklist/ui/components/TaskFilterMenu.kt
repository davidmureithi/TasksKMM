package com.example.tasklist.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.tasklist.ui.TaskFilter

@Composable
fun TaskFilterMenu(
    currentFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Filter tasks")
    }
    
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
//        TaskFilter.values().forEach { filter ->
//            DropdownMenuItem(
//                text = { Text(filter.toString()) },
//                onClick = {
//                    onFilterSelected(filter)
//                    expanded = false
//                }
//            )
//        }
    }
}
