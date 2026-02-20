package com.example.assignmenttracker.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.assignmenttracker.domain.model.Status

@Composable
fun StatusChip(
    status: Status,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (status) {
        Status.NOT_STARTED -> Color(0xFF9E9E9E) to "Not Started"
        Status.IN_PROGRESS -> Color(0xFF2196F3) to "In Progress"
        Status.DONE -> Color(0xFF4CAF50) to "Done"
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
