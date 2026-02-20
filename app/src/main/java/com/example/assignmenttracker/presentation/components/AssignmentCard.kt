@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.assignmenttracker.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignmenttracker.data.local.entity.Assignment
// Make sure the imports match the actual location of your composables
import com.example.assignmenttracker.presentation.components.PriorityChip
import com.example.assignmenttracker.presentation.components.StatusChip

@Composable
fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = assignment.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityChip(priority = assignment.priority)
                StatusChip(status = assignment.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Due: ${assignment.dueDate} ${assignment.dueTime}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
