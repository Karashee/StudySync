package com.example.assignmenttracker.presentation.board

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.model.Status
import com.example.assignmenttracker.presentation.components.AssignmentCard

@Composable
fun BoardColumn(
    status: Status,
    assignments: List<Assignment>,
    onMoveToNextStatus: (Assignment) -> Unit,
    modifier: Modifier = Modifier // Accept Modifier from parent
) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        // Column title
        Text(
            text = when (status) {
                Status.NOT_STARTED -> "Not Started"
                Status.IN_PROGRESS -> "In Progress"
                Status.DONE -> "Done"
            },
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List assignments
        assignments.forEach { assignment ->
            AssignmentCard(
                assignment = assignment,
                onClick = { onMoveToNextStatus(assignment) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
