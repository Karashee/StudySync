package com.example.assignmenttracker.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.assignmenttracker.domain.model.Priority

@Composable
fun PriorityChip(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (priority) {
        Priority.LOW -> Color(0xFF4CAF50) to "Low"
        Priority.MEDIUM -> Color(0xFFFF9800) to "Medium"
        Priority.HIGH -> Color(0xFFFF5722) to "High"
        Priority.URGENT -> Color(0xFFF44336) to "Urgent"
    }
    
    AssistChip(
        onClick = { },
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.2f),
            labelColor = color
        ),
        modifier = modifier
    )
}
