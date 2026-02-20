package com.example.assignmenttracker.presentation.detail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignmentId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AssignmentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignment Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Title
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } }
            )

            // Description
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // DUE DATE
            val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            val dueDate = uiState.dueDate

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val dp = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            viewModel.updateDueDate(LocalDate.of(year, month + 1, dayOfMonth))
                        },
                        dueDate.year,
                        dueDate.monthValue - 1,
                        dueDate.dayOfMonth
                    )
                    dp.show()
                }
            ) {
                Text("Due Date: ${dueDate.format(dateFormatter)}")
            }

            // DUE TIME
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val dueTime = uiState.dueTime

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val tp = TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            viewModel.updateDueTime(LocalTime.of(hour, minute))
                        },
                        dueTime.hour,
                        dueTime.minute,
                        true
                    )
                    tp.show()
                }
            ) {
                Text("Due Time: ${dueTime.format(timeFormatter)}")
            }

            // -----------------------------
            // PRIORITY — FIXED (2 ROWS)
            // -----------------------------
            Text("Priority", style = MaterialTheme.typography.titleMedium)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Row 1: LOW, MEDIUM, HIGH
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH).forEach { priority ->
                        FilterChip(
                            selected = uiState.priority == priority,
                            onClick = { viewModel.updatePriority(priority) },
                            label = { Text(priority.name) }
                        )
                    }
                }

                // Row 2: URGENT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FilterChip(
                        selected = uiState.priority == Priority.URGENT,
                        onClick = { viewModel.updatePriority(Priority.URGENT) },
                        label = { Text("URGENT") }
                    )
                }
            }

            // -----------------------------
            // STATUS — FIXED (2 ROWS)
            // -----------------------------
            Text("Status", style = MaterialTheme.typography.titleMedium)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Row 1: NOT STARTED, IN PROGRESS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(Status.NOT_STARTED, Status.IN_PROGRESS).forEach { status ->
                        FilterChip(
                            selected = uiState.status == status,
                            onClick = { viewModel.updateStatus(status) },
                            label = { Text(status.name.replace("_", " ")) }
                        )
                    }
                }

                // Row 2: DONE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FilterChip(
                        selected = uiState.status == Status.DONE,
                        onClick = { viewModel.updateStatus(Status.DONE) },
                        label = { Text("DONE") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = viewModel::saveAssignment,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text("Save Assignment")
            }
        }
    }
}
