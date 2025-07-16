package com.example.tasklist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePicker(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year) }
    var selectedMonth by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month.number) }
    var selectedDay by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Year picker
                OutlinedTextField(
                    value = selectedYear.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { year ->
                            if (year in 1900..9999) {
                                selectedYear = year
                            }
                        }
                    },
                    label = { Text("Year") }
                )

                // Month picker
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    OutlinedTextField(
                        value = Month(selectedMonth).toString(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor()
                    )
                }

                // Day picker
                OutlinedTextField(
                    value = selectedDay.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { day ->
                            if (day in 1..31) {
                                selectedDay = day
                            }
                        }
                    },
                    label = { Text("Day") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val date = LocalDate(selectedYear, selectedMonth, selectedDay)
                        onDateSelected(date)
                    } catch (e: Exception) {
                        // Invalid date, ignore
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
