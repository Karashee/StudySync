package com.example.assignmenttracker.presentation.list
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignmenttracker.presentation.components.AssignmentCard
import com.example.assignmenttracker.presentation.components.ErrorMessage
import com.example.assignmenttracker.presentation.components.FilterChipGroup
import com.example.assignmenttracker.presentation.components.LoadingIndicator
import com.example.assignmenttracker.presentation.list.AssignmentListViewModel
import com.example.assignmenttracker.presentation.list.AssignmentListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToBoard: () -> Unit = {},
    viewModel: AssignmentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.statusFilter.collectAsStateWithLifecycle()
    val selectedPriority by viewModel.priorityFilter.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (userName.isNotBlank()) {
                        Text(
                            text = "Hey $userName,\nAssignments:",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Start,
                            lineHeight = 20.sp // <- use sp, not dp
                        )
                    } else {
                        Text(
                            text = "Assignments:",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 20.sp
                        )
                    }
                }
            )


        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add assignment")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search assignments...") },
                singleLine = true
            )

            // Filter chips
            FilterChipGroup(
                selectedStatus = selectedStatus,
                selectedPriority = selectedPriority,
                onStatusSelected = viewModel::updateStatusFilter,
                onPrioritySelected = viewModel::updatePriorityFilter,
                onClearFilters = {
                    viewModel.updateStatusFilter(null)
                    viewModel.updatePriorityFilter(null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = uiState) {
                is AssignmentListUiState.Loading -> LoadingIndicator()
                is AssignmentListUiState.Error -> ErrorMessage(state.message)
                is AssignmentListUiState.Success -> {
                    if (state.assignments.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No assignments found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.assignments,
                                key = { it.id }
                            ) { assignment ->

                                val dismissState = rememberDismissState(
                                    confirmValueChange = {
                                        if (it == DismissValue.DismissedToEnd) {
                                            viewModel.deleteAssignment(assignment)
                                        }
                                        true
                                    }
                                )

                                SwipeToDismiss(
                                    state = dismissState,
                                    directions = setOf(DismissDirection.StartToEnd),
                                    background = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.Red
                                            )
                                        }
                                    },
                                    dismissContent = {
                                        AssignmentCard(
                                            assignment = assignment,
                                            onClick = { onNavigateToDetail(assignment.id) }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
