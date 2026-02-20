@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.assignmenttracker.presentation.board

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.assignmenttracker.domain.model.Status

@Composable
fun BoardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: BoardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Board") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        when (uiState) {
            is BoardUiState.Loading -> {
                // TODO: loading UI
            }

            is BoardUiState.Error -> {
                // TODO: error UI
            }

            is BoardUiState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    BoardColumn(
                        status = Status.NOT_STARTED,
                        assignments = uiState.notStarted,
                        onMoveToNextStatus = { viewModel.moveToNextStatus(it) },
                        modifier = Modifier.weight(1f)
                    )

                    BoardColumn(
                        status = Status.IN_PROGRESS,
                        assignments = uiState.inProgress,
                        onMoveToNextStatus = { viewModel.moveToNextStatus(it) },
                        modifier = Modifier.weight(1f)
                    )

                    BoardColumn(
                        status = Status.DONE,
                        assignments = uiState.done,
                        onMoveToNextStatus = { viewModel.moveToNextStatus(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
