package com.example.assignmenttracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(
    selectedStatus: Status?,
    selectedPriority: Priority?,
    onStatusSelected: (Status?) -> Unit,
    onPrioritySelected: (Priority?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters = selectedStatus != null || selectedPriority != null

    Column(modifier = modifier) {
        // Status filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(Status.values()) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = {
                            onStatusSelected(if (selectedStatus == status) null else status)
                        },
                        label = { Text(status.name.replace("_", " ")) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Priority filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Priority:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(Priority.values()) { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = {
                            onPrioritySelected(if (selectedPriority == priority) null else priority)
                        },
                        label = { Text(priority.name) }
                    )
                }
            }
        }

        // Clear filters button
        if (hasActiveFilters) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onClearFilters) {
                    Text("Clear Filters")
                }
            }
        }
    }
}
